package com.wuji.service.controller.open;


import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormExtraFunctionButton;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormMongoDbLinkRequest;
import com.wuji.service.model.request.FormMongodbLinkSelectRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.MongoDbUserFilledRequest;
import com.wuji.service.model.vo.FileVO;
import com.wuji.service.model.vo.FormExtraFunctionVO;
import com.wuji.service.model.vo.FormMongoDbLinkVO;
import com.wuji.service.model.vo.FormPublicPublishVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.CommonService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormPublicPublishService;
import com.wuji.service.service.FormService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/open/api/form/public/publish")
public class FormPublicPublishOpenController {
    @Autowired
    private FormService formService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormPublicPublishService formPublicPublishService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyService companyService;

    @ApiOperation("插入数据")
    @PostMapping("/insert")
    public void insertData(@RequestBody FormInsertDataRequest formInsertDataRequest) {
        formPublicPublishService.checkSecret(formInsertDataRequest.getAccessToken(),
                formInsertDataRequest.getPublishType(), formInsertDataRequest.getFormId(),
                formInsertDataRequest.getApplicationId());
        cacheUser(formInsertDataRequest.getApplicationId(), formInsertDataRequest.getFormId());
        if (StringUtils.isEmpty(formInsertDataRequest.getStatus())) {
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
        }
        formInsertDataRequest.setDataStreamTrigger(Boolean.TRUE);
        formMongoDbService.insertData(formInsertDataRequest);
    }

    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    public FileVO uploadFile(MultipartFile file, String fileType, String formId, String applicationId,
                             String publishType, String accessToken) {
        formPublicPublishService.checkSecret(accessToken, publishType, formId, applicationId);
        FileTypeEnum fileTypeEnum = FileTypeEnum.valueOf(fileType);
        return commonService.uploadFile(file, fileTypeEnum, Boolean.FALSE);
    }

    @ApiOperation("表单详情")
    @GetMapping("/info/{id}")
    public FormVO formInfo(@PathVariable String id, @RequestParam("applicationId") String applicationId) {
        return formService.info(id, applicationId);
    }

    @ApiOperation("数据详情")
    @GetMapping("/data/info/{uuid}")
    public LowcodeDataDomain dataInfo(@RequestParam("formId") String formId, @PathVariable String uuid,
                                      @RequestParam("applicationId") String applicationId) {
        return formMongoDbService.info(uuid, formId, applicationId);
    }

    @ApiOperation("表单发布详情")
    @GetMapping("/publish/infoById/{id}")
    public FormPublicPublishVO publishInfo(@PathVariable String id) {
        FormPublicPublishVO info = formPublicPublishService.info(id);
        formPublicPublishService.checkSecret(info.getAccessToken(), info.getPublishType(), info.getFormId(),
                info.getApplicationId());
        return info;
    }

    @ApiOperation("通过id获取数据")
    @GetMapping("/button/info/{id}")
    public FormExtraFunctionVO info(@PathVariable String id, @RequestParam("publishType") String publishType,
                                    @RequestParam("accessToken") String accessToken) {
        FormExtraFunctionVO info = formExtraFunctionServiceImpl.info(id);
        FormExtraFunctionButton formExtraFunctionButton =
                JSONObject.parseObject(info.getConfig(), FormExtraFunctionButton.class);
        formPublicPublishService.checkSecret(accessToken, publishType, formExtraFunctionButton.getBusinessId(),
                info.getApplicationId());
        return info;
    }

    @ApiOperation("查询数据")
    @PostMapping("/queryList")
    public QueryPageVO<LowcodeDataVO> queryList(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        formPublicPublishService.checkSecret(formSearchDataRequest.getAccessToken(),
                formSearchDataRequest.getPublishType(), formSearchDataRequest.getPublishFormId(),
                formSearchDataRequest.getApplicationId());
        cacheUser(formSearchDataRequest.getApplicationId(), formSearchDataRequest.getFormId());
        return formMongoDbService.queryList(formSearchDataRequest);
    }

    @ApiOperation("查询数据")
    @PostMapping("/queryListLink")
    public QueryPageVO<LowcodeDataVO> queryListLink(@RequestBody FormSearchDataRequest formSearchDataRequest) {
        cacheUser(formSearchDataRequest.getApplicationId(), formSearchDataRequest.getFormId());
        return formMongoDbService.queryListLink(formSearchDataRequest);
    }

    @ApiOperation("数据联动")
    @PostMapping("/link")
    public FormMongoDbLinkVO link(@RequestBody FormMongoDbLinkRequest formMongoDbLinkRequest) {
        formPublicPublishService.checkSecret(formMongoDbLinkRequest.getAccessToken(),
                formMongoDbLinkRequest.getPublishType(), formMongoDbLinkRequest.getPublishFormId(),
                formMongoDbLinkRequest.getApplicationId());
        cacheUser(formMongoDbLinkRequest.getApplicationId(), formMongoDbLinkRequest.getFormId());
        return formMongoDbService.link(formMongoDbLinkRequest);
    }

    @ApiOperation("数据联动")
    @PostMapping("/link/list")
    public FormMongoDbLinkVO linkList(@RequestBody FormMongoDbLinkRequest formMongoDbLinkRequest) {
        formPublicPublishService.checkSecret(formMongoDbLinkRequest.getAccessToken(),
                formMongoDbLinkRequest.getPublishType(), formMongoDbLinkRequest.getPublishFormId(),
                formMongoDbLinkRequest.getApplicationId());
        cacheUser(formMongoDbLinkRequest.getApplicationId(), formMongoDbLinkRequest.getFormId());
        return formMongoDbService.linkList(formMongoDbLinkRequest);
    }


    @ApiOperation("数据联动")
    @PostMapping("/link/select")
    public List<Object> linkSelect(@RequestBody FormMongodbLinkSelectRequest formMongodbLinkSelectRequest) {
        formPublicPublishService.checkSecret(formMongodbLinkSelectRequest.getAccessToken(),
                formMongodbLinkSelectRequest.getPublishType(), formMongodbLinkSelectRequest.getPublishFormId(),
                formMongodbLinkSelectRequest.getApplicationId());
        cacheUser(formMongodbLinkSelectRequest.getApplicationId(), formMongodbLinkSelectRequest.getFormId());
        return formMongoDbService.linkSelect(formMongodbLinkSelectRequest);
    }

    @ApiOperation("数据联动")
    @PostMapping("/select")
    public List<Object> select(@RequestBody FormMongodbLinkSelectRequest formMongodbLinkSelectRequest) {
        formPublicPublishService.checkSecret(formMongodbLinkSelectRequest.getAccessToken(),
                formMongodbLinkSelectRequest.getPublishType(), formMongodbLinkSelectRequest.getPublishFormId(),
                formMongodbLinkSelectRequest.getApplicationId());
        cacheUser(formMongodbLinkSelectRequest.getApplicationId(), formMongodbLinkSelectRequest.getFormId());
        return formMongoDbService.linkSelect(formMongodbLinkSelectRequest).stream().distinct()
                .collect(Collectors.toList());
    }

    @ApiOperation("数据联动")
    @PostMapping("/checkUserFilled")
    public Boolean checkUserFilled(@RequestBody MongoDbUserFilledRequest mongoDbUserFilledRequest) {
        formPublicPublishService.checkSecret(mongoDbUserFilledRequest.getAccessToken(),
                mongoDbUserFilledRequest.getPublishType(), mongoDbUserFilledRequest.getPublishFormId(),
                mongoDbUserFilledRequest.getApplicationId());
        cacheUser(mongoDbUserFilledRequest.getApplicationId(), mongoDbUserFilledRequest.getFormId());
        return formMongoDbService.checkUserFilled(mongoDbUserFilledRequest);
    }

    private void cacheUser(String applicationId, String formId) {
        FormVO info = formService.info(formId, applicationId);
        CompanyVO companyVO = companyService.info(info.getCompanyId());
        UserDomain userDomain = new UserDomain();
        userDomain.setCompanyUuid(companyVO.getCompanyUuid());
        userDomain.setCompanyId(info.getCompanyId());
        userDomain.setUserId(UserDefaultEnum.ANONYMOUS_USER.getId().toString());
        userDomain.setNickName(UserDefaultEnum.ANONYMOUS_USER.getName());
        userDomain.setUserName(UserDefaultEnum.ANONYMOUS_USER.getName());
        UserUtils.setUser(userDomain);
    }
}
