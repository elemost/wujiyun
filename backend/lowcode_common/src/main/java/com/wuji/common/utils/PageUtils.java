package com.wuji.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuji.common.model.request.BasePageRequest;
import com.wuji.common.model.vo.QueryPageVO;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页方法
 *
 * @author Jackie
 * @date 2022-11-09
 */
public class PageUtils {

    /**
     * 分页
     *
     * @param page
     * @param <T>
     * @return
     */
    public static <T> QueryPageVO<T> toQueryPage(IPage<T> page) {
        return new QueryPageVO<>((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal(),
                page.getRecords());
    }

    public static <T> QueryPageVO<T> toQueryPage(IPage page, List<T> record) {
        return new QueryPageVO<>((int) page.getPages(), (int) page.getSize(), (int) page.getTotal(), record);
    }

    public static <T> QueryPageVO<T> toQueryPage(BasePageRequest basePageRequest, Integer total, List<T> record) {
        return new QueryPageVO<>( basePageRequest.getPageNum(),  basePageRequest.getPageSize(), total,
                record);
    }

    /**
     * 分页
     *
     * @param page
     * @param mapper
     * @param <V>
     * @param <E>
     * @return
     */
    public static <V, E> QueryPageVO<V> toQueryPage(IPage<E> page, Function<? super E, ? extends V> mapper) {
        return new QueryPageVO<>((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal(),
                page.getRecords().stream().map(mapper).collect(Collectors.toList()));
    }

    @FunctionalInterface
    public interface Converter {
        <V, E> List<V> entityToVo(List<E> list);
    }
}
