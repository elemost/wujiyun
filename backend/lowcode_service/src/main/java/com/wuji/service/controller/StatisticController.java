package com.wuji.service.controller;

import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import com.wuji.service.model.vo.StatisticVO;
import com.wuji.service.service.StatisticService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistic")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @ApiOperation("统计")
    @GetMapping("/info")
    public StatisticVO getStatistic() {
        return statisticService.getStatistic();
    }


    @ApiOperation("数据工厂")
    @GetMapping("/dataFactory")
    public List<FormDataFactoryStatisticVO> dataFactoryStatistic() {
        return statisticService.dataFactoryStatistic();
    }

    @ApiOperation("聚合表")
    @GetMapping("/aggregate")
    public List<FormAggregateStatisticVO> aggregateStatistic() {
        return statisticService.aggregateStatistic();
    }

    @ApiOperation("数智助手")
    @GetMapping("/dataStream")
    public List<FormDataStreamStatisticVO> dataStreamStatistic() {
        return statisticService.dataStreamStatistic();
    }

    @ApiOperation("应用表单数")
    @GetMapping("/applicationCategory")
    public List<ApplicationCategoryStatisticVO> applicationCategoryStatistic() {
        return statisticService.applicationCategoryStatistic();
    }


}
