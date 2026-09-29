package com.wuji.workflow.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.workflow.model.entity.HiIdentitylinkEntity;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-25
 */
public interface HiIdentitylinkService extends IService<HiIdentitylinkEntity> {

    List<HiIdentitylinkEntity> getByTaskIdList(List<String> taskIdList);
}
