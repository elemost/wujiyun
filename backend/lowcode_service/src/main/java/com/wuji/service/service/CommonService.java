package com.wuji.service.service;

import com.wuji.common.model.vo.FunctionTypeVO;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.model.request.CommonUrlRequest;
import com.wuji.service.model.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommonService {
    FileVO uploadFile(MultipartFile multipartFile, FileTypeEnum fileTypeEnum, Boolean secret);

    String downloadFile(String fileUrl);

    List<FunctionTypeVO> getFunctionList();

    List<FunctionTypeVO> getMongoFunctionList();

    List<FunctionTypeVO> getFactoryFunctionList();

    String getUrl(CommonUrlRequest commonUrlRequest);
}
