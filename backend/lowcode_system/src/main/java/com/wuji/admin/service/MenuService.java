package com.wuji.admin.service;

import com.wuji.admin.model.entity.MenuEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.request.MenuCreateRequest;
import com.wuji.admin.model.request.MenuUpdateRequest;
import com.wuji.admin.model.vo.MenuVO;
import com.wuji.admin.model.vo.RouterVO;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-20
 */
public interface MenuService extends IService<MenuEntity> {
    void create(MenuCreateRequest menuCreateRequest);

    void update(MenuUpdateRequest menuUpdateRequest);

    List<MenuVO> queryAllMenuByClientId();

    List<RouterVO> queryCurrentMenu();
}
