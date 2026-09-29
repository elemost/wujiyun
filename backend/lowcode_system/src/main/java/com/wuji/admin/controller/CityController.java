package com.wuji.admin.controller;

import com.wuji.admin.cache.CityCache;
import com.wuji.admin.model.vo.CityVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-08-04
 */
@RestController
@RequestMapping("/city")
public class CityController {

    @ApiOperation("获取公司列表")
    @GetMapping("/getCity")
    public List<CityVO> getCity() {
        return CityCache.getCity();
    }
    
}
