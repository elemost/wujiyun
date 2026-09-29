package com.wuji.admin.service;

import com.wuji.admin.model.request.CorpCoopUserRequest;
import com.wuji.admin.model.vo.CorpCoopVO;
import com.wuji.admin.model.vo.UserImportVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CorpCoopUserService {
    void createCorpCoopUser(CorpCoopUserRequest corpCoopUserRequest);

    void delete(String userId);

    List<CorpCoopVO> getCorpCompany();

    UserImportVO importCorp(MultipartFile multipartFile);

    void template(HttpServletResponse response);
}
