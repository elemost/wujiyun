package com.wuji.open.service;

import com.wuji.open.model.request.ApplicationFormOpenRequest;
import com.wuji.open.model.vo.ApplicationFormOpenVO;
import com.wuji.open.model.vo.ApplicationOpenVO;

import java.util.List;

public interface ApplicationOpenService {
    List<ApplicationOpenVO> applicationList();

    List<ApplicationFormOpenVO> applicationFormList(ApplicationFormOpenRequest applicationFormOpenRequest);
}
