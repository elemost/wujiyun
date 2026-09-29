package com.wuji.service.service;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.model.domain.FormMongoDbExportDomain;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.vo.FormImportCheckResultVO;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public interface FormDataService {
    String fieldType();

    /**
     * 创建数据时处理
     *
     * @param instValue
     * @param formConfigCommon
     * @param info
     * @param formSubmitCheck
     */
    void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                         FormSubmitCheck formSubmitCheck);

    /**
     * 修改数据时处理
     *
     * @param instValue
     * @param formConfigCommon
     * @param info
     * @param formSubmitCheck
     */
    void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                         FormSubmitCheck formSubmitCheck);

    /**
     * 返回列表表单的字段处理
     *
     * @param lowcodeDataList
     * @param formConfigCommon
     * @param info
     * @param systemAllDataVO
     */
    void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                         SystemAllDataVO systemAllDataVO);

    void dealDetailedReturn(MongodbSearchField mongodbSearchField, List<JSONObject> jsonObjects,SystemAllDataVO systemAllDataVO);

    /**
     * 导入处理数据
     *
     * @param formConfigCommon
     * @param value
     * @param importCheck
     * @return
     */
    FormImportCheckResultVO dealWhileImport(FormConfigCommon formConfigCommon, Object value,
                                            SystemAllDataNameVO importCheck);

    /**
     * 导出时处理数据
     *
     * @param column
     * @param row
     * @param jsonObject
     * @param formMongoDbExportDomain
     * @param systemAllDataVO
     */
    void dealWhileExport(int column, Row row, JSONObject jsonObject, FormMongoDbExportDomain formMongoDbExportDomain,
                         SystemAllDataVO systemAllDataVO);


    /**
     * 数据同步到本地
     *
     * @param formExtraFunctionSync
     * @param value
     * @param importCheck
     * @return
     */
    FormSyncCheckResultVO dealWhileSync(FormExtraFunctionSync formExtraFunctionSync, Object value,
                                        SystemAllDataNameVO importCheck);

    /**
     * 发送数据转换
     *
     * @param formExtraFunctionSync
     * @param instValue
     * @param systemAllData
     * @param sendJson
     */
    void dealWhileSend(FormExtraFunctionSync formExtraFunctionSync, JSONObject instValue, SystemAllDataVO systemAllData,
                       JSONObject sendJson);

    /**
     * 自定义模板
     *
     * @param instValue
     * @param formConfigCommon
     * @param formId
     * @param systemAllDataVO
     */
    void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,SystemAllDataVO systemAllDataVO);

    /**
     * 获取所有configlist
     *
     * @param formConfigCommon
     * @param allFormConfigList
     * @param needSubForm
     */
    void getAllConfig(FormConfigCommon formConfigCommon, List<FormConfigCommon> allFormConfigList, Boolean needSubForm);

    /**
     * 生成模板
     *
     * @param jsonObject
     * @param formConfigCommon
     * @param current
     * @param currentDept
     * @param needTrans
     */
    void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                              List<FormDept> currentDept, Boolean needTrans);


    String transValue(List<Object> values, String fieldId, String fieldType, SystemAllDataVO systemAllDataVO);

}
