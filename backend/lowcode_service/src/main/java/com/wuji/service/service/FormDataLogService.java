package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.FormDataLogEntity;
import com.wuji.service.model.vo.FormDataLogContentVO;
import com.wuji.service.model.vo.FormDataLogVO;
import com.wuji.service.model.vo.FormVO;

import java.util.List;

/**
 * <p>
 * 表单数据日志 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
public interface FormDataLogService extends IService<FormDataLogEntity> {
    /**
     * 记录操作日志
     *
     * @param user
     * @param previous
     * @param current
     * @param status
     * @return
     */
    List<FormDataLogContentVO> recordLog(UserDomain user, LowcodeDataDomain previous, LowcodeDataDomain current,
                                         FormVO formVO, String status);

    List<FormDataLogVO> list(String recordId, String applicationId);

    FormDataLogVO lastLog(String recordId, String applicationId);
}
