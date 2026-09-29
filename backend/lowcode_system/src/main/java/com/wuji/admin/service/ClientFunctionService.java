package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.ClientFunctionEntity;
import com.wuji.admin.model.vo.ClientFunctionVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
public interface ClientFunctionService extends IService<ClientFunctionEntity> {
    List<ClientFunctionVO> getByClientId(String clientId, List<String> idList);

    List<ClientFunctionVO> getExistLimit(String clientId, String equityId);
}
