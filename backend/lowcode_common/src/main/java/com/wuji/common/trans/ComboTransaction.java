package com.wuji.common.trans;

import com.alibaba.druid.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Component
public class ComboTransaction {

    @Autowired
    private DbTxBroker dbTxBroker;

    public <V> V inCombinedTx(Callable<V> callable, String[] transactions) {
        if (callable == null) {
            return null;
        }

        Callable<V> combined = Stream.of(transactions).filter(ele -> !StringUtils.isEmpty(ele)).distinct()
                .reduce(callable, (r, tx) -> {
                    switch (tx) {
                        case "mybatisTransactionManager":
                            return () -> dbTxBroker.inTransactionMybatisTransactionManager(r);
                        case "mongoTransactionManager":
                            return () -> dbTxBroker.inTransactionMongoTransactionManager(r);
                        default:
                            return null;
                    }
                }, (r1, r2) -> r2);

        try {
            return combined.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
