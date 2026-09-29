package com.wuji.workflow.exception;

import com.wuji.common.exception.BizException;
import com.wuji.workflow.enums.WorkflowResultCode;

public class FlowableBizException extends BizException {
    public FlowableBizException(WorkflowResultCode errorCode) {
        super(errorCode.getCode(), errorCode.getSubCode(), errorCode.getMessage());
    }
}
