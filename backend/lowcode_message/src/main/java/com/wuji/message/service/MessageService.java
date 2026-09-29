package com.wuji.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.message.model.entity.MessageEntity;
import com.wuji.message.model.request.MessageCreateRequest;
import com.wuji.message.model.request.MessageListRequest;
import com.wuji.message.model.vo.MessageVO;

import java.util.Map;

public interface MessageService extends IService<MessageEntity> {

    QueryPageVO<MessageVO> queryPage(MessageListRequest messageListRequest);

    MessageVO info(String messageId);

    Map<String, Integer> notView();

    void insert(MessageCreateRequest messageCreateRequest);

    void delete(Long messageId);

    void allView(String source);
}
