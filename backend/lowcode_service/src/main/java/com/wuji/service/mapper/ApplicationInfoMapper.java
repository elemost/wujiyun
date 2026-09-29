package com.wuji.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuji.service.model.entity.ApplicationInfoEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-06-03
 */
public interface ApplicationInfoMapper extends BaseMapper<ApplicationInfoEntity> {

    @Select(" SELECT * FROM lc_application_info " +
            " WHERE info_key = '${infoKey}' and FROM_UNIXTIME(info_value/1000) = TIMESTAMP(DATE_ADD(DATE(NOW()), INTERVAL ${day} DAY)) ")
    List<ApplicationInfoEntity> expire(@Param("infoKey") String infoKey, @Param("day") Integer day);

}
