package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.context.UploadFileContext;
import com.wuji.common.properties.SystemProperties;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormImgConverter;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.mapper.FormImgMapper;
import com.wuji.service.model.entity.FormImgEntity;
import com.wuji.service.model.request.FormImgRequest;
import com.wuji.service.model.vo.FileVO;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.service.CommonService;
import com.wuji.service.service.FormImgService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
@Service
public class FormImgServiceImpl extends ServiceImpl<FormImgMapper, FormImgEntity> implements FormImgService {

    @Autowired
    private FormImgMapper formImgMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UploadFileContext uploadFileContext;

    @Autowired
    private SystemProperties systemProperties;

    @Override
    public String insert(FormImgRequest formImgRequest) {
        FormImgEntity formImgEntity = AbstractFormImgConverter.INSTANCE.toEntity(formImgRequest);
        formImgEntity.setCreator(UserUtils.getUser().getUserId());
        formImgEntity.setModifier(UserUtils.getUser().getUserId());
        if (StringUtils.isEmpty(formImgEntity.getId())) {
            formImgEntity.setId(ObjectId.getGuid());
        }
        formImgEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        formImgMapper.insert(formImgEntity);
        return formImgEntity.getId();
    }

    @Override
    public FormImgVO getImg(String id) {
        FormImgEntity formImgEntity = formImgMapper.selectById(id);
        if (formImgEntity == null) {
            return null;
        }
        String imgUrl = uploadFileContext.getHandler(systemProperties.getUploadType())
                .downloadFile(formImgEntity.getImgKey(), formImgEntity.getBucket());
        formImgEntity.setImgUrl(imgUrl);
        return AbstractFormImgConverter.INSTANCE.toVO(formImgEntity);
    }

    @Override
    public FormImgVO upload(MultipartFile file, FileTypeEnum fileTypeEnum) {
        FileVO fileVO = commonService.uploadFile(file, fileTypeEnum, Boolean.FALSE);
        return AbstractFormImgConverter.INSTANCE.toVO(fileVO);
    }

    @Override
    public List<FormImgVO> getByIdList(List<String> ids) {
        LambdaQueryWrapper<FormImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormImgEntity::getId, ids);
        List<FormImgEntity> formImgEntities = formImgMapper.selectList(queryWrapper);
        return formImgEntities.stream().map(AbstractFormImgConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<FormImgVO> getByIdListOpen(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormImgEntity::getId, ids);
        queryWrapper.eq(FormImgEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<FormImgEntity> formImgEntities = formImgMapper.selectList(queryWrapper);
        for (FormImgEntity formImgEntity : formImgEntities) {
            String imgUrl = uploadFileContext.getHandler(systemProperties.getUploadType())
                    .downloadFile(formImgEntity.getImgKey(), formImgEntity.getBucket());
            formImgEntity.setImgUrl(imgUrl);
        }
        return formImgEntities.stream().map(AbstractFormImgConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public FormImgVO getByImg(String imgUrl) {
        LambdaQueryWrapper<FormImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormImgEntity::getImgUrl, imgUrl);
        FormImgEntity formImgEntity = formImgMapper.selectOne(queryWrapper);
        return AbstractFormImgConverter.INSTANCE.toVO(formImgEntity);
    }

    @Override
    public List<FormImgVO> getByImgList(List<String> imgUrlList) {
        if (CollectionUtils.isEmpty(imgUrlList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormImgEntity::getImgUrl, imgUrlList);
        List<FormImgEntity> formImgEntityList = formImgMapper.selectList(queryWrapper);
        return formImgEntityList.stream().map(AbstractFormImgConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<FormImgVO> getByType(String imgType) {
        LambdaQueryWrapper<FormImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormImgEntity::getImgType, imgType);
        List<FormImgEntity> formImgEntityList = formImgMapper.selectList(queryWrapper);
        return formImgEntityList.stream().map(AbstractFormImgConverter.INSTANCE::toVO).collect(Collectors.toList());
    }
}
