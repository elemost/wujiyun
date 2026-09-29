package com.wuji.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.message.model.entity.MessageUserEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-04-30
 */
public interface MessageUserService extends IService<MessageUserEntity> {

    void save(Long messageId, List<Long> userIdList);

    void view(Long messageId);

    void allView(List<Long> messageIdList);
}
