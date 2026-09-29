package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.wuji.common.express.FormulaRunner;
import com.wuji.service.enums.DataStreamCalculateFnEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateNode;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FormDataStreamCalculateExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Override
    public String nodeType() {
        return "calculate";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        Object functionValue = null;
        DataStreamCalculateNode dataStreamCalculateNode = (DataStreamCalculateNode) dataStreamCommon;
        if ("formula".equals(dataStreamCalculateNode.getCalculateType())) {
            DefaultContext<String, Object> context = new DefaultContext<>();
            Map<String, DataStreamQuoteField> valMap = dataStreamCalculateNode.getValMap();
            if (valMap != null) {
                valMap.forEach((key, val) -> {
                    Object value = MongoDbDataTransUtils.getValueExistMore(nodeIdMap, val, false);
                    context.put(key, value);
                });
            }
            FormulaRunner formulaRunner = new FormulaRunner();
            try {
                functionValue = formulaRunner.execute(dataStreamCalculateNode.getFormula(), context, null, true, false);
            } catch (Exception e) {
                log.error("公式计算错误", e);
                String message = e.getCause().getMessage();
                ServiceResultCode error = ServiceResultCode.DATA_STREAM_CALCULATE;
                executeWhileNull(formDataStreamTrigger, dataStreamCommon, String.format(error.getMessage(), message),
                        dataStreamCalculateNode, nodeIdMap);
            }
            try {
                functionValue = MongoDbDataTransUtils.checkAndDealValue(dataStreamCalculateNode, functionValue);
            } catch (Exception e) {
                executeWhileNull(formDataStreamTrigger, dataStreamCommon, e.getMessage(), dataStreamCalculateNode,
                        nodeIdMap);
            }
        } else {
            DataStreamCalculateFnEnum fn = DataStreamCalculateFnEnum.valueOf(dataStreamCalculateNode.getFn());
            Object valueExistMore =
                    MongoDbDataTransUtils.getValueExistMore(nodeIdMap, dataStreamCalculateNode.getQuoteField(),
                            fn.getExistNull());
            List<Double> doubles = JSONArray.parseArray(JSONObject.toJSONString(valueExistMore), Double.class);
            if (DataStreamCalculateFnEnum.MAX.name().equalsIgnoreCase(dataStreamCalculateNode.getFn())) {
                functionValue = doubles.stream().mapToDouble(c -> c).max().orElse(0);
            } else if (DataStreamCalculateFnEnum.MIN.name().equalsIgnoreCase(dataStreamCalculateNode.getFn())) {
                functionValue = doubles.stream().mapToDouble(c -> c).min().orElse(0);
            } else if (DataStreamCalculateFnEnum.COUNT.name().equalsIgnoreCase(dataStreamCalculateNode.getFn())) {
                functionValue = doubles.size();
            } else if (DataStreamCalculateFnEnum.SUM.name().equalsIgnoreCase(dataStreamCalculateNode.getFn())) {
                functionValue = doubles.stream().mapToDouble(c -> c).sum();
            } else if (DataStreamCalculateFnEnum.AVERAGE.name().equalsIgnoreCase(dataStreamCalculateNode.getFn())) {
                functionValue = doubles.stream().mapToDouble(c -> c).average().orElse(0);
                functionValue = new BigDecimal(functionValue.toString()).setScale(4, RoundingMode.HALF_UP);
            }
        }

        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamCalculateNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setValue(functionValue);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon, functionValue);

        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    private void executeWhileNull(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                                  String e, DataStreamCalculateNode dataStreamCalculateNode,
                                  Map<Long, DataStreamCalculateVO> nodeIdMap) {
        insertLogError(formDataStreamTrigger, dataStreamCommon, e);
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamCalculateNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setValue(null);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }

    private void insertLog(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                           Object functionValue) {
        insertLog(formDataStreamTrigger, dataStreamCommon, functionValue, null, "success");
    }

    private void insertLogError(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                                String result) {
        insertLog(formDataStreamTrigger, dataStreamCommon, result, null, "error");
    }
}
