package com.wuji.console.config;

import org.apache.commons.lang3.StringUtils;


public class ForbiddenException extends RuntimeException{

    private final String username;

    public ForbiddenException(String username,String msg){
        super(msg);
        this.username = username;
    }

    public ForbiddenException(String username){
        this.username = username;
    }

    @Override
    public String getMessage(){
        StringBuffer buffer = new StringBuffer("Invalid user ");
        buffer.append(username);
        if (StringUtils.isNoneEmpty(super.getMessage())){
            buffer.append(" ");
            buffer.append(super.getMessage());
        }
        return buffer.toString();
    }
}
