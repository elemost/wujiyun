package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.request.FormPrivilegeCreateRequest;
import com.wuji.service.model.request.FormPrivilegeSortRequest;
import com.wuji.service.model.request.FormPrivilegeUpdateRequest;
import com.wuji.service.model.vo.FormPrivilegeConfigVO;
import com.wuji.service.model.vo.FormPrivilegeDetailVO;
import com.wuji.service.model.vo.FormPrivilegeVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-10-15
 */
public interface FormPrivilegeService extends IService<FormPrivilegeEntity> {
    String create(FormPrivilegeCreateRequest formPrivilegeCreateRequest);

    void update(FormPrivilegeUpdateRequest formPrivilegeUpdateRequest);

    List<FormPrivilegeVO> getList(String categoryId, String applicationId);

    List<FormPrivilegeVO> getSelectList(String applicationId);

    List<FormPrivilegeVO> getByIdList(List<String> idList, String applicationId);

    FormPrivilegeVO detail(String id);

    void delete(String id);

    List<String> getExistPrivilegeList(List<String> applicationIdList);

    List<FormPrivilegeVO> getUserPrivilegeByCategory(List<String> categoryIdList, String applicationId);

    List<FormPrivilegeConfigVO> getPrivilege(String categoryId, String applicationId);

    List<FormPrivilegeVO> getCreateList(List<String> applicationIdList);

    List<FormPrivilegeDetailVO> getByFormId(String applicationId, List<String> formIdList);

    Map<String, String> useTemplate(String applicationId, String templateApplicationId,
                                    List<ApplicationCategoryEntity> applicationCategoryEntityList);

    Map<String, String> useTemplateDefaultPrivilege(String applicationId, String templateApplicationId,
                                                    List<ApplicationCategoryEntity> applicationCategoryEntityList);

    void saveDefault(ApplicationCategoryEntity applicationCategoryEntity);

    void dealData(String applicationId);

    void dealDataAll();

    Map<String, String> copy(String applicationId, String formId, String newFormId);

    void sort(FormPrivilegeSortRequest formPrivilegeSortRequest);

    Map<String, String> copyApplication(String applicationId, String sourceApplicationId,
                         List<ApplicationCategoryEntity> applicationCategoryEntityList, Boolean share);
}
