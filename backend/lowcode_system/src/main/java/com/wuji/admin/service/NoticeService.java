package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.NoticeEntity;
import com.wuji.admin.model.request.NoticeRequest;
import com.wuji.admin.model.vo.NoticeVO;
import com.wuji.common.model.vo.QueryPageVO;

/**
 * <p>
 * 通知公告表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
public interface NoticeService extends IService<NoticeEntity> {
    QueryPageVO<NoticeVO> queryList(NoticeRequest noticeRequest);
}
