package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.CompanyRelationEntity;
import com.wuji.admin.model.request.CompanyRelationSaveRequest;
import com.wuji.admin.model.request.ContactPersonSaveRequest;
import com.wuji.admin.model.vo.ContactPersonVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-26
 */
public interface CompanyRelationService extends IService<CompanyRelationEntity> {
    void saveRelation(CompanyRelationSaveRequest companyRelationSaveRequest);

    void saveContactPerson(ContactPersonSaveRequest contactPersonSaveRequest);

    ContactPersonVO getContactPerson(Long companyId);

    List<Long> getCurrentUserRelationCompany();
}
