package com.wuji.common.service.impl;

import cn.hutool.core.io.IoUtil;
import com.tencent.cloud.Response;
import com.wuji.common.model.vo.UploadFilePartVO;
import com.wuji.common.properties.MinioProperties;
import com.wuji.common.service.UploadFileService;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service("minioUploadFileServiceImpl")
@Slf4j
public class MinioUploadFileServiceImpl implements UploadFileService {

    @Autowired
    private MinioProperties minioProperties;

    @Override
    public String type() {
        return "MINIO";
    }

    @Override
    public UploadFilePartVO updateByParts(MultipartFile file, String fileKey) {
        UploadFilePartVO uploadFilePartVO = new UploadFilePartVO();
        MinioClient client = getClient();
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String key = fileKey + "." + suffix;
        try (InputStream inputStream = file.getInputStream();) {
            PutObjectArgs args = PutObjectArgs.builder().bucket(minioProperties.getBucketName()).object(key)
                    .stream(inputStream, file.getSize(), -1) // 文件流、文件大小、分片大小-1自动
                    .contentType(file.getContentType()).build();
            ObjectWriteResponse objectWriteResponse = client.putObject(args);
            uploadFilePartVO.setRet(minioProperties.getBucketName() + "/" + objectWriteResponse.object());
            uploadFilePartVO.setUrl(minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" +
                    uploadFilePartVO.getRet());
            return uploadFilePartVO;
        } catch (Exception e) {
            log.error("minio上传文件失败", e);
        }
        return null;
    }

    public MinioClient getClient() {
        return MinioClient.builder().endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }

    @Override
    public String downloadFile(String imgUrl, String bucketName) {
        MinioClient client = getClient();
        try {
            // 链接有效期7天
            return client.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder().bucket(minioProperties.getBucketName()).object(imgUrl)
                            .method(Method.GET).expiry(7, TimeUnit.DAYS).build());
        } catch (Exception e) {
            log.error("生成预览链接失败", e);
            return null;
        }
    }

    @Override
    public void downloadFileToByte(String imgUrl, String bucketName, HttpServletResponse response) {

    }

    @Override
    public byte[] getBytesByUrl(String imgUrl) {
        MinioClient client = getClient();
        try (InputStream inputStream = client.getObject(
                GetObjectArgs.builder().bucket(minioProperties.getBucketName()).object(imgUrl).build())) {
            // 读取流转byte数组，hutool工具简化IO操作
            return IoUtil.readBytes(inputStream);
        } catch (Exception e) {
            log.error("MinIO读取文件转byte失败，key:{}", imgUrl, e);
            return null;
        }
    }

    @Override
    public Response getTicket() {
        return null;
    }
}
