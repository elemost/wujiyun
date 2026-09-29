package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormRuleEntity;
import com.wuji.service.model.request.FormRuleCreateRequest;
import com.wuji.service.model.request.FormRuleSortRequest;
import com.wuji.service.model.request.FormRuleUpdateRequest;
import com.wuji.service.model.vo.FormRuleVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-12-16
 */
public interface FormRuleService extends IService<FormRuleEntity> {
    String create(FormRuleCreateRequest formRuleCreateRequest);

    void update(FormRuleUpdateRequest formRuleUpdateRequest);

    void updateStatus(FormRuleUpdateRequest formRuleUpdateRequest);

    FormRuleVO info(String id, String applicationId, String formId);

    List<FormRuleVO> queryList(String formId, String applicationId, String ruleType, String state);

    List<FormRuleVO> queryListByApplication(String applicationId);

    void delete(String id, String applicationId, String formId);

    void sort(FormRuleSortRequest formRuleSortRequest);

    Map<String, List<FormRuleVO>> queryAllList(String applicationId, String formId);

    void userTemplate(String applicationId, String templateApplicationId);

    void copyApplication(String applicationId, String sourceApplicationId);
}
