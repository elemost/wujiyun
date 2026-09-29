package com.wuji.service.model.vo;

import com.wuji.common.model.domain.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserContextVO {
    private UserDomain user;
    private String userId;
}
