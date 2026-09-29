package com.wuji.open.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.model.vo.SystemAllDataNameVO;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.AdminCommonService;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.open.converter.AbstractFormOpenConverter;
import com.wuji.open.enums.OpenResultCode;
import com.wuji.open.exception.OpenException;
import com.wuji.open.model.request.FormDataQueryRequest;
import com.wuji.open.model.request.FormSyncOpenRequest;
import com.wuji.open.model.vo.FormDataSyncVO;
import com.wuji.open.service.FormOpenService;
import com.wuji.platform.model.vo.SyncMappingVO;
import com.wuji.platform.service.SyncMappingService;
import com.wuji.service.context.FormDataContext;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormExtraFunctionSync;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.vo.FormSyncCheckResultVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormDataService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormOpenServiceImpl implements FormOpenService {

    @Autowired
    private FormService formService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private FormDataContext formDataContext;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private SyncMappingService syncMappingService;

    @Autowired
    private AdminCommonService adminCommonService;

    @Override
    public FormDataSyncVO insert(FormSyncOpenRequest formSyncOpenRequest) {
        FormVO formVO = formService.info(formSyncOpenRequest.getFormId(), formSyncOpenRequest.getApplicationId());
        JSONObject instValue = checkAndGetInstValue(formSyncOpenRequest);
        FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
        formInsertDataRequest.setInstValue(instValue);
        formInsertDataRequest.setFormId(formVO.getId());
        formInsertDataRequest.setVersion(formVO.getVersion());
        formInsertDataRequest.setUuid(ObjectId.getGuid());
        if (formSyncOpenRequest.getStartWorkflow()) {
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        } else {
            formInsertDataRequest.setStatus(FormDataStatusEnum.DRAFT.name());
        }
        formInsertDataRequest.setApplicationId(formSyncOpenRequest.getApplicationId());
        formInsertDataRequest.setDataStreamTrigger(formSyncOpenRequest.getStartTrigger());
        formMongoDbService.insertData(formInsertDataRequest);
        FormDataSyncVO formDataSyncVO = new FormDataSyncVO();
        formDataSyncVO.setUuid(formInsertDataRequest.getUuid());
        JSONObject jsonObject = returnValue(formSyncOpenRequest, formInsertDataRequest.getUuid());
        formDataSyncVO.setInstValue(jsonObject);
        return formDataSyncVO;
    }

    private JSONObject returnValue(FormSyncOpenRequest formSyncOpenRequest, String uuid) {
        SyncMappingVO syncMappingVO =
                syncMappingService.info(formSyncOpenRequest.getApplicationId(), formSyncOpenRequest.getFormId());
        if (syncMappingVO == null) {
            throw new OpenException(OpenResultCode.FORM_MAPPING_NOT_EXIST);
        }
        List<FormExtraFunctionSync> syncList =
                JSONObject.parseArray(syncMappingVO.getMappingConfig(), FormExtraFunctionSync.class);
        LowcodeDataDomain info =
                formMongoDbService.info(uuid, formSyncOpenRequest.getFormId(), formSyncOpenRequest.getApplicationId());
        JSONObject sendJson = new JSONObject();
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
            if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                continue;
            }
            FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
            if (formDataService != null) {
                formDataService.dealWhileSend(formExtraFunctionSync, info.getInstValue(), systemAllData, sendJson);
            } else {
                sendJson.put(formExtraFunctionSync.getMappingField(),
                        info.getInstValue().get(formExtraFunctionSync.getName()));
            }
        }
        return sendJson;
    }

    @Override
    public FormDataSyncVO update(FormSyncOpenRequest formSyncOpenRequest) {
        LowcodeDataDomain info = formMongoDbService.info(formSyncOpenRequest.getUuid(), formSyncOpenRequest.getFormId(),
                formSyncOpenRequest.getApplicationId());
        if (info == null) {
            throw new OpenException(OpenResultCode.DATA_NOT_EXIST);
        }
        FormVO formVO = formService.info(formSyncOpenRequest.getFormId(), formSyncOpenRequest.getApplicationId());
        JSONObject instValue = checkAndGetInstValue(formSyncOpenRequest);
        FormUpdateDataRequest formUpdateDataRequest = new FormUpdateDataRequest();
        formUpdateDataRequest.setInstValue(instValue);
        formUpdateDataRequest.setFormId(formVO.getId());
        formUpdateDataRequest.setVersion(formVO.getVersion());
        formUpdateDataRequest.setUuid(formSyncOpenRequest.getUuid());
        if (formSyncOpenRequest.getStartWorkflow()) {
            formUpdateDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        } else {
            formUpdateDataRequest.setStatus(FormDataStatusEnum.DRAFT.name());
        }
        formUpdateDataRequest.setApplicationId(formSyncOpenRequest.getApplicationId());
        formUpdateDataRequest.setDataStreamTrigger(formSyncOpenRequest.getStartTrigger());
        formMongoDbService.updateData(formUpdateDataRequest, Boolean.FALSE);
        FormDataSyncVO formDataSyncVO = new FormDataSyncVO();
        formDataSyncVO.setUuid(formUpdateDataRequest.getUuid());
        JSONObject jsonObject = returnValue(formSyncOpenRequest, formSyncOpenRequest.getUuid());
        formDataSyncVO.setInstValue(jsonObject);
        return formDataSyncVO;
    }

    @Override
    public FormDataSyncVO delete(FormSyncOpenRequest formSyncOpenRequest) {
        LowcodeDataDomain info = formMongoDbService.info(formSyncOpenRequest.getUuid(), formSyncOpenRequest.getFormId(),
                formSyncOpenRequest.getApplicationId());
        if (info == null) {
            throw new OpenException(OpenResultCode.DATA_NOT_EXIST);
        }
        formMongoDbService.deleteData(formSyncOpenRequest.getUuid(), formSyncOpenRequest.getFormId(),
                formSyncOpenRequest.getApplicationId(), new ArrayList<>());
        FormDataSyncVO formDataSyncVO = new FormDataSyncVO();
        formDataSyncVO.setUuid(formSyncOpenRequest.getUuid());
        return formDataSyncVO;
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryList(FormDataQueryRequest formDataQueryRequest) {
        FormSearchDataRequest formSearchDataRequest =
                AbstractFormOpenConverter.INSTANCE.toRequest(formDataQueryRequest);
        formSearchDataRequest.setFormId(formDataQueryRequest.getFormId());
        formSearchDataRequest.setApplicationId(formDataQueryRequest.getApplicationId());
        QueryPageVO<LowcodeDataVO> lowcodeDataVOQueryPageVO = formMongoDbService.queryList(formSearchDataRequest);
        SystemAllDataVO systemAllData = adminCommonService.getSystemAllData();
        List<FormExtraFunctionSync> syncList = getSyncList(formDataQueryRequest, false);
        List<LowcodeDataVO> list = lowcodeDataVOQueryPageVO.getList();
        for (LowcodeDataVO lowcodeDataVO : list) {
            JSONObject sendJson = lowcodeDataVO.getInstValue();
            for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
                if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                    continue;
                }
                FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
                if (formDataService != null) {
                    formDataService.dealWhileSend(formExtraFunctionSync, lowcodeDataVO.getInstValue(), systemAllData,
                            sendJson);
                } else {
                    sendJson.put(formExtraFunctionSync.getMappingField(),
                            lowcodeDataVO.getInstValue().get(formExtraFunctionSync.getName()));
                }
            }
            lowcodeDataVO.setInstValue(sendJson);
        }
        return lowcodeDataVOQueryPageVO;
    }

    private List<FormExtraFunctionSync> getSyncList(FormDataQueryRequest formDataQueryRequest, Boolean needThrow) {
        SyncMappingVO syncMappingVO =
                syncMappingService.info(formDataQueryRequest.getApplicationId(), formDataQueryRequest.getFormId());
        if (needThrow) {
            if (syncMappingVO == null) {
                throw new OpenException(OpenResultCode.FORM_MAPPING_NOT_EXIST);
            }
        } else {
            return new ArrayList<>();
        }
        return JSONObject.parseArray(syncMappingVO.getMappingConfig(), FormExtraFunctionSync.class);
    }

    private SystemAllDataNameVO importCheck() {
        List<UserCompanyVO> allUser = userCompanyService.getAllUser();
        List<Long> userIdList = allUser.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userService.queryByIds(userIdList);
        Map<String, UserVO> phoneNumberMap = userVOS.stream().collect(Collectors.toMap(UserVO::getPhonenumber, c -> c));
        List<DepartmentVO> departmentVOList = departmentService.queryAllList(UserUtils.getUser().getCompanyId(),
                DepartmentTypeEnum.INTERNAL_DEPT.getCode(), Boolean.TRUE);
        Map<String, List<DepartmentVO>> deptNameMap =
                departmentVOList.stream().collect(Collectors.groupingBy(DepartmentVO::getDeptName));
        SystemAllDataNameVO importCheck = new SystemAllDataNameVO();
        importCheck.setDeptNameMap(deptNameMap);
        importCheck.setPhonenumberMap(phoneNumberMap);
        return importCheck;
    }

    private JSONObject checkAndGetInstValue(FormSyncOpenRequest formSyncOpenRequest) {
        SyncMappingVO info =
                syncMappingService.info(formSyncOpenRequest.getApplicationId(), formSyncOpenRequest.getFormId());
        if (info == null) {
            throw new OpenException(OpenResultCode.FORM_MAPPING_NOT_EXIST);
        }
        List<FormExtraFunctionSync> syncList =
                JSONObject.parseArray(info.getMappingConfig(), FormExtraFunctionSync.class);

        JSONObject syncJson = formSyncOpenRequest.getInstValue();
        SystemAllDataNameVO systemAllDataNameVO = importCheck();
        JSONObject instValue = new JSONObject();
        for (FormExtraFunctionSync formExtraFunctionSync : syncList) {
            if (StringUtils.isEmpty(formExtraFunctionSync.getMappingField())) {
                continue;
            }
            Object value = syncJson.get(formExtraFunctionSync.getMappingField());
            FormDataService formDataService = formDataContext.getHandler(formExtraFunctionSync.getType());
            if (formDataService != null) {
                FormSyncCheckResultVO formSyncCheckResultVO =
                        formDataService.dealWhileSync(formExtraFunctionSync, value, systemAllDataNameVO);
                if (StringUtils.isNotEmpty(formSyncCheckResultVO.getErrorMessage())) {
                    throw new OpenException(OpenResultCode.SYNC_ERROR, formSyncCheckResultVO.getErrorMessage());
                }
                instValue.put(formExtraFunctionSync.getName(), formSyncCheckResultVO.getValue());
            } else {
                instValue.put(formExtraFunctionSync.getName(), value);
            }
        }
        return instValue;
    }
}
