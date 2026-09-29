package com.wuji.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.IdUtils;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.platform.converter.AbstractSecretConverter;
import com.wuji.platform.enums.SecretStateEnum;
import com.wuji.platform.mapper.SecretMapper;
import com.wuji.platform.model.entity.SecretEntity;
import com.wuji.platform.model.request.SecretGenerateRequest;
import com.wuji.platform.model.request.SecretRemarkRequest;
import com.wuji.platform.model.vo.SecretVO;
import com.wuji.platform.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-06
 */
@Service
public class SecretServiceImpl extends ServiceImpl<SecretMapper, SecretEntity> implements SecretService {

    @Autowired
    private SecretMapper secretMapper;

    @Override
    public SecretVO getSecret(String appKey) {
        LambdaQueryWrapper<SecretEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecretEntity::getAppKey, appKey);
        queryWrapper.eq(SecretEntity::getDeleted, Boolean.FALSE);
        SecretEntity secretEntity = secretMapper.selectOne(queryWrapper);
        return AbstractSecretConverter.INSTANCE.toVO(secretEntity);
    }

    @Override
    public void generate(SecretGenerateRequest secretGenerateRequest) {
        UserDomain user = UserUtils.getUser();
        SecretEntity secretEntity = new SecretEntity();
        secretEntity.setId(ObjectId.getGuid());
        secretEntity.setAppKey(IdUtils.simpleUUID());
        secretEntity.setAppSecret(IdUtils.simpleUUID());
        secretEntity.setCreator(user.getNickName());
        secretEntity.setState(SecretStateEnum.OPEN.name());
        secretEntity.setModifier(user.getNickName());
        secretEntity.setRemark(secretGenerateRequest.getRemark());
        secretEntity.setCompanyId(user.getCompanyId());
        secretMapper.insert(secretEntity);
    }

    @Override
    public void delete(String id) {
        UserDomain user = UserUtils.getUser();
        SecretEntity secretEntity = new SecretEntity();
        secretEntity.setId(id);
        secretEntity.setDeleted(Boolean.TRUE);
        secretEntity.setModifier(user.getNickName());
        secretMapper.updateById(secretEntity);
    }

    @Override
    public void updateState(String id, String state) {
        UserDomain user = UserUtils.getUser();
        SecretEntity secretEntity = new SecretEntity();
        secretEntity.setId(id);
        secretEntity.setState(state);
        secretEntity.setModifier(user.getNickName());
        secretMapper.updateById(secretEntity);
    }

    @Override
    public void remark(SecretRemarkRequest secretRemarkRequest) {
        UserDomain user = UserUtils.getUser();
        SecretEntity secretEntity = new SecretEntity();
        secretEntity.setId(secretRemarkRequest.getId());
        secretEntity.setRemark(secretRemarkRequest.getRemark());
        secretEntity.setModifier(user.getNickName());
        secretMapper.updateById(secretEntity);
    }

    @Override
    public List<SecretVO> currentCompanyList() {
        LambdaQueryWrapper<SecretEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SecretEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(SecretEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(SecretEntity::getCreateTime);
        List<SecretEntity> secretEntities = secretMapper.selectList(queryWrapper);
        return secretEntities.stream().map(AbstractSecretConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
