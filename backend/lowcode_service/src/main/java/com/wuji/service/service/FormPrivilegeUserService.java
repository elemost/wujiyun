package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.FormPrivilegeEntity;
import com.wuji.service.model.entity.FormPrivilegeUserEntity;
import com.wuji.service.model.request.FormPrivilegeGroupSaveRequest;
import com.wuji.service.model.request.FormPrivilegeUserRequest;
import com.wuji.service.model.vo.FormPrivilegeUserVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeUserVO;

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
public interface FormPrivilegeUserService extends IService<FormPrivilegeUserEntity> {
    void save(String applicationId, String categoryId, String groupId,
              List<FormPrivilegeUserRequest> formPrivilegeUserRequestList);

    List<FormPrivilegeUserVO> getByGroupIdList(List<String> groupIdList);

    void deleted(String groupId);

    List<FormPrivilegeUserVO> getUserPrivilegeList(List<String> applicationIdList);

    List<FormPrivilegeUserVO> getUserPrivilegeListByCategory(List<String> categoryIdList, String applicationId);

    void useTemplate(String applicationId, Map<String, String> groupIdMap,
                     List<TemplateFormPrivilegeUserVO> templateFormPrivilegeUserList);

    void copyUserScope(String applicationId, String sourceApplicationId, Map<String, String> privilegeMap,
                       String categoryId);

    void useTemplatePrivilege(String applicationId, Map<String, String> categoryToPrivilegeMap,
                              List<FormPrivilegeEntity> formPrivilegeEntityList);

    void saveDefault(String groupId, String categoryId, String applicationId);

    void savePrivilegeByBusinessId(String businessId, String businessType,
                                   List<FormPrivilegeGroupSaveRequest> groupIds);
}
