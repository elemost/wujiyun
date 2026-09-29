package com.wuji.service.service;

import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import com.wuji.service.model.vo.StatisticVO;

import java.util.List;

public interface StatisticService {

    StatisticVO getStatistic();

    List<FormDataFactoryStatisticVO> dataFactoryStatistic();

    List<FormAggregateStatisticVO> aggregateStatistic();

    List<FormDataStreamStatisticVO> dataStreamStatistic();

    List<ApplicationCategoryStatisticVO> applicationCategoryStatistic();

}
