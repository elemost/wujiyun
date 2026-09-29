package com.wuji.admin.client.qcc;

import com.wuji.common.exception.BizException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class QccResponseDecoder extends ResponseEntityDecoder implements Decoder {
    public QccResponseDecoder(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object decode = super.decode(response, type);
        if (type.getTypeName().contains(QccResult.class.getName())) {
            QccResult result = (QccResult) decode;
            if (!result.isSuccess(result.getStatus())) {
                throw new BizException(result.getMsg());
            }
        }
        return decode;
    }
}
