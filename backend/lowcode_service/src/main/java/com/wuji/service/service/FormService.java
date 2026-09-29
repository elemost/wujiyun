package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormEntity;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.request.FormCreateRequest;
import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormFieldVO;
import com.wuji.service.model.vo.FormVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 表单 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
public interface FormService extends IService<FormEntity> {

    /**
     * 创建表单
     *
     * @param formCreateRequest
     */
    void createForm(FormCreateRequest formCreateRequest);

    /**
     * 修改表单
     *
     * @param formUpdateRequest
     */
    void updateForm(FormUpdateRequest formUpdateRequest);

    /**
     * 表单详情
     *
     * @param id
     * @param applicationId
     */
    FormVO info(String id, String applicationId);


    List<FormVO> getByIdList(List<String> formIdList, String applicationId);

    List<FormVO> getByApplicationId(String applicationId);

    /**
     * 删除表单
     *
     * @param id            id
     * @param applicationId
     */
    void delete(String id, String applicationId);

    /**
     * 发布表单
     *
     * @param id
     * @param applicationId
     */
    void publish(String id, String applicationId);

    FieldExistNameVO getFormConfigCommonList(String formId, Boolean containSystem, String applicationId);

    FieldExistNameVO getAllFormConfigCommonList(String formId, Boolean containSystem, String applicationId,
                                                Boolean dealSubForm);

    List<FieldExistNameVO> getAllFormConfigCommonList(List<String> formIdList, Boolean containSystem,
                                                      String applicationId, Boolean dealSubForm);

    List<FieldExistNameVO> getAllFormConfigCommonList(Boolean containSystem,
                                                      String applicationId, Boolean dealSubForm);

    List<FormFieldVO> getAllFormFieldVO(String applicationId, List<String> formIdList, Integer defaultFieldType);

    List<FieldExistNameVO> getAllFormFieldVO(List<String> applicationIds, List<String> formIdList, Boolean needSubForm, Boolean needDeleted);

    void updateTableName(String applicationId);

    Map<String, Map<String, FormConfigEncryptKey>> getEncrypt(String applicationId);

    void copy(String sourceId, String formId, String applicationId);

    void copyApplication(String applicationId, String sourceApplicationId, Boolean share, Boolean needData);
}
