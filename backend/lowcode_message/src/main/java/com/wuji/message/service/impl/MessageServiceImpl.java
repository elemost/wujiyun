package com.wuji.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.message.converter.AbstractMessageConverter;
import com.wuji.message.enums.MessageSendTypeEnum;
import com.wuji.message.mapper.MessageMapper;
import com.wuji.message.model.domain.MessageDomain;
import com.wuji.message.model.entity.MessageEntity;
import com.wuji.message.model.request.MessageCreateRequest;
import com.wuji.message.model.request.MessageListRequest;
import com.wuji.message.model.vo.MessageNotViewVO;
import com.wuji.message.model.vo.MessageVO;
import com.wuji.message.service.MessageService;
import com.wuji.message.service.MessageUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-04-29
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageUserService messageUserService;

    @Override
    public QueryPageVO<MessageVO> queryPage(MessageListRequest messageListRequest) {
        QueryWrapper<MessageDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(messageListRequest.getMessageType()), "m.message_type",
                messageListRequest.getMessageType());
        queryWrapper.eq(StringUtils.isNotEmpty(messageListRequest.getSource()), "m.source",
                messageListRequest.getSource());
        if (messageListRequest.getView() != null) {
            if (messageListRequest.getView()) {
                queryWrapper.eq("mu.view", true);
            } else {
                queryWrapper.and(c -> c.eq("mu.view", false).or().isNull("mu.view"));
            }
        }
        queryWrapper.and(c -> c.eq("mu.user_id", UserUtils.getUser().getUserId()).or()
                .eq("m.send_type", MessageSendTypeEnum.ALL.name()));
        queryWrapper.eq("m.company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.eq("m.deleted", Boolean.FALSE);
        queryWrapper.eq(StringUtils.isNotEmpty(messageListRequest.getContent()), "m.content",
                messageListRequest.getContent());
        queryWrapper.orderByDesc("m.create_time");
        queryWrapper.groupBy("m.id");
        IPage<MessageDomain> messageDomainIPage =
                messageMapper.selectPage(new Page<>(messageListRequest.getPageNum(), messageListRequest.getPageSize()),
                        queryWrapper);
        List<MessageVO> messageList = new ArrayList<>();
        for (MessageDomain messageDomain : messageDomainIPage.getRecords()) {
            MessageVO messageVO = AbstractMessageConverter.INSTANCE.toVO(messageDomain);
            if (messageVO.getView() == null) {
                messageVO.setView(Boolean.FALSE);
            }
            messageList.add(messageVO);
        }
        return PageUtils.toQueryPage(messageDomainIPage, messageList);
    }

    @Override
    public MessageVO info(String messageId) {
        QueryWrapper<MessageDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m.id", messageId);
        IPage<MessageDomain> messageDomainIPage = messageMapper.selectPage(new Page<>(1, 1), queryWrapper);
        if (!messageDomainIPage.getRecords().isEmpty()) {
            return AbstractMessageConverter.INSTANCE.toVO(messageDomainIPage.getRecords().get(0));
        }
        return null;
    }

    @Override
    public Map<String, Integer> notView() {
        QueryWrapper<MessageNotViewVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(c -> c.eq("mu.user_id", UserUtils.getUser().getUserId()).or()
                .eq("m.send_type", MessageSendTypeEnum.ALL.name()));
        queryWrapper.eq("m.company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.and(c -> c.eq("mu.view", false).or().isNull("mu.view"));
        queryWrapper.eq("m.deleted", false);
        queryWrapper.groupBy("m.source");
        List<MessageNotViewVO> messageNotViewVOS = messageMapper.notView(queryWrapper);
        return messageNotViewVOS.stream()
                .collect(Collectors.toMap(MessageNotViewVO::getSource, MessageNotViewVO::getCount));
    }

    @Override
    public void insert(MessageCreateRequest messageCreateRequest) {
        MessageEntity messageEntity = AbstractMessageConverter.INSTANCE.toEntity(messageCreateRequest);
        messageEntity.setCreator(UserUtils.getUser().getUserId());
        messageEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        messageMapper.insert(messageEntity);
        if (CollectionUtils.isNotEmpty(messageCreateRequest.getUserIdList())) {
            messageUserService.save(messageEntity.getId(), messageCreateRequest.getUserIdList());
        }
    }

    @Override
    public void delete(Long messageId) {
        MessageEntity exist = messageMapper.selectById(messageId);
        if (!"COMPANY".equals(exist.getSource())) {
            return;
        }
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setDeleted(Boolean.TRUE);
        LambdaQueryWrapper<MessageEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(MessageEntity::getId, messageId);

        messageMapper.update(messageEntity, deleteWrapper);
    }

    @Override
    public void allView(String source) {
        QueryWrapper<MessageDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("m.source", source);
        queryWrapper.and(c -> c.eq("mu.view", false).or().isNull("mu.view"));
        queryWrapper.and(c -> c.eq("mu.user_id", UserUtils.getUser().getUserId()).or()
                .eq("m.send_type", MessageSendTypeEnum.ALL.name()));
        queryWrapper.eq("m.company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.eq("m.deleted", Boolean.FALSE);
        List<MessageDomain> messageDomainList = messageMapper.select(queryWrapper);
        List<Long> messageIdList = messageDomainList.stream().map(MessageDomain::getId).collect(Collectors.toList());
        messageUserService.allView(messageIdList);
    }
}
