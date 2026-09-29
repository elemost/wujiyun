package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.ItemEntity;
import com.wuji.admin.model.vo.ItemVO;

import java.util.List;

/**
 * <p>
 * 五极产品 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-10-28
 */

public interface ItemService extends IService<ItemEntity> {
    ItemVO info(String itemCode);

    List<ItemVO> itemList();
}
