package com.wuji.service.controller;

import com.wuji.service.model.request.FormViewCalendarRequest;
import com.wuji.service.model.request.FormViewFieldGroupRequest;
import com.wuji.service.model.request.FormViewLevelRequest;
import com.wuji.service.model.vo.FormFieldGroupVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.service.FormMongoDbViewService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/form/data")
public class FormMongoDbViewController {

    @Autowired
    private FormMongoDbViewService formMongoDbViewService;

    @ApiOperation("获取甘特图 和 日历 数据")
    @PostMapping("/calendar/queryDataByTime")
    public List<LowcodeDataVO> calendarDailyData(@RequestBody FormViewCalendarRequest formViewCalendarRequest) {
        return formMongoDbViewService.queryDataByTime(formViewCalendarRequest);
    }

    @ApiOperation("当月match的数据")
    @PostMapping("/calendar/monthlyData")
    public List<Long> calendarMonthlyData(@RequestBody FormViewCalendarRequest formViewCalendarRequest) {
        return formMongoDbViewService.monthlyData(formViewCalendarRequest);
    }

    @ApiOperation("分组")
    @PostMapping("/fieldGroup")
    public FormFieldGroupVO fieldGroup(@RequestBody FormViewFieldGroupRequest formViewFieldGroupRequest) {
        return formMongoDbViewService.fieldGroup(formViewFieldGroupRequest);
    }

    @ApiOperation("分组")
    @PostMapping("/level")
    public List<LowcodeDataVO> level(@RequestBody FormViewLevelRequest formViewLevelRequest) {
        return formMongoDbViewService.level(formViewLevelRequest);
    }

}
