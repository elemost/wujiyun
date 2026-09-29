package com.wuji.common.express.function.text;

import com.ql.util.express.Operator;

import java.net.InetAddress;

public class IpFunction extends Operator {

    public IpFunction(String name) {
        this.name = name;
    }

    @Override
    public Object executeInner(Object[] list) throws Exception {
        InetAddress ipAddress = InetAddress.getLocalHost();
        return ipAddress.getHostAddress();
    }
}
