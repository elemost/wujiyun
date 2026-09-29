package com.wuji.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.mapper.MessageUserMapper;
import com.wuji.message.model.entity.MessageUserEntity;
import com.wuji.message.service.MessageUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-30
 */
@Service
public class MessageUserServiceImpl extends ServiceImpl<MessageUserMapper, MessageUserEntity>
        implements MessageUserService {

    @Autowired
    private MessageUserMapper messageUserMapper;

    @Override
    public void save(Long messageId, List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        userIdList = userIdList.stream().distinct().collect(Collectors.toList());
        List<MessageUserEntity> messageUserEntityList = new ArrayList<>();
        for (Long userId : userIdList) {
            if (userId == null) {
                continue;
            }
            MessageUserEntity messageUserEntity = new MessageUserEntity();
            messageUserEntity.setUserId(userId);
            messageUserEntity.setMessageId(messageId);
            messageUserEntityList.add(messageUserEntity);
        }
        saveBatch(messageUserEntityList);
    }

    @Override
    public void view(Long messageId) {
        LambdaQueryWrapper<MessageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MessageUserEntity::getMessageId, messageId);
        queryWrapper.eq(MessageUserEntity::getUserId, UserUtils.getUser().getUserId());
        MessageUserEntity exist = messageUserMapper.selectOne(queryWrapper);
        if (exist == null) {
            MessageUserEntity messageUserEntity = new MessageUserEntity();
            messageUserEntity.setMessageId(messageId);
            messageUserEntity.setUserId(UserUtils.getUser().getUserIdLongValue());
            messageUserEntity.setView(Boolean.TRUE);
            messageUserEntity.setViewTime(new Date());
            messageUserMapper.insert(messageUserEntity);
        } else {
            MessageUserEntity messageUserEntity = new MessageUserEntity();
            messageUserEntity.setView(Boolean.TRUE);
            messageUserEntity.setViewTime(new Date());
            messageUserMapper.update(messageUserEntity, queryWrapper);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void allView(List<Long> messageIdList) {
        if (CollectionUtils.isEmpty(messageIdList)) {
            return;
        }
        LambdaQueryWrapper<MessageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MessageUserEntity::getMessageId, messageIdList);
        queryWrapper.eq(MessageUserEntity::getUserId, UserUtils.getUser().getUserId());
        List<MessageUserEntity> messageUserEntityList = messageUserMapper.selectList(queryWrapper);
        List<MessageUserEntity> updateList = new ArrayList<>();
        List<MessageUserEntity> insertList = new ArrayList<>();
        Map<Long, MessageUserEntity> messageIdMap =
                messageUserEntityList.stream().collect(Collectors.toMap(MessageUserEntity::getMessageId, c -> c));
        for (Long messageId : messageIdList) {
            MessageUserEntity exist = messageIdMap.get(messageId);
            if (exist == null) {
                MessageUserEntity messageUserEntity = new MessageUserEntity();
                messageUserEntity.setMessageId(messageId);
                messageUserEntity.setUserId(UserUtils.getUser().getUserIdLongValue());
                messageUserEntity.setView(Boolean.TRUE);
                messageUserEntity.setViewTime(new Date());
                insertList.add(messageUserEntity);
            } else {
                MessageUserEntity messageUserEntity = new MessageUserEntity();
                messageUserEntity.setView(Boolean.TRUE);
                messageUserEntity.setViewTime(new Date());
                messageUserEntity.setId(exist.getId());
                updateList.add(messageUserEntity);
            }
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            saveBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateBatchById(updateList);
        }
    }
}
