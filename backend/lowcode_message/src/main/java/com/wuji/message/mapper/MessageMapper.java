package com.wuji.message.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wuji.message.model.domain.MessageDomain;
import com.wuji.message.model.entity.MessageEntity;
import com.wuji.message.model.vo.MessageNotViewVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author hzm
 * @since 2025-04-29
 */
public interface MessageMapper extends BaseMapper<MessageEntity> {
    @Select(" select m.* , mu.view, mu.view_time from lc_message m " +
            " left join lc_message_user mu on m.id = mu.message_id " +
            " ${ew.customSqlSegment} ")
    IPage<MessageDomain> selectPage(Page<MessageDomain> objectPage, @Param("ew") Wrapper<MessageDomain> queryWrapper);

    @Select(" select count(0) as count, m.source from lc_message m " +
            " left join lc_message_user mu on m.id = mu.message_id " +
            " ${ew.customSqlSegment} ")
    List<MessageNotViewVO> notView(@Param("ew") Wrapper<MessageNotViewVO> queryWrapper);

    @Select(" select m.* , mu.view, mu.view_time from lc_message m " +
            " left join lc_message_user mu on m.id = mu.message_id " +
            " ${ew.customSqlSegment} ")
    List<MessageDomain> select(@Param("ew") Wrapper<MessageDomain> queryWrapper);
}
