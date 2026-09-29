package com.wuji.common.trans;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;

@Component
public class DbTxBroker {

    @Transactional(value = "mybatisTransactionManager")
    public <V> V inTransactionMybatisTransactionManager(Callable<V> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(value = "mongoTransactionManager")
    public <V> V inTransactionMongoTransactionManager(Callable<V> callable) {
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

