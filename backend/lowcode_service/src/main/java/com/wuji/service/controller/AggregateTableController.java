package com.wuji.service.controller;

import com.wuji.service.model.info.FormAggregateTable;
import com.wuji.service.service.AggregateTableService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aggregate/table")
public class AggregateTableController {

    @Autowired
    private AggregateTableService aggregateTableService;

    @ApiOperation("聚合表")
    @PostMapping("")
    public Object aggregateTable(@RequestBody FormAggregateTable formAggregateTable) {
        return aggregateTableService.aggregateTable(formAggregateTable, null);
    }

}
