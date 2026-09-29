package com.wuji.service.model.domain;

import com.wuji.service.model.vo.LowcodeDataVO;
import lombok.Data;

@Data
public class FormExportDomain {
    private LowcodeDataVO lowcodeDataVO;

    private Integer maxRow;
}
