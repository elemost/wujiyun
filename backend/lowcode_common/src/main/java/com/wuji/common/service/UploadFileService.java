package com.wuji.common.service;

import com.tencent.cloud.Response;
import com.wuji.common.model.vo.UploadFilePartVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface UploadFileService {
    String type();

    UploadFilePartVO updateByParts(MultipartFile file, String fileKey);

    String downloadFile(String imgUrl, String bucketName);

    void downloadFileToByte(String imgUrl, String bucketName, HttpServletResponse response);

    byte[] getBytesByUrl(String imgUrl);

    Response getTicket();
}
