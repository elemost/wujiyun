package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.request.FormDataFactoryCopyRequest;
import com.wuji.service.model.request.FormDataFactoryCreateRequest;
import com.wuji.service.model.request.FormDataFactoryRequest;
import com.wuji.service.model.request.FormDataFactorySyncConfigRequest;
import com.wuji.service.model.request.FormDataFactoryUpdateRequest;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import com.wuji.service.model.vo.FormDataFactoryUpdateVO;
import com.wuji.service.model.vo.FormDataFactoryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2026-01-05
 */
public interface FormDataFactoryService extends IService<FormDataFactoryEntity> {
    String create(FormDataFactoryCreateRequest formDataFactoryCreateRequest);

    FormDataFactoryUpdateVO update(FormDataFactoryUpdateRequest formDataFactoryUpdateRequest);

    String copy(FormDataFactoryCopyRequest formDataFactoryCopyRequest);

    List<FormDataFactoryVO> queryList(FormDataFactoryRequest formDataFactoryRequest);

    List<FormDataFactoryVO> queryByIds(String applicationId, List<String> ids);

    FormDataFactoryVO info(String id, String applicationId);

    void delete(String id, String applicationId);

    void syncFormConfig(FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest);

    void useTemplate(String applicationId, String templateId, String sourceApplicationId);

    void copyApplication(String applicationId, String sourceApplicationId);

    Long getCountByApplication(List<String> applicationIds);

    List<FormDataFactoryStatisticVO> statisticDetail(List<String> applicationIds);


}
