package com.wuji.open.service.impl;

import com.wuji.open.converter.AbstractApplicationOpenConverter;
import com.wuji.open.model.request.ApplicationFormOpenRequest;
import com.wuji.open.model.vo.ApplicationFormOpenVO;
import com.wuji.open.model.vo.ApplicationOpenVO;
import com.wuji.open.service.ApplicationOpenService;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationOpenServiceImpl implements ApplicationOpenService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Override
    public List<ApplicationOpenVO> applicationList() {
        List<ApplicationVO> applicationVOList = applicationService.getAllList();
        return applicationVOList.stream().map(AbstractApplicationOpenConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationFormOpenVO> applicationFormList(ApplicationFormOpenRequest applicationFormOpenRequest) {
        List<ApplicationCategoryVO> applicationCategoryVOS =
                applicationCategoryService.selectList(applicationFormOpenRequest.getApplicationId());
        return applicationCategoryVOS.stream().map(AbstractApplicationOpenConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
