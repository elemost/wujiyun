package com.wuji.admin.controller;

import com.wuji.admin.model.vo.DictDataVO;
import com.wuji.admin.service.DictDataService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 字典数据表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@RestController
@RequestMapping("/dict/data")
public class DictDataController {

    @Autowired
    private DictDataService dictDataService;

    @ApiOperation("根据type获取字典值")
    @GetMapping("/getByType/{type}")
    public List<DictDataVO> getByType(@PathVariable String type) {
        return dictDataService.getByType(type);
    }

}
