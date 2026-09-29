package com.wuji.common.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class QRCodeUtils {

    public static String generateAndSaveQRCode(String text, String imgPath, String imgName) {
        String pathStr = "";
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            String name = imgName + ".png";
            pathStr = imgPath + name;
            Path path = Paths.get(pathStr);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            return pathStr;
        } catch (Exception e) {
            log.error("生成二维码失败", e);
        }
        return null;
    }

    public static byte[] generateAndSaveQRCode(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            // 将图片写入字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("生成二维码失败", e);
        }
        return null;
    }
}
