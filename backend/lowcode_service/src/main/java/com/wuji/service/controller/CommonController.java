package com.wuji.service.controller;

import com.tencent.cloud.Response;
import com.wuji.common.context.UploadFileContext;
import com.wuji.common.model.vo.FunctionTypeVO;
import com.wuji.common.properties.SystemProperties;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.model.request.CommonUrlRequest;
import com.wuji.service.model.request.FileDownloadRequest;
import com.wuji.service.model.vo.FileVO;
import com.wuji.service.service.CommonService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    private CommonService commonService;

    @Autowired
    private UploadFileContext uploadFileContext;

    @Autowired
    private SystemProperties systemProperties;


    @ApiOperation("上传文件")
    @PostMapping("/uploadFile")
    public FileVO uploadFile(MultipartFile file, String fileType, Boolean secret) {
        FileTypeEnum fileTypeEnum = FileTypeEnum.valueOf(fileType);
        if (secret == null) {
            secret = Boolean.FALSE;
        }
        return commonService.uploadFile(file, fileTypeEnum, secret);
    }

    @ApiOperation("下载")
    @PostMapping("/downloadFile")
    public com.wuji.common.model.Response<String> uploadFile(@RequestBody FileDownloadRequest fileDownloadRequest) {
        return com.wuji.common.model.Response.success(commonService.downloadFile(fileDownloadRequest.getImgUrl()));
    }

    @ApiOperation("上传文件")
    @GetMapping("/getTicket")
    public Response getTicket() {
        return uploadFileContext.getHandler(systemProperties.getUploadType()).getTicket();
    }

    @ApiOperation("获取智能助手公式")
    @GetMapping("/functionList")
    public List<FunctionTypeVO> functionList() {
        return commonService.getFunctionList();
    }

    @ApiOperation("获取mongo支持的公式")
    @GetMapping("/getMongoFunctionList")
    public List<FunctionTypeVO> getMongoFunctionList() {
        return commonService.getMongoFunctionList();
    }

    @ApiOperation("获取mongo支持的公式")
    @GetMapping("/getFactoryFunctionList")
    public List<FunctionTypeVO> getFactoryFunctionList() {
        return commonService.getFactoryFunctionList();
    }

    @ApiOperation("获取地址")
    @PostMapping("/getUrl")
    public com.wuji.common.model.Response<String> getUrl(@RequestBody CommonUrlRequest commonUrlRequest) {
        return com.wuji.common.model.Response.success(commonService.getUrl(commonUrlRequest));
    }
}
