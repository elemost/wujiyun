package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 应用目录 Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@Repository
public interface ApplicationCategoryMapper extends BaseMapper<ApplicationCategoryEntity> {

    @Update(" UPDATE lc_application_category " +
            " SET sort_num = sort_num + 1 , " +
            " modifier = #{modifier} " +
            " WHERE sort_num > #{sortNum} or (sort_num = #{sortNum} and create_time <= #{createTime}) and deleted = 0 and parent_id = #{parentId}")
    void updateSort(@Param("sortNum") Integer sortNum, @Param("createTime") Date createTime,
                @Param("modifier") String modifier, @Param("parentId") String parentId);


    @Select(" select count(0) as count,application_id from lc_application_category " +
            " ${ew.customSqlSegment} ")
    List<ApplicationCategoryStatisticVO> applicationStatistic(
            @Param("ew") QueryWrapper<ApplicationCategoryEntity> queryWrapper);
}
