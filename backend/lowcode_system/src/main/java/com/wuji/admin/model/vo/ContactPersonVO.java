package com.wuji.admin.model.vo;

import com.wuji.common.model.vo.UserVO;
import lombok.Data;

import java.util.List;

@Data
public class ContactPersonVO {
   private List<UserVO> userVOList;
}
