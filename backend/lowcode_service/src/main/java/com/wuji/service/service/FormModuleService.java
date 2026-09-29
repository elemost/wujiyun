package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.entity.FormModuleEntity;
import com.wuji.service.model.request.FormModuleInsertRequest;
import com.wuji.service.model.request.FormModuleUpdateRequest;
import com.wuji.service.model.request.InstrumentPanelViewListRequest;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.LowcodeDataVO;

import java.util.List;

/**
 * <p>
 * 表单组件表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-11-20
 */
public interface FormModuleService extends IService<FormModuleEntity> {
    String insert(FormModuleInsertRequest formModuleInsertRequest);

    void update(FormModuleUpdateRequest formModuleUpdateRequest);

    List<FormModuleVO> getByFormId(String formId, String applicationId);

    List<FormModuleVO> getByApplicationId(String applicationId);

    FormModuleVO info(String id, String applicationId, String formId);

    void deleteExtra(List<String> idList, String applicationId, String formId);

    void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId);

    void copyApplication(String applicationId, String sourceApplicationId, Boolean share);

    void copyForm(String applicationId, String formId, String sourceFormId);

    QueryPageVO<LowcodeDataVO> queryList(InstrumentPanelViewListRequest instrumentPanelViewListRequest);

}
