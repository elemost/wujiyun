package com.wuji.admin.controller;

import com.wuji.admin.model.request.PostCreateRequest;
import com.wuji.admin.model.request.PostSelectRequest;
import com.wuji.admin.model.request.PostUpdateRequest;
import com.wuji.admin.service.PostService;
import com.wuji.common.model.vo.PostVO;
import com.wuji.common.model.vo.QueryPageVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 岗位信息表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-10-22
 */
@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @ApiOperation("创建岗位")
    @PostMapping("/create")
    public void create(@RequestBody PostCreateRequest postCreateRequest) {
        postService.create(postCreateRequest);
    }

    @ApiOperation("修改岗位")
    @PostMapping("/update")
    public void update(@RequestBody PostUpdateRequest postUpdateRequest) {
        postService.update(postUpdateRequest);
    }

    @ApiOperation("岗位列表")
    @PostMapping("/list")
    public QueryPageVO<PostVO> selectList(@RequestBody PostSelectRequest postSelectRequest) {
        return postService.selectList(postSelectRequest);
    }

    @ApiOperation("岗位信息")
    @GetMapping("/detail/{postId}")
    public PostVO detail(@PathVariable Long postId) {
        return postService.detail(postId);
    }

    @ApiOperation("删除岗位")
    @DeleteMapping("/delete/{postCode}")
    public void delete(@PathVariable String postCode) {
        postService.delete(postCode);
    }
}
