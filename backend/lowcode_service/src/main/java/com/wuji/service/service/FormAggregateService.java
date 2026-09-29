package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.entity.FormAggregateEntity;
import com.wuji.service.model.request.FormAggregateCreateRequest;
import com.wuji.service.model.request.FormAggregateDataRequest;
import com.wuji.service.model.request.FormAggregateListRequest;
import com.wuji.service.model.request.FormAggregateUpdateRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormAggregateMongoVO;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.FormFieldVO;

import java.util.List;

/**
 * <p>
 * 聚合表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-10
 */
public interface FormAggregateService extends IService<FormAggregateEntity> {
    String create(FormAggregateCreateRequest formAggregateCreateRequest);

    void update(FormAggregateUpdateRequest formAggregateUpdateRequest);

    QueryPageVO<FormAggregateVO> queryPage(FormAggregateListRequest formAggregateListRequest);

    List<FormAggregateVO> getByIdList(String applicationId, List<String> ids);

    List<FormAggregateVO> getByApplicationId(String applicationId);

    Long getCountByApplication(List<String> applicationIds);

    void delete(String id, String applicationId);

    FormAggregateVO info(String id, String applicationId);

    FieldExistNameVO getFields(String id, String applicationId);

    List<FieldExistNameVO> getFieldByAggIds(List<String> idList, String applicationId);

    List<FormFieldVO> getFieldByApplicationId(String applicationId);

    FormAggregateMongoVO buildAggregate(String id, String applicationId);

    Object getDataById(String id, FormAggregateDataRequest formAggregateDataRequest);

    void useTemplate(String applicationId, String templateId);

    void copyApplication(String applicationId, String sourceApplicationId, Boolean share);

    List<FormAggregateStatisticVO> statisticDetail(List<String> applicationIds);
}
