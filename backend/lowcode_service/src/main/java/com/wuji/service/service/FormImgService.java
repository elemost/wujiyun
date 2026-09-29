package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.enums.FileTypeEnum;
import com.wuji.service.model.entity.FormImgEntity;
import com.wuji.service.model.request.FormImgRequest;
import com.wuji.service.model.vo.FormImgVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-03-24
 */
public interface FormImgService extends IService<FormImgEntity> {
    String insert(FormImgRequest formImgRequest);

    FormImgVO getImg(String id);

    FormImgVO upload(MultipartFile file, FileTypeEnum fileTypeEnum);

    List<FormImgVO> getByIdList(List<String> ids);

    List<FormImgVO> getByIdListOpen(List<String> ids);

    FormImgVO getByImg(String imgUrl);

    List<FormImgVO> getByImgList(List<String> imgUrlList);

    List<FormImgVO> getByType(String imgType);


}
