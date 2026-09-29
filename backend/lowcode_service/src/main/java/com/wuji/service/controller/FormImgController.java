package com.wuji.service.controller;

import com.wuji.common.model.Response;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.model.request.FormImgRequest;
import com.wuji.service.model.vo.FormImgVO;
import com.wuji.service.service.FormImgService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/form/img")
public class FormImgController {

    @Autowired
    private FormImgService formImgService;

    @ApiOperation("创建图片")
    @PostMapping("/create")
    public Response<String> create(@RequestBody FormImgRequest formImgRequest) {
        return Response.success(formImgService.insert(formImgRequest));
    }

    @ApiOperation("图片详情")
    @GetMapping("/info/{id}")
    public FormImgVO getImg(@PathVariable String id) {
        return formImgService.getImg(id);
    }

    @ApiOperation("创建图片")
    @PostMapping("/upload")
    public Response<FormImgVO> upload(MultipartFile multipartFile, FileTypeEnum fileTypeEnum) {
        return Response.success(formImgService.upload(multipartFile, fileTypeEnum));
    }

    @ApiOperation("根据文件类型获取模板列表")
    @GetMapping("/getByType/{imgType}")
    public List<FormImgVO> getByType(@PathVariable String imgType) {
        return formImgService.getByType(imgType);
    }

}
