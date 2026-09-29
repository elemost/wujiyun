package com.wuji.admin.start;

import com.wuji.admin.service.RoleService;
import com.wuji.common.start.handler.StartHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("RoleStartHandlerImpl")
@Slf4j
public class RoleStartHandlerImpl implements StartHandler {

    @Autowired
    private RoleService roleService;

    @Override
    public void initAction() {
        try {
            log.info("角色初始化开始");
            roleService.initRole();
            log.info("角色初始化结束");
        } catch (Exception e) {
            log.error("角色初始化失败", e);
        }
    }
}
