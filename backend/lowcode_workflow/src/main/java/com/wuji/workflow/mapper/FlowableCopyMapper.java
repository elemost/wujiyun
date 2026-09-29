package com.wuji.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuji.workflow.model.domain.FlowableCopyDomain;
import com.wuji.workflow.model.entity.FlowableCopyEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 抄送表 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-09-12
 */
public interface FlowableCopyMapper extends BaseMapper<FlowableCopyEntity> {

    @Select(" select fc.* , fcu.user_view, fcu.view_time from lc_flowable_copy fc " +
            " left join lc_flowable_copy_user fcu on fc.id = fcu.copy_id " +
            " ${ew.customSqlSegment} ")
    IPage<FlowableCopyDomain> selectPage(Page<FlowableCopyDomain> objectPage,
                                         @Param("ew") Wrapper<FlowableCopyDomain> queryWrapper);
}
