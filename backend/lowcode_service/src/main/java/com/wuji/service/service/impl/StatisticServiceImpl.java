package com.wuji.service.service.impl;

import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import com.wuji.service.model.vo.StatisticVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.StatisticService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticServiceImpl implements StatisticService {

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Override
    public StatisticVO getStatistic() {
        StatisticVO statisticVO = new StatisticVO();
        Long userCount =
                userCompanyService.userCount(UserUtils.getUser().getCompanyId(), UserTypeEnum.INTERNAL.getCode());
        statisticVO.setUserCount(userCount.intValue());
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        List<String> applicationIds = allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applicationIds)) {
            return statisticVO;
        }
        Long aggregateCount = formAggregateService.getCountByApplication(applicationIds);
        Long dataStreamCount = formDataStreamService.getCountByApplication(applicationIds);
        Long dataFactoryCount = formDataFactoryService.getCountByApplication(applicationIds);
        Long formCount = applicationCategoryService.categoryCount(applicationIds);
        statisticVO.setApplicationCount(allApplication.size());
        statisticVO.setAggregateCount(aggregateCount.intValue());
        statisticVO.setDataStreamCount(dataStreamCount.intValue());
        statisticVO.setDataFactoryCount(dataFactoryCount.intValue());
        statisticVO.setFormCount(formCount.intValue());
        return statisticVO;
    }

    @Override
    public List<FormDataFactoryStatisticVO> dataFactoryStatistic() {
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        if (CollectionUtils.isEmpty(allApplication)) {
            return new ArrayList<>();
        }
        List<String> applicationIds = allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList());
        List<FormDataFactoryStatisticVO> formDataFactoryStatisticVOList =
                formDataFactoryService.statisticDetail(applicationIds);
        Map<String, String> applicationIdToNameMap = allApplication.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        for (FormDataFactoryStatisticVO formDataFactoryStatisticVO : formDataFactoryStatisticVOList) {
            formDataFactoryStatisticVO.setApplicationName(
                    applicationIdToNameMap.get(formDataFactoryStatisticVO.getApplicationId()));
        }
        return formDataFactoryStatisticVOList;
    }

    @Override
    public List<FormAggregateStatisticVO> aggregateStatistic() {
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        if (CollectionUtils.isEmpty(allApplication)) {
            return Collections.emptyList();
        }
        List<String> applicationIds = allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList());
        Map<String, String> applicationIdToNameMap = allApplication.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<FormAggregateStatisticVO> formAggregateStatisticVOS = formAggregateService.statisticDetail(applicationIds);
        for (FormAggregateStatisticVO formAggregateStatisticVO : formAggregateStatisticVOS) {
            formAggregateStatisticVO.setApplicationName(
                    applicationIdToNameMap.get(formAggregateStatisticVO.getApplicationId()));
        }
        return formAggregateStatisticVOS;
    }

    @Override
    public List<FormDataStreamStatisticVO> dataStreamStatistic() {
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        if (CollectionUtils.isEmpty(allApplication)) {
            return Collections.emptyList();
        }
        List<String> applicationIds = allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList());
        Map<String, String> applicationIdToNameMap = allApplication.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<FormDataStreamStatisticVO> formDataStreamStatisticVOS =
                formDataStreamService.statisticDetail(applicationIds);
        for (FormDataStreamStatisticVO formDataStreamStatisticVO : formDataStreamStatisticVOS) {
            formDataStreamStatisticVO.setApplicationName(
                    applicationIdToNameMap.get(formDataStreamStatisticVO.getApplicationId()));
        }
        return formDataStreamStatisticVOS;
    }

    @Override
    public List<ApplicationCategoryStatisticVO> applicationCategoryStatistic() {
        List<ApplicationVO> allApplication = applicationService.getAllNormalApplication();
        if (CollectionUtils.isEmpty(allApplication)) {
            return Collections.emptyList();
        }
        List<String> applicationIds = allApplication.stream().map(ApplicationVO::getId).collect(Collectors.toList());
        Map<String, String> applicationIdToNameMap = allApplication.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<ApplicationCategoryStatisticVO> applicationCategoryStatisticVOS =
                applicationCategoryService.statisticDetail(applicationIds);
        for (ApplicationCategoryStatisticVO applicationCategoryStatisticVO : applicationCategoryStatisticVOS) {
            applicationCategoryStatisticVO.setApplicationName(
                    applicationIdToNameMap.get(applicationCategoryStatisticVO.getApplicationId()));
        }
        return applicationCategoryStatisticVOS;
    }
}
