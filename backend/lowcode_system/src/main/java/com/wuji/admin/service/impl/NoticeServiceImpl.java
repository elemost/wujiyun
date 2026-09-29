package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractNoticeConverter;
import com.wuji.admin.mapper.NoticeMapper;
import com.wuji.admin.model.entity.NoticeEntity;
import com.wuji.admin.model.request.NoticeRequest;
import com.wuji.admin.model.vo.NoticeVO;
import com.wuji.admin.service.NoticeService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * <p>
 * 通知公告表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
@Service
@DS("slave")
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, NoticeEntity> implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public QueryPageVO<NoticeVO> queryList(NoticeRequest noticeRequest) {
        LambdaQueryWrapper<NoticeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(NoticeEntity::getCreateTime);
        Page<NoticeEntity> noticeEntityPage =
                noticeMapper.selectPage(new Page<>(noticeRequest.getPageNum(), noticeRequest.getPageSize()),
                        queryWrapper);
        return PageUtils.toQueryPage(noticeEntityPage,
                noticeEntityPage.getRecords().stream().map(AbstractNoticeConverter.INSTANCE::toVO)
                        .collect(Collectors.toList()));

    }
}
