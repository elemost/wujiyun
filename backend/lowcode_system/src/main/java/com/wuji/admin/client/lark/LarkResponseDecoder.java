package com.wuji.admin.client.lark;

import com.wuji.common.exception.BizException;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class LarkResponseDecoder extends ResponseEntityDecoder implements Decoder {
    public LarkResponseDecoder(Decoder decoder) {
        super(decoder);
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
        Object decode = super.decode(response, type);
        if (type.getTypeName().contains(LarkResult.class.getName())) {
            LarkResult result = (LarkResult) decode;
            if (!result.isSuccess(result.getCode())) {
                throw new BizException(result.getMsg());
            }
        }
        return decode;
    }
}
