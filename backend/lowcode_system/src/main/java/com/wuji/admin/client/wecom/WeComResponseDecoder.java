package com.wuji.admin.client.wecom;

import com.wuji.common.exception.BizException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class WeComResponseDecoder extends ResponseEntityDecoder implements Decoder {
    public WeComResponseDecoder(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object decode = super.decode(response, type);
        if (decode instanceof WeComResult) {
            WeComResult result = (WeComResult) decode;
            if (!result.isSuccess(result.getErrcode())) {
                throw new BizException(result.getErrmsg());
            }
        } else {
            if (((Class) type).getSuperclass().getTypeName().contains(WeComResult.class.getName())) {
                WeComResult result = (WeComResult) decode;
                if (!result.isSuccess(result.getErrcode())) {
                    throw new BizException(result.getErrmsg());
                }
            }
        }
        return decode;
    }
}
