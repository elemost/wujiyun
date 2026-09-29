package com.wuji.admin.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractUserCompanyConverter;
import com.wuji.admin.converter.AbstractUserConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyChannelTypeEnum;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.UserTypeEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.UserMapper;
import com.wuji.admin.model.domain.CorpCoopDomain;
import com.wuji.admin.model.entity.UserEntity;
import com.wuji.admin.model.request.CorpCoopUserRequest;
import com.wuji.admin.model.request.UserCompanySaveRequest;
import com.wuji.admin.model.request.UserDeptSaveRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.CorpCoopVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.model.vo.UserImportErrorVO;
import com.wuji.admin.model.vo.UserImportVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.CorpCoopUserService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserDeptService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.utils.IdUtils;
import com.wuji.common.utils.PasswordUtil;
import com.wuji.common.utils.StringUtil;
import com.wuji.common.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@DS("slave")
@Slf4j
public class CorpCoopUserServiceImpl implements CorpCoopUserService {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserDeptService userDeptService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCorpCoopUser(CorpCoopUserRequest corpCoopUserRequest) {
        CompanyVO companyName = companyService.getByCompanyName(corpCoopUserRequest.getCompanyName(),
                CompanyChannelTypeEnum.SYSTEM.name());
        Long companyId;
        if (companyName == null) {
            companyId = companyService.createCompany(corpCoopUserRequest.getCompanyName(), null,
                    CompanyChannelTypeEnum.SYSTEM.name());
        } else {
            companyId = companyName.getCompanyId();
        }
        UserVO userVO = userService.queryByMobile(corpCoopUserRequest.getPhonenumber());
        Long userId;
        if (userVO == null) {
            if (companyName != null) {
                throw new AdminException(AdminResultCode.USER_NAME_NOY_EXIST_COMPANY);
            }
            final UserEntity userEntity = AbstractUserConverter.INSTANCE.toEntity(corpCoopUserRequest);
            userEntity.setUserName(corpCoopUserRequest.getPhonenumber());
            userEntity.setCreateBy(corpCoopUserRequest.getPhonenumber());
            userEntity.setStatus(Constants.NORMAL);
            userEntity.setUuid(IdUtils.simpleUUID());
            userEntity.setUserType("06");
            userEntity.setCompanyId(companyId);
            userEntity.setUpdateBy(corpCoopUserRequest.getPhonenumber());
            userEntity.setPassword(PasswordUtil.encryptPassword(corpCoopUserRequest.getPassword()));
            userMapper.insert(userEntity);
            saveCompanyUser(corpCoopUserRequest, userEntity.getUserId(), companyId, userEntity.getStatus(), "00", null,
                    true);
            userId = userEntity.getUserId();
        } else {
            UserCompanyVO userCompanyVO = userCompanyService.getByUserIdAndCompanyId(userVO.getUserId(), companyId);
            if (userCompanyVO == null) {
                throw new AdminException(AdminResultCode.USER_NAME_NOY_EXIST_COMPANY);
            }
            if (userVO.getCompanyId() == null || userVO.getCompanyId() == 0) {
                UserEntity userEntity = new UserEntity();
                userEntity.setUserId(userVO.getUserId());
                userEntity.setCompanyId(companyId);
                userMapper.updateById(userEntity);
            }
            userId = userVO.getUserId();
        }
        UserCompanyVO userCompanyVO =
                userCompanyService.getByUserIdAndCompanyId(userId, UserUtils.getUser().getCompanyId());
        if (userCompanyVO == null) {
            saveCompanyUser(corpCoopUserRequest, userId, UserUtils.getUser().getCompanyId(), "0", "10", companyId,
                    Boolean.FALSE);
        } else {
            throw new AdminException(AdminResultCode.USER_NAME_EXIST);
        }
        Map<String, Long> deptNameToCompanyIdMap = new HashMap<>();
        deptNameToCompanyIdMap.put(corpCoopUserRequest.getCompanyName(), companyId);
        Map<String, Long> companyNameToDeptIdMap =
                departmentService.checkAndCreateDept(Collections.singletonList(corpCoopUserRequest.getCompanyName()),
                        deptNameToCompanyIdMap);
        userDeptService.save(
                Collections.singletonList(companyNameToDeptIdMap.get(corpCoopUserRequest.getCompanyName())), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String uuid) {
        UserVO userVO = userService.queryByUuid(uuid);
        UserCompanyVO userCompanyVO =
                userCompanyService.getByUserIdAndCompanyId(userVO.getUserId(), UserUtils.getUser().getCompanyId());
        if (userCompanyVO == null) {
            return;
        }
        userService.delete(uuid);
        userDeptService.delete(userVO.getUserId());
        List<UserCompanyVO> userCompanyVOList =
                userCompanyService.getBySourceCompany(userCompanyVO.getSourceCompanyId());
        userCompanyVOList = userCompanyVOList.stream()
                .filter(c -> Objects.equals(c.getCompanyId(), UserUtils.getUser().getCompanyId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userCompanyVOList)) {
            departmentService.deleteBySourceCompanyId(userCompanyVO.getSourceCompanyId());
        }
    }

    @Override
    public List<CorpCoopVO> getCorpCompany() {
        List<UserCompanyVO> userCompanyVOList =
                userCompanyService.getBySourceCompany(UserUtils.getUser().getCompanyId());
        List<Long> companyIdList =
                userCompanyVOList.stream().map(UserCompanyVO::getCompanyId).collect(Collectors.toList());
        List<CompanyVO> companyVOS = companyService.getByIds(companyIdList);
        List<CorpCoopVO> corpCoopVOList = new ArrayList<>();
        for (CompanyVO companyVO : companyVOS) {
            CorpCoopVO corpCoopVO = new CorpCoopVO();
            corpCoopVO.setCompanyUuid(companyVO.getCompanyUuid());
            corpCoopVO.setCompanyName(companyVO.getCompanyName());
            corpCoopVO.setCompanyId(companyVO.getCompanyId());
            corpCoopVOList.add(corpCoopVO);
        }
        return corpCoopVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserImportVO importCorp(MultipartFile multipartFile) {
        UserImportVO userImportVO = new UserImportVO();
        List<CorpCoopDomain> importList = new ArrayList<>();
        List<CorpCoopDomain> bindTwoCompanyAndCreateUser = new ArrayList<>();
        List<CorpCoopDomain> bindTwoCompanyList = new ArrayList<>();
        List<CorpCoopDomain> bindTwoCompanyAndCreateCompany = new ArrayList<>();
        List<CorpCoopDomain> bindOneCompanyList = new ArrayList<>();
        Map<String, CompanyVO> companyNameToMap = new HashMap<>();
        boolean importDate = Boolean.FALSE;
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        try {
            List<CorpCoopDomain> userDomainList =
                    EasyExcel.read(multipartFile.getInputStream(), CorpCoopDomain.class, null)
                            .excelType(ExcelTypeEnum.XLSX).sheet(0).headRowNumber(2).autoTrim(false).doReadSync();
            List<String> phoneNumberList =
                    userDomainList.stream().map(CorpCoopDomain::getPhonenumber).filter(StringUtils::isNotEmpty)
                            .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(userDomainList)) {
                return null;
            }
            List<String> companyNameList =
                    userDomainList.stream().map(CorpCoopDomain::getCompanyName).collect(Collectors.toList());
            List<CompanyVO> companyNames =
                    companyService.getByCompanyNames(companyNameList, CompanyChannelTypeEnum.SYSTEM.name());
            companyNameToMap = companyNames.stream().collect(Collectors.toMap(CompanyVO::getCompanyName, c -> c));
            List<UserVO> userVOS = userService.queryByMobilePhone(phoneNumberList);
            Map<String, UserVO> phoneNameToMap =
                    userVOS.stream().collect(Collectors.toMap(UserVO::getPhonenumber, c -> c));
            List<Long> userIdList = userVOS.stream().map(UserVO::getUserId).collect(Collectors.toList());
            List<UserCompanyVO> userCompanyVOList = userCompanyService.getByUserIdsWithoutCompany(userIdList);
            Map<String, UserCompanyVO> companyIdUserIdMap = userCompanyVOList.stream()
                    .collect(Collectors.toMap(c -> c.getCompanyId() + "_" + c.getUserId(), c -> c));
            int i = 0;
            List<UserImportErrorVO> corpCoopImportErrorVOS = new ArrayList<>();
            Map<String, List<CorpCoopDomain>> compantNameToMap =
                    userDomainList.stream().collect(Collectors.groupingBy(CorpCoopDomain::getCompanyName));
            List<String> excelPhoneList = new ArrayList<>();
            for (CorpCoopDomain corpCoopDomain : userDomainList) {
                boolean canImport = Boolean.TRUE;
                if (StringUtils.isEmpty(corpCoopDomain.getCompanyName())) {
                    addErrorMessage(i, "公司名称不能为空", corpCoopImportErrorVOS, false);
                    canImport = Boolean.FALSE;
                }
                if (info.getCompanyName().equals(corpCoopDomain.getCompanyName())) {
                    addErrorMessage(i, "导入公司不能与当前公司相同", corpCoopImportErrorVOS, false);
                    canImport = Boolean.FALSE;
                }
                if (StringUtils.isEmpty(corpCoopDomain.getPhonenumber())) {
                    addErrorMessage(i, "手机号码不能为空", corpCoopImportErrorVOS, false);
                    canImport = Boolean.FALSE;
                }
                if (!StringUtil.isValidPhoneNumber(corpCoopDomain.getPhonenumber())) {
                    addErrorMessage(i, "手机号码错误", corpCoopImportErrorVOS, false);
                    canImport = Boolean.FALSE;
                }
                if (excelPhoneList.contains(corpCoopDomain.getPhonenumber())) {
                    addErrorMessage(i, "excel中存在相同号码", corpCoopImportErrorVOS, false);
                    canImport = Boolean.FALSE;
                }
                excelPhoneList.add(corpCoopDomain.getPhonenumber());
                UserVO userVO = phoneNameToMap.get(corpCoopDomain.getPhonenumber());
                CompanyVO companyVO = companyNameToMap.get(corpCoopDomain.getCompanyName());
                if (userVO != null) {
                    UserCompanyVO userCompanyVO =
                            companyIdUserIdMap.get(UserUtils.getUser().getCompanyId() + "_" + userVO.getUserId());
                    if (userCompanyVO != null) {
                        addErrorMessage(i, "用户已存在", corpCoopImportErrorVOS, false);
                        canImport = Boolean.FALSE;
                    }
                }
                if (companyVO == null) {
                    List<CorpCoopDomain> corpCoopDomains = compantNameToMap.get(corpCoopDomain.getCompanyName());
                    if (CollectionUtils.isNotEmpty(corpCoopDomains)) {
                        if (corpCoopDomains.size() == 1) {
                            corpCoopDomain.setAdmin(Boolean.TRUE);
                        } else {
                            List<CorpCoopDomain> corpCoopDomainList =
                                    corpCoopDomains.stream().filter(c -> "是".equals(c.getAdminString()))
                                            .collect(Collectors.toList());
                            if (corpCoopDomainList.isEmpty()) {
                                addErrorMessage(i, "当前公司不存在超级管理员不可导入", corpCoopImportErrorVOS, false);
                                canImport = Boolean.FALSE;
                            } else if (corpCoopDomainList.size() > 1) {
                                addErrorMessage(i, "当前公司存在多个超级管理员不可导入", corpCoopImportErrorVOS, false);
                                canImport = Boolean.FALSE;
                            }
                        }
                    }
                }
                if (canImport) {
                    importDate = Boolean.TRUE;
                    if (companyVO != null) {
                        if (userVO == null) {
                            bindTwoCompanyAndCreateUser.add(corpCoopDomain);
                        } else {
                            corpCoopDomain.setUuid(userVO.getUuid());
                            UserCompanyVO userCompanyVO =
                                    companyIdUserIdMap.get(companyVO.getCompanyId() + "_" + userVO.getUserId());
                            if (userCompanyVO == null) {
                                bindTwoCompanyList.add(corpCoopDomain);
                            } else {
                                bindOneCompanyList.add(corpCoopDomain);
                            }
                        }
                    } else {
                        if ("是".equals(corpCoopDomain.getAdminString())) {
                            corpCoopDomain.setAdmin(Boolean.TRUE);
                        }
                        if (userVO == null) {
                            importList.add(corpCoopDomain);
                        } else {
                            corpCoopDomain.setUuid(userVO.getUuid());
                            bindTwoCompanyAndCreateCompany.add(corpCoopDomain);
                        }
                    }
                    corpCoopDomain.setRow(i);
                    addErrorMessage(i, "", corpCoopImportErrorVOS, true);

                }
                i++;
            }
            userImportVO.setErrorList(corpCoopImportErrorVOS);
        } catch (Exception e) {
            log.error("解析excel失败", e);
        }
        if (importDate) {
            // 创建公司
            List<String> companyNameList = saveCompany(importList, bindTwoCompanyAndCreateCompany, companyNameToMap);
            Map<String, Long> companyNameToIdMap = companyNameToMap.values().stream()
                    .collect(Collectors.toMap(CompanyVO::getCompanyName, CompanyVO::getCompanyId));

            List<UserPullVO> importUserPullList = buildUserPull(importList, companyNameToIdMap);
            List<UserPullVO> bindTwoCompanyAndCreateUserPullList =
                    buildUserPull(bindTwoCompanyAndCreateUser, companyNameToIdMap);
            List<UserPullVO> bindTwoCompanyPullList = buildUserPull(bindTwoCompanyList, companyNameToIdMap);
            List<UserPullVO> bindTwoCompanyAndCreateCompanyPullList =
                    buildUserPull(bindTwoCompanyAndCreateCompany, companyNameToIdMap);
            List<UserPullVO> bindOneCompanyListPullList = buildUserPull(bindOneCompanyList, companyNameToIdMap);

            // 创建用户
            List<UserPullVO> allUserPullVO = new ArrayList<>();
            allUserPullVO.addAll(importUserPullList);
            allUserPullVO.addAll(bindTwoCompanyAndCreateUserPullList);
            allUserPullVO.addAll(bindTwoCompanyPullList);
            allUserPullVO.addAll(bindTwoCompanyAndCreateCompanyPullList);
            allUserPullVO.addAll(bindOneCompanyListPullList);
            Map<String, UserEntity> insertUserUuidToMap =
                    userService.saveUserBatchByPhone(allUserPullVO, UserUtils.getUser(),
                            CompanyDataSourceEnum.EXTERNAL_IMPORT.name());

            // 关联内部公司关系
            // 创建用户
            List<UserPullVO> bindInternalList = new ArrayList<>();
            bindInternalList.addAll(importUserPullList);
            bindInternalList.addAll(bindTwoCompanyAndCreateUserPullList);
            bindInternalList.addAll(bindTwoCompanyPullList);
            bindInternalList.addAll(bindTwoCompanyAndCreateCompanyPullList);
            saveUserCompanyList(bindInternalList, null, insertUserUuidToMap, UserTypeEnum.INTERNAL.getCode());

            saveUserCompanyList(allUserPullVO, null, insertUserUuidToMap, UserTypeEnum.EXTERNAL.getCode());

            Map<String, Long> deptNameToIdMap =
                    departmentService.checkAndCreateDept(new ArrayList<>(companyNameToIdMap.keySet()),
                            companyNameToIdMap);
            List<Long> userIdList = new ArrayList<>();
            List<UserDeptSaveRequest> userDeptSaveRequests = new ArrayList<>();
            for (UserPullVO userPullVO : allUserPullVO) {
                UserEntity userEntity = insertUserUuidToMap.get(userPullVO.getUuid());
                Long deptId = deptNameToIdMap.get(userPullVO.getCompanyName());
                if (userEntity != null && deptId != null) {
                    UserDeptSaveRequest userDeptSaveRequest = new UserDeptSaveRequest();
                    userDeptSaveRequest.setUserId(userEntity.getUserId());
                    userDeptSaveRequest.setDeptId(deptId);
                    userDeptSaveRequests.add(userDeptSaveRequest);
                    userIdList.add(userEntity.getUserId());
                }
            }
            userDeptService.save(userDeptSaveRequests, userIdList);
        }
        return userImportVO;
    }

    private List<String> saveCompany(List<CorpCoopDomain> importList,
                                     List<CorpCoopDomain> bindTwoCompanyAndCreateCompany,
                                     Map<String, CompanyVO> companyNameToMap) {
        List<String> companyNameList =
                importList.stream().map(CorpCoopDomain::getCompanyName).collect(Collectors.toList());
        companyNameList.addAll(bindTwoCompanyAndCreateCompany.stream().map(CorpCoopDomain::getCompanyName)
                .collect(Collectors.toList()));
        companyNameList = companyNameList.stream().distinct().collect(Collectors.toList());
        Map<String, CompanyVO> saveCompanyMap = companyService.saveBatchCompany(companyNameList);
        companyNameToMap.putAll(saveCompanyMap);
        return companyNameList;
    }

    private static List<UserPullVO> buildUserPull(List<CorpCoopDomain> importList,
                                                  Map<String, Long> companyNameToIdMap) {
        List<UserPullVO> userPullList = new ArrayList<>();
        for (CorpCoopDomain corpCoopDomain : importList) {
            UserPullVO userPullVO = new UserPullVO();
            userPullVO.setCompanyName(corpCoopDomain.getCompanyName());
            userPullVO.setPhonenumber(corpCoopDomain.getPhonenumber());
            userPullVO.setUserName(corpCoopDomain.getPhonenumber());
            userPullVO.setNickName(corpCoopDomain.getNickName());
            userPullVO.setEmail(corpCoopDomain.getEmail());
            userPullVO.setSourceCompanyId(companyNameToIdMap.get(corpCoopDomain.getCompanyName()));
            userPullVO.setUuid(corpCoopDomain.getUuid());
            userPullVO.setAdmin(corpCoopDomain.getAdmin());
            userPullList.add(userPullVO);
        }
        return userPullList;
    }

    public void saveUserCompanyList(List<UserPullVO> userDingVOS, CompanyVO info, Map<String, UserEntity> userIdMap,
                                    String userType) {
        List<UserCompanySaveRequest> userCompanySaveRequestList = new ArrayList<>();
        for (UserPullVO userPullVO : userDingVOS) {
            UserEntity userEntity = userIdMap.get(userPullVO.getUuid());
            UserCompanySaveRequest userCompanySaveRequest = AbstractUserCompanyConverter.INSTANCE.toRequest(userPullVO);
            userCompanySaveRequest.setDingUnionId(userPullVO.getUnionid());
            if (UserTypeEnum.INTERNAL.getCode().equals(userType)) {
                userCompanySaveRequest.setCompanyId(userPullVO.getSourceCompanyId());
                userCompanySaveRequest.setSourceCompanyId(null);
                userCompanySaveRequest.setAdminUser(userPullVO.getAdmin());
            } else {
                userCompanySaveRequest.setCompanyId(UserUtils.getUser().getCompanyId());
            }
            userCompanySaveRequest.setUserId(userEntity.getUserId());
            userCompanySaveRequest.setUserType(userType);
            userCompanySaveRequest.setDingThirdId(userPullVO.getDingThirdId());
            if (info != null) {
                userCompanySaveRequest.setThirdType(info.getDataSource());
            }
            userCompanySaveRequestList.add(userCompanySaveRequest);
        }
        userCompanyService.onlySaveBatch(userCompanySaveRequestList);
    }

    @Override
    public void template(HttpServletResponse response) {
        try (InputStream is = ResourceReader.class.getResourceAsStream("/template/corpCoopTemplate.xlsx");
             ServletOutputStream outputStream = response.getOutputStream()) {
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("互联组织.xlsx", "UTF-8"));
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            IOUtils.copy(is, outputStream);
        } catch (Exception e) {
            log.error("生成模板错误", e);
        }
    }

    private static void addErrorMessage(int i, String errorMessage, List<UserImportErrorVO> corpCoopImportList,
                                        Boolean success) {
        UserImportErrorVO corpCoopImportErrorVO = new UserImportErrorVO();
        corpCoopImportErrorVO.setRow(i + 1);
        corpCoopImportErrorVO.setErrorMessage(errorMessage);
        corpCoopImportErrorVO.setSuccess(success);
        corpCoopImportList.add(corpCoopImportErrorVO);
    }

    private void saveCompanyUser(CorpCoopUserRequest corpCoopUserRequest, Long userId, Long companyId, String status,
                                 String userType, Long sourceCompanyId, Boolean adminUser) {
        UserCompanySaveRequest userCompanySaveRequest = new UserCompanySaveRequest();
        userCompanySaveRequest.setUserId(userId);
        userCompanySaveRequest.setCompanyId(companyId);
        userCompanySaveRequest.setNickName(corpCoopUserRequest.getNickName());
        userCompanySaveRequest.setUserType(userType);
        userCompanySaveRequest.setStatus(status);
        userCompanySaveRequest.setAdminUser(adminUser);
        userCompanySaveRequest.setSourceCompanyId(sourceCompanyId);
        userCompanyService.checkAndSave(userCompanySaveRequest, Boolean.TRUE);
    }
}
