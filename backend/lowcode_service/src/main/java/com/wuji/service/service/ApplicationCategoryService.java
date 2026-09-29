package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.request.ApplicationCategoryCreateRequest;
import com.wuji.service.model.request.ApplicationCategorySortRequest;
import com.wuji.service.model.request.ApplicationCategoryTypeTransRequest;
import com.wuji.service.model.request.ApplicationCategoryUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import com.wuji.service.model.vo.ApplicationCategoryVO;

import java.util.List;

/**
 * <p>
 * 应用目录 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
public interface ApplicationCategoryService extends IService<ApplicationCategoryEntity> {
    /**
     * 创建应用目录
     *
     * @param applicationCategoryCreateRequest
     * @param defaultPrivilege
     */
    String create(ApplicationCategoryCreateRequest applicationCategoryCreateRequest, Boolean defaultPrivilege);

    /**
     * 修改目录
     * @param applicationCategoryUpdateRequest
     */
    void updateCategory(ApplicationCategoryUpdateRequest applicationCategoryUpdateRequest);

    void updateShowType(String id, String showType, String applicationId);

    /**
     * 删除目录
     *
     * @param id            目录id
     * @param applicationId
     */
    void deleteCategory(String id, String applicationId);

    /**
     * 获取应用目录列表
     *
     * @param applicationId 应用id
     * @param published
     */
    List<ApplicationCategoryVO> selectTree(List<String> applicationId, Boolean published);

    List<ApplicationCategoryVO> quoteList(List<String> applicationId, Boolean published);

    List<ApplicationCategoryVO> selectTreePrivilege(String applicationId, Boolean published);

    List<ApplicationCategoryVO> selectPrivilege(List<String> applicationId, Boolean published);

    /**
     * 已发布应用流程表单
     *
     * @param applicationIdList
     * @param showType
     * @param exitPrivilege
     * @return
     */
    List<ApplicationCategoryVO> getCategoryFlowableForm(List<String> applicationIdList, String showType, Boolean exitPrivilege);

    /**
     * 应用目录详情
     *
     * @param id            目录id
     * @param applicationId
     * @return
     */
    ApplicationCategoryVO info(String id, String applicationId);

    List<ApplicationCategoryVO> getByIdList(List<String> idList, String applicationId);

    List<ApplicationCategoryVO> getByIdList(List<String> idList, List<String> applicationIds);

    /**
     * 修改是否发布标记
     *
     * @param id
     * @param applicationId
     */
    void publish(String id, String applicationId);

    List<ApplicationCategoryVO> selectList(String applicationId);

    List<ApplicationCategoryVO> latestUseForm(Integer limitCount);

    String copy(String formId, String applicationId);

    void sortCategory(ApplicationCategorySortRequest applicationCategorySortRequest);

    List<ApplicationCategoryVO> getByAppIdList(List<String> appIdList);

    void transToFlowable(ApplicationCategoryTypeTransRequest applicationCategoryTypeTransRequest);

    Long categoryCount(List<String> applicationIdList);

    List<ApplicationCategoryStatisticVO> statisticDetail(List<String> applicationIdList);
}
