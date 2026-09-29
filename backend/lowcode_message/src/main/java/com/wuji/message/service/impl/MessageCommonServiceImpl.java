package com.wuji.message.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.common.service.MessageCommonService;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.converter.AbstractMessageConverter;
import com.wuji.message.mapper.MessageMapper;
import com.wuji.message.model.entity.MessageEntity;
import com.wuji.message.service.MessageUserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("master")
public class MessageCommonServiceImpl implements MessageCommonService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageUserService messageUserService;

    @Override
    public void insert(MessageInsertRequest messageInsertRequest) {
        UserDomain user = UserUtils.getUser();
        if (CollectionUtils.isEmpty(messageInsertRequest.getUserIdList())) {
            return;
        }
        MessageEntity messageEntity = AbstractMessageConverter.INSTANCE.toEntity(messageInsertRequest);
        if (user != null) {
            messageEntity.setCreator(user.getUserId());
        }
        messageMapper.insert(messageEntity);
        messageUserService.save(messageEntity.getId(), messageInsertRequest.getUserIdList());
    }
}
