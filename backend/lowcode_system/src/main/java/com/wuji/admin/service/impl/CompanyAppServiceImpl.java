package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractCompanyAppConverter;
import com.wuji.admin.enums.CompanyInfoKeyEnum;
import com.wuji.admin.mapper.CompanyAppMapper;
import com.wuji.admin.model.domain.OrderItemConfigDomain;
import com.wuji.admin.model.entity.CompanyAppEntity;
import com.wuji.admin.model.request.SmsSendRequest;
import com.wuji.admin.model.vo.ClientFunctionVO;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.model.vo.CompanyAppVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.DictDataVO;
import com.wuji.admin.model.vo.UserCompanyVO;
import com.wuji.admin.service.AdminMessageService;
import com.wuji.admin.service.ClientFunctionService;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DictDataService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.UserService;
import com.wuji.common.api.SaleOrderApi;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.enums.DictDataTypeEnum;
import com.wuji.common.enums.InMailMessageTypeEnum;
import com.wuji.common.enums.InMailSourceTypeEnum;
import com.wuji.common.model.request.MessageInsertRequest;
import com.wuji.common.model.vo.UserVO;
import com.wuji.common.properties.SystemProperties;
import com.wuji.common.service.MessageCommonService;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.systemapi.service.CompanyAppOpenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 * 企业使用产品限制 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@Service
@DS("slave")
@Slf4j
public class CompanyAppServiceImpl extends ServiceImpl<CompanyAppMapper, CompanyAppEntity>
        implements CompanyAppService {

    @Autowired
    private CompanyAppMapper companyAppMapper;

    @Autowired
    private ClientFunctionService clientFunctionService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private MessageCommonService messageService;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private AdminMessageService adminMessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemProperties systemProperties;

    @Autowired
    private ThreadPoolExecutor mongoTaskExecutor;

    @Autowired
    private SaleOrderApi saleOrderApi;

    @Autowired
    private CompanyAppOpenService companyAppOpenService;

    private final String clientId = DictDataTypeEnum.ELECLOUD.name().toLowerCase();

    @Override
    public CompanyAppDetailVO getCurrent() {

        CompanyAppDetailVO companyAppDetailVO = new CompanyAppDetailVO();
        LambdaQueryWrapper<CompanyAppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyAppEntity::getOrgId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(CompanyAppEntity::getClientId, clientId);
        List<CompanyAppEntity> companyAppEntities = companyAppMapper.selectList(queryWrapper);
        List<DictDataVO> dataVOList = dictDataService.getByType(DictDataTypeEnum.ELECLOUD.getDictType());
        Map<String, String> dictMap =
                dataVOList.stream().collect(Collectors.toMap(DictDataVO::getDictValue, DictDataVO::getDictLabel));
        if (CollectionUtils.isEmpty(companyAppEntities)) {
            CompanyAppVO companyAppVO = new CompanyAppVO();
            companyAppVO.setStartTime(new Date());
            companyAppVO.setEndTime(TimeUtils.getMonthZero(new Date(), 1, null));
            DictDataVO dictDataVO =
                    dataVOList.stream().filter(c -> c.getIsDefault().equals("Y")).findFirst().orElse(null);
            if (dictDataVO != null) {
                companyAppVO.setEquityId(dictDataVO.getDictValue());
                companyAppVO.setEquityName(dictDataVO.getDictLabel());
                companyAppVO.setRemark(dictDataVO.getRemark());
            }
            companyAppDetailVO.setCompanyAppVOList(Collections.singletonList(companyAppVO));
            companyAppDetailVO.setCompanyAppVO(companyAppVO);
            List<ClientFunctionVO> clientFunctionVOS = clientFunctionService.getByClientId(clientId,
                    Collections.singletonList(companyAppVO.getEquityId()));
            companyAppDetailVO.setFunctionList(
                    clientFunctionVOS.stream().map(ClientFunctionVO::getPermissionKey).collect(Collectors.toList()));
            Map<String, ClientFunctionVO> clientFunctionMap = new HashMap<>();
            for (ClientFunctionVO clientFunctionVO : clientFunctionVOS) {
                clientFunctionMap.put(clientFunctionVO.getPermissionKey(), clientFunctionVO);
            }
            companyAppDetailVO.setClientFunctionMap(clientFunctionMap);
        } else {
            CompanyAppVO companyAppVO = AbstractCompanyAppConverter.INSTANCE.toVO(companyAppEntities.get(0));
            local(companyAppDetailVO, companyAppVO, companyAppEntities.get(0));
            if (new Date().after(companyAppVO.getEndTime())) {
                companyAppDetailVO.setExpire(Boolean.TRUE);
                companyAppVO.setStartTime(new Date());
                companyAppVO.setEndTime(TimeUtils.getMonthZero(new Date(), 1, null));
                DictDataVO dictDataVO =
                        dataVOList.stream().filter(c -> c.getIsDefault().equals("Y")).findFirst().orElse(null);
                if (dictDataVO != null) {
                    companyAppVO.setEquityId(dictDataVO.getDictValue());
                    companyAppVO.setEquityName(dictDataVO.getDictLabel());
                    companyAppVO.setRemark(dictDataVO.getRemark());
                }
                companyAppDetailVO.setCompanyAppVOList(Collections.singletonList(companyAppVO));
                companyAppDetailVO.setCompanyAppVO(companyAppVO);
                List<ClientFunctionVO> clientFunctionVOS =
                        clientFunctionService.getByClientId(DictDataTypeEnum.ELECLOUD.name().toLowerCase(),
                                Collections.singletonList(companyAppVO.getEquityId()));
                companyAppDetailVO.setFunctionList(clientFunctionVOS.stream().map(ClientFunctionVO::getPermissionKey)
                        .collect(Collectors.toList()));
                Map<String, ClientFunctionVO> clientFunctionMap = new HashMap<>();
                for (ClientFunctionVO clientFunctionVO : clientFunctionVOS) {
                    clientFunctionMap.put(clientFunctionVO.getPermissionKey(), clientFunctionVO);
                }
                companyAppDetailVO.setClientFunctionMap(clientFunctionMap);
            } else {
                companyAppVO.setEquityName(dictMap.get(companyAppVO.getEquityId()));
                companyAppDetailVO.setCompanyAppVO(companyAppVO);
                DictDataVO dictDataVO =
                        dataVOList.stream().filter(c -> c.getDictValue().equals(companyAppVO.getEquityId())).findFirst().orElse(null);
                if (dictDataVO != null) {
                    companyAppVO.setRemark(dictDataVO.getRemark());
                }
                if (CollectionUtils.isNotEmpty(companyAppEntities)) {
                    List<String> clientIdList =
                            companyAppEntities.stream().map(CompanyAppEntity::getEquityId).collect(Collectors.toList());
                    List<ClientFunctionVO> clientFunctionVOS =
                            clientFunctionService.getByClientId(DictDataTypeEnum.ELECLOUD.name().toLowerCase(),
                                    clientIdList);
                    companyAppDetailVO.setFunctionList(
                            clientFunctionVOS.stream().map(ClientFunctionVO::getPermissionKey)
                                    .collect(Collectors.toList()));
                    Map<String, ClientFunctionVO> clientFunctionMap = new HashMap<>();
                    for (ClientFunctionVO clientFunctionVO : clientFunctionVOS) {
                        clientFunctionMap.put(clientFunctionVO.getPermissionKey(), clientFunctionVO);
                    }
                    companyAppDetailVO.setClientFunctionMap(clientFunctionMap);
                }
            }
        }
        CompanyVO companyVO = companyService.info(UserUtils.getUser().getCompanyId());
        Map<String, ClientFunctionVO> clientFunctionMap = companyAppDetailVO.getClientFunctionMap();
        if ("-1".equals(systemProperties.getLimitCount())) {
            clientFunctionMap.forEach((key, value) -> {
                Map<String, String> infoMap = companyVO.getInfoMap();
                String limitCount = infoMap.get(key);
                if (limitCount != null) {
                    if (value.getLimitCount() != null) {
                        if (value.getLimitCount() < Integer.parseInt(limitCount)) {
                            value.setLimitCount(Integer.parseInt(limitCount));
                            clientFunctionMap.put(key, value);
                        }
                    }
                }
            });
        } else {
            String decrypt = AESUtils.decrypt(Constants.PASSWORD_KEY.getBytes(), systemProperties.getLimitCount());
            ClientFunctionVO clientFunctionVO = new ClientFunctionVO();
            clientFunctionVO.setLimitCount(Integer.parseInt(decrypt));
            clientFunctionVO.setPermissionName("用户数量控制");
            clientFunctionVO.setPermissionKey(CompanyInfoKeyEnum.userLimit.name());
            clientFunctionVO.setClientId("clientId");
            clientFunctionMap.put(clientFunctionVO.getPermissionKey(), clientFunctionVO);
        }

        CompanyAppCache.cache(UserUtils.getUser().getCompanyId(), companyAppDetailVO);
        return companyAppDetailVO;
    }

    @Override
    public CompanyAppDetailVO getCurrentClient() {
        String detail = companyAppOpenService.getCompanyAppDetail();
        return JSONObject.parseObject(detail, CompanyAppDetailVO.class);
    }

    private void local(CompanyAppDetailVO companyAppDetailVO, CompanyAppVO companyAppVO,
                       CompanyAppEntity companyAppEntity) {
        if (!Constants.ENV.equals(systemProperties.getEnv())) {
            String value = ConfigCache.getValue(ConfigEnum.LOWCODE_START_TIME.name());
            if (StringUtils.isEmpty(value)) {
                companyAppDetailVO.setCanUsed(Boolean.FALSE);
                return;
            }
            String decrypt = AESUtils.decrypt(Constants.PASSWORD_KEY.getBytes(), value);
            if (decrypt == null) {
                companyAppDetailVO.setCanUsed(Boolean.FALSE);
                return;
            }
            companyAppVO.setStartTime(new Date(Long.parseLong(decrypt)));
            companyAppVO.setEndTime(TimeUtils.getMonthZero(companyAppVO.getStartTime(), 1, null));
            companyAppVO.setEquityId("elecloud_ee");
            companyAppVO.setClientId(clientId);

            companyAppEntity.setStartTime(new Date(Long.parseLong(decrypt)));
            companyAppEntity.setEndTime(TimeUtils.getMonthZero(companyAppEntity.getStartTime(), 1, null));
            companyAppEntity.setEquityId("elecloud_ee");
            companyAppEntity.setClientId(clientId);
        }
    }

    @Override
    public void saveDefault(Long companyId) {
        CompanyAppEntity wjCompanyApp = new CompanyAppEntity();
        wjCompanyApp.setClientId(clientId);
        wjCompanyApp.setOrgId(companyId);
        wjCompanyApp.setStartTime(new Date());
        wjCompanyApp.setEndTime(TimeUtils.getDataZero(new Date(), 30));
        wjCompanyApp.setEquityId("elecloud_pro_free");
        companyAppMapper.insert(wjCompanyApp);
    }

    @Override
    public void expire() {
        Integer day = -1;
        List<CompanyAppEntity> companyAppEntities = companyAppMapper.trialExpirationReminder(day);
        if (CollectionUtils.isEmpty(companyAppEntities)) {
            return;
        }
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        List<Long> companyIdList =
                companyAppEntities.stream().map(CompanyAppEntity::getOrgId).collect(Collectors.toList());
        List<UserCompanyVO> adminList = userCompanyService.getAdminList(companyIdList);
        List<Long> userIdList = adminList.stream().map(UserCompanyVO::getUserId).collect(Collectors.toList());
        List<UserVO> userVOS = userService.infoOnly(userIdList);
        List<String> phoneNumberList = userVOS.stream().map(UserVO::getPhonenumber).collect(Collectors.toList());
        SmsSendRequest sendRequest = new SmsSendRequest.Builder().setParams(paramMap).setMobileList(phoneNumberList)
                .setSmsScene("版本试用期体验提醒").build();
        adminMessageService.sendSms(sendRequest);
    }

    @Override
    public List<CompanyAppVO> beenUsedDays(int day) {
        List<CompanyAppEntity> companyAppEntities = companyAppMapper.trialBeenUsedDays(day);
        return companyAppEntities.stream().map(AbstractCompanyAppConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CompanyAppVO> getByCompanyId(List<Long> companyIdList) {
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<CompanyAppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyAppEntity::getClientId, clientId);
        queryWrapper.in(CompanyAppEntity::getOrgId, companyIdList);
        return companyAppMapper.selectList(queryWrapper).stream().map(AbstractCompanyAppConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyAppVO getCurrentInfo() {
        LambdaQueryWrapper<CompanyAppEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyAppEntity::getClientId, clientId);
        queryWrapper.eq(CompanyAppEntity::getOrgId, UserUtils.getUser().getCompanyId());
        return AbstractCompanyAppConverter.INSTANCE.toVO(companyAppMapper.selectOne(queryWrapper));
    }

    @Override
    public void updateVersion(String config, Integer orderPeriod) {
        List<OrderItemConfigDomain> configDomains = JSON.parseArray(config, OrderItemConfigDomain.class);
        for (OrderItemConfigDomain orderItemConfigDomain : configDomains) {
            LambdaQueryWrapper<CompanyAppEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CompanyAppEntity::getClientId, clientId);
            queryWrapper.eq(CompanyAppEntity::getOrgId, UserUtils.getUser().getCompanyId());
            queryWrapper.orderByDesc(CompanyAppEntity::getId);
            queryWrapper.last(" limit 1");
            CompanyAppEntity companyAppEntity = companyAppMapper.selectOne(queryWrapper);
            if (companyAppEntity == null) {
                companyAppEntity = new CompanyAppEntity();
                companyAppEntity.setEquityId(orderItemConfigDomain.getEquityId());
                companyAppEntity.setStartTime(new Date());
                companyAppEntity.setEndTime(TimeUtils.dateAddDayWithLastSecondAndUnit(new Date(), orderPeriod,
                        orderItemConfigDomain.getDayUnit()));
                companyAppEntity.setOrgId(UserUtils.getUser().getCompanyId());
                companyAppEntity.setClientId(DictDataTypeEnum.ELECLOUD.getDictType());
                companyAppMapper.insert(companyAppEntity);
            } else {
                companyAppEntity.setEndTime(
                        TimeUtils.dateAddDayWithLastSecondAndUnit(companyAppEntity.getEndTime(), orderPeriod,
                                orderItemConfigDomain.getDayUnit()));
                companyAppEntity.setEquityId(orderItemConfigDomain.getEquityId());
                companyAppMapper.updateById(companyAppEntity);
            }
            if (StringUtils.isNotEmpty(orderItemConfigDomain.getApplicationId())) {
                mongoTaskExecutor.execute(() -> {
                    saleOrderApi.generateTemplate(orderItemConfigDomain.getApplicationId(), orderPeriod,
                            UserUtils.getUser());
                });
            }
        }
    }

    private void sendMessage(CompanyAppEntity companyAppEntity, String message) {
        MessageInsertRequest messageInsertRequest = new MessageInsertRequest();
        messageInsertRequest.setMessageType(InMailMessageTypeEnum.VIP_EXPIRE.name());
        messageInsertRequest.setSource(InMailSourceTypeEnum.SYSTEM.name());
        messageInsertRequest.setContent(message);
        messageInsertRequest.setCompanyId(companyAppEntity.getOrgId());
        Long admin = userCompanyService.getAdmin(companyAppEntity.getOrgId());
        messageInsertRequest.setUserIdList(Collections.singletonList(admin));
        messageService.insert(messageInsertRequest);
    }
}
