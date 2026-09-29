package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.PostEntity;
import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 岗位信息表 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
public interface PostService extends IService<PostEntity> {
    String create(PostCreateRequest postCreateRequest);

    void update(PostUpdateRequest postUpdateRequest);

    QueryPageVO<PostVO> selectList(PostSelectRequest postSelectRequest);

    void delete(String postCode);

    PostVO detail(Long postId);

    Map<Long, String> getListByIdList(List<Long> postIdList);

    List<PostVO> importPost(List<String> postNameList);

    List<PostVO> getAllPost();
}
