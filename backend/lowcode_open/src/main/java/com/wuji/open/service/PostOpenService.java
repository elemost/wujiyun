package com.wuji.open.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.open.model.request.PostCreateOpenRequest;
import com.wuji.open.model.request.PostSelectOpenRequest;
import com.wuji.open.model.request.PostUpdateOpenRequest;
import com.wuji.open.model.vo.PostOpenVO;

public interface PostOpenService {

    String addPost(PostCreateOpenRequest postCreateOpenRequest);

    void updatePost(PostUpdateOpenRequest postUpdateOpenRequest);

    QueryPageVO<PostOpenVO> postList(PostSelectOpenRequest postSelectOpenRequest);

    void deletePost(String postCode);
}
