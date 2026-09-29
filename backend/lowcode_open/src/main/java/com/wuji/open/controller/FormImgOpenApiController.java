package com.wuji.open.controller;

import com.wuji.common.model.Response;
import com.wuji.open.components.OpenPlatformComponent;
import com.wuji.open.converter.AbstractFormFileOpenConverter;
import com.wuji.open.model.request.FormFileOpenRequest;
import com.wuji.open.model.vo.FormFileOpenVO;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.service.FormImgService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/develop/document/form/img")
public class FormImgOpenApiController {

    @Autowired
    private FormImgService formImgService;

    @Autowired
    private OpenPlatformComponent openPlatformComponent;

    @ApiOperation("创建图片")
    @PostMapping("/upload")
    public Response<String> upload(MultipartFile file, String appKey, String creator) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(appKey);
        openPlatformComponent.cacheUserDomain(creator, secret);
        return Response.success(formImgService.upload(file, FileTypeEnum.OPEN_FILE).getId());
    }

    @ApiOperation("获取图片地址")
    @PostMapping("/getImgUrl")
    public List<FormFileOpenVO> getImgUrl(@RequestBody FormFileOpenRequest formFileOpenRequest) {
        SecretVO secret = openPlatformComponent.checkAndGetSecret(formFileOpenRequest.getAppKey());
        openPlatformComponent.cacheUserDomain(formFileOpenRequest.getCreator(), secret);
        if (CollectionUtils.isEmpty(formFileOpenRequest.getFileIds())) {
            return new ArrayList<>();
        }
        return formImgService.getByIdListOpen(formFileOpenRequest.getFileIds()).stream()
                .map(AbstractFormFileOpenConverter.INSTANCE::toVO).collect(Collectors.toList());
    }


}
