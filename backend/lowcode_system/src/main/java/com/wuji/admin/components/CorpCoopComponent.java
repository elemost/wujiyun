package com.wuji.admin.components;

import com.google.common.collect.Lists;
import com.wuji.admin.cache.CompanyCache;
import com.wuji.admin.cache.UserCompanyCache;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.UserDeptVO;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CorpCoopComponent {
    public void exchangeCompany(String companyUuid) {
        if (companyUuid == null) {
            return;
        }
        if (companyUuid.equals(UserUtils.getUser().getCompanyUuid())) {
            return;
        }
        CompanyVO companyVO = CompanyCache.getCompanyByUuid(companyUuid);
        if (companyVO == null) {
            return;
        }
        UserVO user = UserCompanyCache.getUserById(companyVO.getCompanyId() + "_" + UserUtils.getUser().getUserId());
        buildUserDomain(user);
    }

    public void exchangeCompanyId(Long companyId) {
        if (companyId == null) {
            return;
        }
        if (companyId.equals(UserUtils.getUser().getCompanyId())) {
            return;
        }
        UserVO user = UserCompanyCache.getUserById(companyId + "_" + UserUtils.getUser().getUserId());
        buildUserDomain(user);
    }

    public static void buildUserDomain(UserVO user) {
        UserDomain userDomain = new UserDomain();
        userDomain.setUserId(user.getUserId().toString());
        userDomain.setUserIdLongValue(user.getUserId());
        userDomain.setCompanyId(user.getCompanyId());
        userDomain.setUserName(user.getUserName());
        userDomain.setNickName(user.getNickName());
        userDomain.setRealName(user.getRealName());
        if (CollectionUtils.isNotEmpty(user.getUserDeptList())) {
            List<Long> collect =
                    user.getUserDeptList().stream().map(UserDeptVO::getDeptId).collect(Collectors.toList());
            userDomain.setDeptIdList(collect);
            userDomain.setDataScopeDeptIdList(collect);
            userDomain.setUserDeptList(user.getUserDeptList());
        } else {
            userDomain.setDeptIdList(new ArrayList<>());
            userDomain.setDataScopeDeptIdList(Lists.newArrayList());
            userDomain.setUserDeptList(new ArrayList<>());
        }
        userDomain.setCompanyUuid(user.getCompanyUuid());
        userDomain.setAdminUser(user.getAdminUser());
        userDomain.setUserType(user.getUserType());
        UserUtils.setUser(userDomain);
    }
}
