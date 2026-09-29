package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-10-15
 */
public interface FormPrivilegeMapper extends BaseMapper<FormPrivilegeEntity> {

    @Update("<script>" +
            "<foreach collection=\"list\" separator=\";\" item=\"list\" index=\"index\"> " +
            "update lc_form_privilege " +
            "SET sort = #{list.sort} " +
            "where id = #{list.id} and application_id = #{applicationId} and category_id = #{formId}" +
            "</foreach>" +
            "</script>")
    void batchUpdate(@Param("applicationId") String applicationId, @Param("list") List<FormPrivilegeEntity> list,
                     @Param("formId") String formId);
}
