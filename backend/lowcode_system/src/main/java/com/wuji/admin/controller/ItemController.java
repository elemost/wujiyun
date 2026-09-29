package com.wuji.admin.controller;

import com.wuji.admin.model.vo.ItemVO;
import com.wuji.admin.service.ItemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 五极产品 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @ApiOperation("item list")
    @GetMapping("/list")
    public List<ItemVO> itemList() {
        return itemService.itemList();
    }

}
