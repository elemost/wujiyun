package com.wuji.plugin.service;

public interface PluginUseService {

    String pluginType();

    Object execute(Object object);
}
