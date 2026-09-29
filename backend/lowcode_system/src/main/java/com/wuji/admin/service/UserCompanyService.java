package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserCompanyEntity;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.model.vo.UserCompanyVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-10-28
 */
public interface UserCompanyService extends IService<UserCompanyEntity> {
    Boolean checkAndSave(UserCompanySaveRequest userCompanySaveRequest, Boolean create);

    void save(List<UserCompanySaveRequest> userCompanySaveRequestList);

    void onlySaveBatch(List<UserCompanySaveRequest> userCompanySaveRequestList);

    void delete(Long companyId, List<String> dingUserId);

    void deleteByThirdId(Long companyId, String thirdType, List<String> thirdIdList);

    void deleteByLarkUserId(Long companyId, List<String> userId);

    List<UserCompanyVO> getByLarkUserId(List<String> larkUserId);

    List<UserCompanyVO> getByDingTalkUserId(List<String> idList);

    List<UserCompanyVO> getByWeComUserId(List<String> idList);

    UserCompanyVO getByCompanyIdAndSecret(String weComUserId, Long companyId);

    List<Long> getUserCompany(Long userId);

    List<UserCompanyVO> getByUserIdList(List<Long> userIdList);

    List<UserCompanyVO> getByUserIdsWithoutCompany(List<Long> userIdList);

    List<Long> deleteByUserId(Long userId);

    List<UserCompanyVO> getByThirdType(Long companyId, String thirdType);

    List<UserCompanyVO> getAllUser();

    Map<Long, UserCompanyVO> getAllUserWithDelete();

    Long getAdmin(Long companyId);

    List<UserCompanyVO> getAdminList(List<Long> companyIdList);

    void setAdmin(Long companyId, Long userId);

    Long userCount(Long companyId, String userType);

    UserCompanyVO getByUserIdAndCompanyId(Long userId, Long companyId);

    List<UserCompanyVO> getBySourceCompany(Long sourceCompanyId);

    void bindWeComId(List<Long> userIdList);

    UserCompanyVO getByJobNumber(Long companyId, String jobNumber);
}
