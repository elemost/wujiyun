package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.converter.AbstractPostConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.PostMapper;
import com.wuji.admin.model.entity.PostEntity;
import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.admin.service.PostService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 岗位信息表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
@Service
@DS("slave")
public class PostServiceImpl extends ServiceImpl<PostMapper, PostEntity> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public String create(PostCreateRequest postCreateRequest) {
        checkSameName(null, postCreateRequest.getPostName());
        PostEntity postEntity = AbstractPostConverter.INSTANCE.toEntity(postCreateRequest);
        postEntity.setPostCode(ObjectId.getGuid());
        postEntity.setCreateBy(UserUtils.getUser().getUserName());
        postEntity.setUpdateBy(UserUtils.getUser().getUserName());
        postEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        postEntity.setCreateTime(new Date());
        postEntity.setUpdateTime(new Date());
        postEntity.setStatus(Constants.NORMAL);
        postMapper.insert(postEntity);
        return postEntity.getPostCode();
    }

    private void checkSameName(Long postId, String postName) {
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostEntity::getPostName, postName);
        queryWrapper.ne(postId != null, PostEntity::getPostId, postId);
        queryWrapper.eq(PostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(PostEntity::getDelFlag, Constants.NU_DELETED);
        PostEntity postEntity = postMapper.selectOne(queryWrapper);
        if (postEntity != null) {
            throw new AdminException(AdminResultCode.POST_NAME_EXIST);
        }
    }

    @Override
    public void update(PostUpdateRequest postUpdateRequest) {
        checkSameName(postUpdateRequest.getPostId(), postUpdateRequest.getPostName());
        PostEntity postEntity = AbstractPostConverter.INSTANCE.toEntity(postUpdateRequest);
        postEntity.setUpdateBy(UserUtils.getUser().getUserName());
        postEntity.setUpdateTime(new Date());
        postMapper.updateById(postEntity);
    }

    @Override
    public QueryPageVO<PostVO> selectList(PostSelectRequest postSelectRequest) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostEntity::getCompanyId, user.getCompanyId());
        queryWrapper.in(CollectionUtils.isNotEmpty(postSelectRequest.getPostIdList()), PostEntity::getPostId,
                postSelectRequest.getPostIdList());
        queryWrapper.eq(PostEntity::getStatus, Constants.NORMAL);
        queryWrapper.eq(PostEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.like(StringUtils.isNotEmpty(postSelectRequest.getPostName()), PostEntity::getPostName,
                postSelectRequest.getPostName());
        queryWrapper.orderByDesc(PostEntity::getPostSort, PostEntity::getCreateTime);
        Page<PostEntity> postEntityPage =
                postMapper.selectPage(new Page<>(postSelectRequest.getPageNum(), postSelectRequest.getPageSize()),
                        queryWrapper);
        List<PostEntity> records = postEntityPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageUtils.toQueryPage(postEntityPage, new ArrayList<>());
        }
        List<PostVO> postVOList = new ArrayList<>();
        for (PostEntity postEntity : records) {
            PostVO postVO = AbstractPostConverter.INSTANCE.toVO(postEntity);
            postVOList.add(postVO);
        }
        return PageUtils.toQueryPage(postEntityPage, postVOList);
    }

    @Override
    public void delete(String postCode) {
        LambdaQueryWrapper<PostEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(PostEntity::getPostCode, postCode);
        PostEntity postEntity = new PostEntity();
        postEntity.setDelFlag(Constants.DELETED);
        postEntity.setUpdateBy(UserUtils.getUser().getUserName());
        postEntity.setUpdateTime(new Date());
        postMapper.update(postEntity, deleteWrapper);
    }

    @Override
    public PostVO detail(Long postId) {
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostEntity::getPostId, postId);
        PostEntity postEntity = postMapper.selectOne(queryWrapper);
        return AbstractPostConverter.INSTANCE.toVO(postEntity);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<Long, String> getListByIdList(List<Long> postIdList) {
        if (CollectionUtils.isEmpty(postIdList)) {
            return new HashMap<>();
        }
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PostEntity::getPostId, postIdList);
        List<PostVO> postVOS = postMapper.selectList(queryWrapper).stream().map(AbstractPostConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
        return postVOS.stream().collect(Collectors.toMap(PostVO::getPostId, PostVO::getPostName));
    }

    @Override
    public List<PostVO> importPost(List<String> postNameList) {
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostEntity::getStatus, Constants.NORMAL);
        queryWrapper.eq(PostEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(PostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<PostEntity> postEntityList = postMapper.selectList(queryWrapper);
        Map<String, PostEntity> postNameMap =
                postEntityList.stream().collect(Collectors.toMap(PostEntity::getPostName, c -> c));
        List<PostEntity> insertList = new ArrayList<>();
        for (String postName : postNameList) {
            PostEntity exist = postNameMap.get(postName);
            if (exist != null) {
                continue;
            }
            PostEntity postEntity = getPostEntity(postName);
            insertList.add(postEntity);
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            saveBatch(insertList);
        }
        postEntityList.addAll(insertList);
        return postEntityList.stream().map(AbstractPostConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<PostVO> getAllPost() {
        LambdaQueryWrapper<PostEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostEntity::getStatus, Constants.NORMAL);
        queryWrapper.eq(PostEntity::getDelFlag, Constants.NU_DELETED);
        queryWrapper.eq(PostEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<PostEntity> postEntityList = postMapper.selectList(queryWrapper);
        return postEntityList.stream().map(AbstractPostConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    private static PostEntity getPostEntity(String postName) {
        PostEntity postEntity = new PostEntity();
        postEntity.setPostCode(ObjectId.getGuid());
        postEntity.setPostName(postName);
        postEntity.setPostSort(0);
        postEntity.setCreateBy(UserUtils.getUser().getUserName());
        postEntity.setUpdateBy(UserUtils.getUser().getUserName());
        postEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        postEntity.setCreateTime(new Date());
        postEntity.setUpdateTime(new Date());
        postEntity.setStatus(Constants.NORMAL);
        return postEntity;
    }
}
