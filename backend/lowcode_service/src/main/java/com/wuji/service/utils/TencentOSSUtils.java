package com.wuji.service.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import com.wuji.common.properties.CosProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class TencentOSSUtils {
    @Autowired
    private CosProperties cosProperties;

    /**
     * 上传文件
     *
     * @param file
     * @param fileKey
     * @return
     */
    public String uploadFile(MultipartFile file, String fileKey) {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred =
                new BasicCOSCredentials(cosProperties.getAccessKeyId(), cosProperties.getAccessKeySecret());
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);

        // 获取文件后缀
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        String key = fileKey + "." + suffix;

        try {
            // 判断文件大小（小文件上传建议不超过20M）
            byte[] bytes = file.getBytes();
            int length = bytes.length;

            InputStream input = new ByteArrayInputStream(bytes);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(length);
            // 默认下载时根据cos路径key的后缀返回响应的contenttype, 上传时设置contenttype会覆盖默认值
            //objectMetadata.setContentType("image/jpeg");

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(cosProperties.getBucketName(), key, input, objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            //putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
            // putobjectResult会返回文件的etag
            // eTag = putObjectResult.getETag();
            // System.out.println(eTag);

            // url = domainName +""+ key;
        } catch (Exception e) {
            key = "";
            e.printStackTrace();
        }
        // 关闭客户端
        cosclient.shutdown();
        return key;
    }


    // 创建 TransferManager 实例，这个实例用来后续调用高级接口
    TransferManager createTransferManager() {
        // 创建一个 COSClient 实例，这是访问 COS 服务的基础实例。
        // 详细代码参见本页: 简单操作 -> 创建 COSClient
        COSClient cosClient = createCOSClient();

        // 自定义线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);

        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        // 设置高级接口的配置项
        // 分块上传阈值和分块大小分别为 5MB 和 1MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    // 创建 COSClient 实例，这个实例用来后续调用请求
    COSClient createCOSClient() {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred =
                new BasicCOSCredentials(cosProperties.getAccessKeyId(), cosProperties.getAccessKeySecret());
        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region(cosProperties.getRegion()));
        // 3 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);

        // 生成 cos 客户端。
        return cosclient;
    }

    public String updateByParts(MultipartFile file, String fileKey) {
        // 使用高级接口必须先保证本进程存在一个 TransferManager 实例，如果没有则创建
        // 详细代码参见本页：高级接口 -> 创建 TransferManager
        TransferManager transferManager = createTransferManager();

        // 获取文件后缀
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

        String key = fileKey + "." + suffix;


        try {
            // 这里创建一个 ByteArrayInputStream 来作为示例，实际中这里应该是您要上传的 InputStream 类型的流
            byte[] bytes = file.getBytes();
            int length = bytes.length;

            InputStream input = new ByteArrayInputStream(bytes);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
            // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
            objectMetadata.setContentLength(length);

            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(cosProperties.getBucketName(), key, input, objectMetadata);

            // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
            // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
            putObjectRequest.setStorageClass(StorageClass.Standard_IA);
            // 高级接口会返回一个异步结果Upload
            // 可同步地调用 waitForUploadResult 方法等待上传完成，成功返回 UploadResult, 失败抛出异常
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 确定本进程不再使用 transferManager 实例之后，关闭即可
        // 详细代码参见本页：高级接口 -> 关闭 TransferManager
        shutdownTransferManager(transferManager);
        return key;
    }

    public String downloadFile(String imgUrl, String bucketName) {
        COSClient cosClient = createCOSClient();
        Date expiration = new Date(new Date().getTime() + 30 * 60 * 1000L);
        URL url = cosClient.generatePresignedUrl(bucketName, imgUrl, expiration);
        return url.toString();
    }

    public void downloadFileToByte(String imgUrl, String bucketName, HttpServletResponse response) {
        COSClient cosClient = createCOSClient();
        Date expiration = new Date(new Date().getTime() + 30 * 60 * 1000L);
        URL url = cosClient.generatePresignedUrl(bucketName, imgUrl, expiration);
        try (InputStream inputStream = url.openStream();
             ServletOutputStream outputStream = response.getOutputStream()) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {

        }
    }

    void shutdownTransferManager(TransferManager transferManager) {
        // 指定参数为 true, 则同时会关闭 transferManager 内部的 COSClient 实例。
        // 指定参数为 false, 则不会关闭 transferManager 内部的 COSClient 实例。
        transferManager.shutdownNow(true);
    }

    public byte[] getBytesByUrl(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", cosProperties.getAccessKeyId() + ":" +
                    cosProperties.getAccessKeySecret()); // 设置你的SecretId和SecretKey进行鉴权

            try (InputStream inputStream = connection.getInputStream();) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                return out.toByteArray();
            }

        } catch (Exception e) {
            log.error("下载图片失败", e);
        }
        return new byte[0];
    }

    public Response getTicket() {
        // 临时秘钥配置
        TreeMap<String, Object> config = new TreeMap<String, Object>();
        try {
            config.put("SecretId", cosProperties.getAccessKeyId());
            config.put("SecretKey", cosProperties.getAccessKeySecret());
            config.put("durationSeconds", 1800); // 设置可使用事件1800秒 -> 30分钟
            config.put("bucket", cosProperties.getBucketName()); // 存储桶名
            config.put("region", cosProperties.getRegion()); // 地区
            // policy的resource => 前缀;

            // 密钥的权限列表。必须在这里指定本次临时密钥所需要的权限。
            // 简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看 https://cloud.tencent.com/document/product/436/31923
            config.put("allowActions", new String[]{"name/cos:PutObject",// 简单上传
                    "name/cos:PostObject",// 表单上传、小程序上传
                    "name/cos:GetBucket", // 允许获取桶的对象列表
                    // 分片上传
                    "name/cos:InitiateMultipartUpload", "name/cos:ListMultipartUploads", "name/cos:ListParts",
                    "name/cos:UploadPart", "name/cos:CompleteMultipartUpload"});

            //成功返回临时密钥信息，如下打印密钥信息
            return CosStsClient.getCredential(config);
        } catch (Exception e) {
            //失败抛出异常
            e.printStackTrace();
            throw new IllegalArgumentException("no valid secret !");
        }

    }
}
