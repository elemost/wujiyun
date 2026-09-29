package com.wuji.open.service.impl;

import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.admin.service.PostService;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.open.converter.AbstractPostOpenConverter;
import com.wuji.open.model.request.PostCreateOpenRequest;
import com.wuji.open.model.request.PostSelectOpenRequest;
import com.wuji.open.model.request.PostUpdateOpenRequest;
import com.wuji.open.model.vo.PostOpenVO;
import com.wuji.open.service.PostOpenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PostOpenServiceImpl implements PostOpenService {

    @Autowired
    private PostService postService;


    @Override
    public String addPost(PostCreateOpenRequest postCreateOpenRequest) {
        PostCreateRequest postCreateRequest = AbstractPostOpenConverter.INSTANCE.toRequest(postCreateOpenRequest);
        return postService.create(postCreateRequest);
    }
    @Override
    public void updatePost(PostUpdateOpenRequest postUpdateOpenRequest) {
        PostUpdateRequest request = AbstractPostOpenConverter.INSTANCE.toRequest(postUpdateOpenRequest);
        postService.update(request);
    }

    @Override
    public QueryPageVO<PostOpenVO> postList(PostSelectOpenRequest postSelectOpenRequest) {
        PostSelectRequest postSelectRequest = AbstractPostOpenConverter.INSTANCE.toRequest(postSelectOpenRequest);
        QueryPageVO<PostVO> postPage = postService.selectList(postSelectRequest);
        return new QueryPageVO<>(postPage.getPageNum(), postPage.getPageSize(), postPage.getTotal(),
                postPage.getList().stream().map(AbstractPostOpenConverter.INSTANCE::toVO)
                        .collect(Collectors.toList()));
    }

    @Override
    public void deletePost(String postCode) {
        postService.delete(postCode);
    }
}
