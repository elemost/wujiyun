package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormQuoteEntity;
import com.wuji.service.model.request.FormQuoteSaveRequest;
import com.wuji.service.model.request.RelevanceRelationRequest;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.FormQuoteVO;
import com.wuji.service.model.vo.RelevanceRelationVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-11-09
 */
public interface FormQuoteService extends IService<FormQuoteEntity> {
    void save(FormQuoteSaveRequest formQuoteSaveRequest);

    FormQuoteVO getInfo(String formId, String applicationId);

    void delete(String formId, String applicationId, List<String> businessIdList);

    List<FormQuoteInfoVO> getByFormIdList(String applicationId, List<String> formIdList);

    void useTemplate(String applicationId, String templateApplicationId);

    List<RelevanceRelationVO> relevanceRelation(RelevanceRelationRequest relevanceRelationRequest);

    void copyApplication(String applicationId, String sourceApplicationId);
}
