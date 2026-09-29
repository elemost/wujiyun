package com.wuji.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 分页返回对象
 * @author Jackie
 * @date 2022-11-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPageVO<T> {
    private Integer page;
    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private List<T> list;
    private Map<String, Object> otherData;

    public QueryPageVO(Integer pageNum,Integer pageSize,Integer total,List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
    }

    public QueryPageVO(Integer total,List<T> list) {
        this.total = total;
        this.list = list;
    }
}
