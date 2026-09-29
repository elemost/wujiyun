package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
public interface FormInfoMapper extends BaseMapper<FormInfoEntity> {
    @Select(" select ifnull(max(sort), 0) from lc_form_info ${ew.customSqlSegment} ")
    Integer maxSort(@Param("ew") QueryWrapper<FormInfoEntity> queryWrapper);

    @Update("<script>" +
            "<foreach collection=\"list\" separator=\";\" item=\"list\" index=\"index\"> " +
            "update lc_form_info " +
            "SET sort = #{list.sort} " +
            "where id = #{list.id} and application_id = #{applicationId} and form_id = #{formId}" +
            "</foreach>" +
            "</script>")
    void batchUpdate(@Param("applicationId") String applicationId, @Param("list") List<FormInfoEntity> list,
                     @Param("formId") String formId);
}
