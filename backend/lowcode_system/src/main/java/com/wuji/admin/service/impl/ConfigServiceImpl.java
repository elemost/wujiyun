package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractConfigConverter;
import com.wuji.admin.mapper.ConfigMapper;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.entity.ConfigEntity;
import com.wuji.common.model.request.ConfigSaveRequest;
import com.wuji.common.model.vo.ConfigVO;
import com.wuji.common.properties.SystemProperties;
import com.wuji.common.service.ConfigService;
import com.wuji.common.utils.AESUtils;
import com.wuji.common.utils.PublicIpUtil;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-05-09
 */
@Service
@DS("slave")
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigEntity> implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public void saveOrUpdate(ConfigSaveRequest configSaveRequest) {
        ConfigEntity configEntity = AbstractConfigConverter.INSTANCE.toEntity(configSaveRequest);
        UserDomain user = UserUtils.getUser();
        configEntity.setCreateBy(user.getUserName());
        configEntity.setCreateTime(new Date());
        configEntity.setUpdateBy(user.getUserName());
        configEntity.setUpdateTime(new Date());
        saveOrUpdate(configEntity);
        ConfigCache.clear(configSaveRequest.getConfigKey());
    }

    @Override
    public ConfigVO detailById(Long id) {
        ConfigEntity configEntity = configMapper.selectById(id);
        return AbstractConfigConverter.INSTANCE.toVO(configEntity);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public ConfigVO detailByKey(String configKey) {
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConfigEntity::getConfigKey, configKey);
        queryWrapper.last(" limit 1");
        ConfigEntity configEntity = configMapper.selectOne(queryWrapper);
        return AbstractConfigConverter.INSTANCE.toVO(configEntity);
    }

    @Override
    public void init() {
        ConfigEnum[] values = ConfigEnum.values();
        if (values.length == 0) {
            return;
        }
        LambdaQueryWrapper<ConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        final List<ConfigEntity> configEntities = configMapper.selectList(queryWrapper);
        final List<String> configKeyList =
                configEntities.stream().map(ConfigEntity::getConfigKey).collect(Collectors.toList());
        List<ConfigEntity> configEntityList = new ArrayList<>();
        for (ConfigEnum configEnum : values) {
            if (configKeyList.contains(configEnum.name())) {
                continue;
            }
            if (configEnum == ConfigEnum.LOWCODE_START_TIME) {
                if (!Constants.ENV.equals(systemProperties.getEnv())) {
                    String configValue = AESUtils.encrypt(Constants.PASSWORD_KEY, new Date().getTime() + "");
                    buildConfigEntity(configEnum, configValue, configEntityList);
                }
            } else if (configEnum == ConfigEnum.LOCAL_EXTERNAL_URL) {
                buildConfigEntity(configEnum, PublicIpUtil.getPublicIp(), configEntityList);
            } else if (configEnum == ConfigEnum.LOCAL_WEB_PORT) {
                buildConfigEntity(configEnum, PublicIpUtil.getPublicIpPort(), configEntityList);
            } else if (configEnum == ConfigEnum.FILE_URL) {
                String fileUrl = String.format("http://%s:%s/miniio-file", PublicIpUtil.getPublicIp(),
                        PublicIpUtil.getPublicIpPort());
                buildConfigEntity(configEnum, fileUrl, configEntityList);
            } else {
                buildConfigEntity(configEnum, configEnum.getDefaultValue(), configEntityList);
            }
        }
        if (CollectionUtils.isNotEmpty(configEntityList)) {
            saveBatch(configEntityList);
        }
    }

    private void buildConfigEntity(ConfigEnum configEnum, String configValue, List<ConfigEntity> configEntityList) {
        ConfigEntity configEntity = new ConfigEntity();
        configEntity.setConfigKey(configEnum.name());
        configEntity.setConfigName(configEnum.getDesc());
        configEntity.setConfigValue(configValue);
        configEntity.setRemark(configEnum.getDesc());
        configEntity.setCreateTime(new Date());
        configEntity.setUpdateTime(new Date());
        configEntityList.add(configEntity);
    }
}
