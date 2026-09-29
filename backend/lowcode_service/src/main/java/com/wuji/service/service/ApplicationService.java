package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.request.ApplicationCreateRequest;
import com.wuji.service.model.request.ApplicationOwnerRequest;
import com.wuji.service.model.request.ApplicationQueryRequest;
import com.wuji.service.model.request.ApplicationUpdateRequest;
import com.wuji.service.model.vo.ApplicationCountVO;
import com.wuji.service.model.vo.ApplicationFlowableVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.CategoryPrivilegeVO;

import java.util.List;

/**
 * <p>
 * 应用 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
public interface ApplicationService extends IService<ApplicationEntity> {
    /**
     * 创建应用
     *
     * @param applicationCreateRequest
     * @return
     */
    String create(ApplicationCreateRequest applicationCreateRequest);

    void update(ApplicationUpdateRequest applicationUpdateRequest);


    ApplicationVO detail(String id);

    /**
     * 应用查询列表
     *
     * @param applicationQueryRequest
     * @return
     */
    QueryPageVO<ApplicationVO> queryList(ApplicationQueryRequest applicationQueryRequest);

    List<ApplicationVO> getApplicationByIdList(List<String> applicationIdList);

    List<ApplicationVO> getAllNormalApplication();

    List<ApplicationVO> getAllList();

    /**
     * 最近使用应用列表
     *
     * @param limitCount 性质数量
     * @return
     */
    List<ApplicationVO> latestUseApplication(Integer limitCount);

    /**
     * 获取我创建的应用
     *
     * @param applicationOwnerRequest@return
     */
    List<ApplicationVO> getMyApplication(ApplicationOwnerRequest applicationOwnerRequest);

    void delete(String id);

    /**
     * 改变应用状态
     *
     * @param id    应用id
     * @param state
     */
    void updateState(String id, String state);

    List<ApplicationFlowableVO> getAllApplicationFlowable();

    List<ApplicationFlowableVO> getAllApplicationForm();

    List<CategoryPrivilegeVO> getMyPrivilege();


    QueryPageVO<ApplicationVO> getApplicationListPrivilege(ApplicationQueryRequest applicationQueryRequest);

    List<ApplicationVO> getLatestApplicationPrivilege(Integer limitCount);

    Long useTimes(String templateApplicationId);

    void dealIcon();

    List<ApplicationCountVO> applicationCount(List<Long> companyIdList);

    ApplicationVO getByTemplateId(String templateId);
}
