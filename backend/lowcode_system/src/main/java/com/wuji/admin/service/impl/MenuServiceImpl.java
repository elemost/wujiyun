package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.admin.constant.UserConstants;
import com.wuji.admin.converter.AbstractMenuConverter;
import com.wuji.admin.enums.MenuTypeEnum;
import com.wuji.admin.mapper.MenuMapper;
import com.wuji.admin.model.entity.MenuEntity;
import com.wuji.admin.model.request.MenuCreateRequest;
import com.wuji.admin.model.request.MenuUpdateRequest;
import com.wuji.admin.model.vo.MenuVO;
import com.wuji.admin.model.vo.MetaVO;
import com.wuji.admin.model.vo.RouterVO;
import com.wuji.admin.service.MenuService;
import com.wuji.admin.service.RoleMenuService;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.constant.Constants;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-20
 */
@Service
@DS("slave")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public void create(MenuCreateRequest menuCreateRequest) {
        UserDomain user = UserUtils.getUser();
        final MenuEntity menuEntity = AbstractMenuConverter.INSTANCE.toEntity(menuCreateRequest);
        menuEntity.setCreateBy(user.getUserName());
        menuEntity.setUpdateBy(user.getUserName());
        menuEntity.setCreateTime(new Date());
        menuEntity.setUpdateTime(new Date());
        menuMapper.insert(menuEntity);
    }

    @Override
    public void update(MenuUpdateRequest menuUpdateRequest) {
        UserDomain user = UserUtils.getUser();
        final MenuEntity menuEntity = AbstractMenuConverter.INSTANCE.toEntity(menuUpdateRequest);
        menuEntity.setUpdateBy(user.getUserName());
        menuEntity.setUpdateTime(new Date());
        menuMapper.updateById(menuEntity);
    }

    @Override
    public List<MenuVO> queryAllMenuByClientId() {
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(MenuEntity::getOrderNum);
        final List<MenuEntity> menuEntityList = menuMapper.selectList(queryWrapper);
        final List<MenuVO> menuVOList =
                menuEntityList.stream().map(AbstractMenuConverter.INSTANCE::toVO).collect(Collectors.toList());
        final List<MenuVO> parentList = menuVOList.stream()
                .filter(c -> Objects.equals(ConfigCache.getValue(ConfigEnum.LOWCODE_MENU_ID.name()),
                        c.getMenuId().toString())).collect(Collectors.toList());
        buildChildren(menuVOList, parentList);
        return parentList;
    }

    @Override
    public List<RouterVO> queryCurrentMenu() {
        final List<Long> menuList = roleMenuService.getCurrentUserMenu();
        if (CollectionUtils.isEmpty(menuList)) {
            throw new BizException(ResultCode.NO_AUTH);
        }
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(MenuEntity::getOrderNum);
        queryWrapper.in(MenuEntity::getMenuType, Lists.newArrayList(MenuTypeEnum.C.name(), MenuTypeEnum.M.name()));
        queryWrapper.eq(MenuEntity::getStatus, com.wuji.admin.constant.Constants.NORMAL);
        queryWrapper.in(MenuEntity::getMenuId, menuList);
        final List<MenuEntity> menuEntityList = menuMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(menuEntityList)) {
            throw new BizException(ResultCode.NO_AUTH);
        }
        List<MenuVO> menuVOList =
                menuEntityList.stream().map(AbstractMenuConverter.INSTANCE::toVO).collect(Collectors.toList());

        List<MenuVO> parentList = menuVOList.stream().filter(c -> "0".equals(c.getParentId().toString()) &&
                        c.getMenuId().toString().equals(ConfigCache.getValue(ConfigEnum.LOWCODE_MENU_ID.name())))
                .collect(Collectors.toList());

        buildChildren(menuVOList, parentList);
        return buildRoute(parentList);
    }


    private void buildChildren(List<MenuVO> menuVOList, List<MenuVO> parentList) {
        for (MenuVO menuVO : parentList) {
            List<MenuVO> children = menuVOList.stream().filter(c -> menuVO.getMenuId().equals(c.getParentId()))
                    .collect(Collectors.toList());
            menuVO.setChildren(children);
            buildChildren(menuVOList, children);
        }
    }

    private List<RouterVO> buildRoute(List<MenuVO> menuVOList) {

        List<RouterVO> routers = new LinkedList<RouterVO>();
        for (MenuVO menu : menuVOList) {
            RouterVO router = new RouterVO();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()),
                    menu.getPath()));
            List<MenuVO> cMenus = menu.getChildren();
            if (CollectionUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildRoute(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(
                        new MetaVO(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()),
                                menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVO> childrenList = new ArrayList<RouterVO>();
                RouterVO children = new RouterVO();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVO(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;

    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(MenuVO menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(MenuVO menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType()) &&
                UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(MenuVO menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 &&
                isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(MenuVO menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType()) &&
                menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(MenuVO menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) &&
                StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS);
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(MenuVO menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }


    /**
     * 内链域名特殊字符替换
     *
     * @return
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, "."},
                new String[]{"", "", "", "/"});
    }
}
