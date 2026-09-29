package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.request.FormDataStreamCreateRequest;
import com.wuji.service.model.request.FormDataStreamListRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.FormDataStreamFromVO;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import com.wuji.service.model.vo.FormDataStreamVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-30
 */
public interface FormDataStreamService extends IService<FormDataStreamEntity> {
    String create(FormDataStreamCreateRequest formDataStreamCreateRequest);

    void update(FormDataStreamUpdateRequest formDataStreamUpdateRequest);

    void publish(FormDataStreamUpdateRequest formDataStreamUpdateRequest);

    List<FormDataStreamVO> queryList(FormDataStreamListRequest formDataStreamListRequest);

    FormDataStreamVO info(String id, String applicationId);

    void delete(String id, String applicationId);

    void openOrClose(String id, String applicationId, Boolean enable);

    void useTemplate(String applicationId, String templateId, String sourceApplicationId);

    Map<String, String> copy(String applicationId, String formId, String newFormId);

    List<FormDataStreamFromVO> formList(FormDataStreamListRequest formDataStreamListRequest);

    void copyApplication(String applicationId, String sourceApplicationId, List<String> formIdList, Boolean share);

    Long getCountByApplication(List<String> applicationIdList);

    List<FormDataStreamStatisticVO> statisticDetail(List<String> applicationIdList);
}
