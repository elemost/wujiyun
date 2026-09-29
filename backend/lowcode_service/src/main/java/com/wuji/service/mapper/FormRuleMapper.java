package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormRuleEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-12-16
 */
public interface FormRuleMapper extends BaseMapper<FormRuleEntity> {
    @Update("<script>" +
            "<foreach collection=\"list\" separator=\";\" item=\"list\" index=\"index\"> " +
            "update lc_form_rule " +
            "SET sort = #{list.sort} " +
            "where id = #{list.id} and application_id = #{applicationId} and form_id = #{formId} " +
            "</foreach>" +
            "</script>")
    void batchUpdate(@Param("applicationId") String applicationId, @Param("list") List<FormRuleEntity> list,
                     @Param("formId") String formId);
}
