package com.wuji.service.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/factory")
@Slf4j
public class DruidController {

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @GetMapping("/info")
    public void info() {
        Map<String, DataSource> dataSourceMap = dynamicRoutingDataSource.getDataSources();
        DataSource master = dataSourceMap.get("master");
        log(master);
        log(dataSourceMap.get("slave"));
        System.out.println("------------------------------------");
    }

    private static void log(DataSource master) {
        System.out.println("--- Active Connections Stack Traces ---");
        if (master instanceof ItemDataSource) {
            ItemDataSource itemDataSource = (ItemDataSource) master;
            DataSource realDataSource = itemDataSource.getRealDataSource();
            if (realDataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) realDataSource;
                System.out.println("活跃连接数: " + druidDataSource.getActiveCount());
                System.out.println("空闲连接数: " + druidDataSource.getPoolingCount());
                System.out.println("最大连接数: " + druidDataSource.getMaxActive());
                Set<DruidPooledConnection> activeConnections = druidDataSource.getActiveConnections();
                if (activeConnections != null && !activeConnections.isEmpty()) {
                    for (DruidPooledConnection holder : activeConnections) {
                        Thread ownerThread = holder.getOwnerThread();
                        System.out.println("Owner Thread Name: " + ownerThread.getName());
                        System.out.println("Owner Thread ID: " + ownerThread.getId());
                        System.out.println("Stack Trace:");
                        StackTraceElement[] stackTrace = ownerThread.getStackTrace();
                        if (stackTrace != null && stackTrace.length > 0) {
                            for (StackTraceElement element : stackTrace) {
                                System.out.println("\t" + element.toString());
                            }
                        } else {
                            System.out.println("\t(No stack trace available)");
                        }
                    }
                }
            }
        } else {
            System.out.println("数据源类型不支持直接获取统计信息");
        }
    }
}
