package com.wuji.service.controller;

import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.privilege.annotation.Resource;
import com.wuji.common.privilege.annotation.Secure;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormMongoDbViewService;
import com.wuji.service.valid.FormValidator;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workbench/form/mongodb")
public class WorkbenchFormMongoDbController {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormMongoDbViewService formMongoDbViewService;

    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public QueryPageVO<LowcodeDataVO> queryList(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        return formMongoDbService.queryListPrivilege(formSearchDataRequest);
    }

    @ApiOperation("查询数据")
    @PostMapping("/queryListView")
    public QueryPageVO<LowcodeDataVO> queryListViewPrivilege(
            @RequestBody FormViewMongoDbRequest formViewMongoDbRequest) {
        return formMongoDbViewService.queryListViewPrivilege(formViewMongoDbRequest);
    }

    @ApiOperation("详情页按钮")
    @GetMapping("/infoButton/{uuid}")
    public List<FormExtraFunctionButton> infoButton(@PathVariable String uuid,
                                                    @RequestParam("applicationId") String applicationId,
                                                    @RequestParam("formId") String formId,
                                                    @RequestParam("groupId") String groupId) {
        return formMongoDbService.infoButton(applicationId, formId, uuid, groupId);
    }


    @ApiOperation("查询子流程数据")
    @PostMapping("/queryChildList")
    @Secure(@Resource(identifierLocation = IdentifierLocationEnum.BODY_FIELD,
            applicationLocation = IdentifierLocationEnum.BODY_FIELD, resourceValidator = FormValidator.class))
    public QueryPageVO<LowcodeDataVO> queryChildList(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        if (StringUtils.isEmpty(formSearchDataRequest.getParentDataUuid())) {
            throw new BizException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isEmpty(formSearchDataRequest.getFormId())) {
            throw new BizException(ResultCode.VALIDATE_FAILED);
        }
        if (StringUtils.isEmpty(formSearchDataRequest.getParentFormId())) {
            throw new BizException(ResultCode.VALIDATE_FAILED);
        }
        return formMongoDbService.queryList(formSearchDataRequest);
    }
}
