package com.wuji.admin.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wuji.admin.model.entity.UserEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2024-04-15
 */
@Repository
@DS("slave")
public interface UserMapper extends BaseMapper<UserEntity> {
    @Select(" SELECT u.user_id,u.uuid,u.user_name, uc.nick_name, u.real_name, uc.email, " +
            " u.phonenumber,u.sex,u.`status`,u.del_flag,u.demand, u.interests, uc.company_id, uc.create_time " +
            " FROM sys_user u" +
            " LEFT JOIN sys_user_dept ud ON u.user_id = ud.user_id" +
            " LEFT JOIN sys_user_post up ON u.user_id = up.user_id " +
            " LEFT JOIN sys_user_company uc ON u.user_id = uc.user_id  " +
            " ${ew.customSqlSegment} ")
    IPage<UserEntity> selectPages(IPage<UserEntity> page, @Param("ew") QueryWrapper<UserEntity> queryWrapper);

    @Select(" SELECT u.user_id,u.uuid,u.user_name, uc.nick_name, u.real_name, uc.email, u.remark, " +
            " u.phonenumber,u.sex,u.`status`,u.del_flag,u.demand, u.interests, uc.company_id " +
            " FROM sys_user u" +
            " LEFT JOIN sys_user_dept ud ON u.user_id = ud.user_id" +
            " LEFT JOIN sys_user_post up ON u.user_id = up.user_id " +
            " LEFT JOIN sys_user_company uc ON u.user_id = uc.user_id  " +
            " ${ew.customSqlSegment} ")
    List<UserEntity> selectLists(@Param("ew") QueryWrapper<UserEntity> queryWrapper);
}
