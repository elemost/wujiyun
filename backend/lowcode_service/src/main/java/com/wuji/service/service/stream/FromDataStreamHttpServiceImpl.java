package com.wuji.service.service.stream;

import com.wuji.service.service.FormDataStreamExecuteService;
import org.springframework.stereotype.Service;

@Service
public class FromDataStreamHttpServiceImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Override
    public String nodeType() {
        return "http";
    }
}
