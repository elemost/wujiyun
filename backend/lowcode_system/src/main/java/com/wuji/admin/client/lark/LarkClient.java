package com.wuji.admin.client.lark;


import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.lark.model.JssdkTicketVO;
import com.wuji.admin.client.lark.model.LarkDataVO;
import com.wuji.admin.client.lark.model.LarkDepartmentLoginRequest;
import com.wuji.admin.client.lark.model.LarkSendMessageRequest;
import com.wuji.admin.client.lark.model.LarkUserAssessTokenRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "larkClient", url = "${lark.server:https://open.feishu.cn}",
        configuration = LarkFeignConfiguration.class)
public interface LarkClient {
    @GetMapping("/open-apis/contact/v3/departments/{departmentId}/children")
    LarkResult<LarkDataVO<List<JSONObject>>> getDeptList(@PathVariable("departmentId") String departmentId,
                                                         @RequestParam("department_id_type") String departmentIdType,
                                                         @RequestParam("page_size") Integer pageSize,
                                                         @RequestParam("fetch_child") Boolean fetchChild,
                                                         @RequestParam(value = "page_token", required = false)
                                                         String pageToken,
                                                         @RequestParam("user_id_type") String userIdType);


    @GetMapping("/open-apis/contact/v3/users/find_by_department")
    LarkResult<LarkDataVO<List<JSONObject>>> getUserList(@RequestParam("department_id") String departmentId,
                                                         @RequestParam("department_id_type") String departmentIdType,
                                                         @RequestParam("page_size") Integer pageSize,
                                                         @RequestParam(value = "page_token", required = false)
                                                         String pageToken);

    @GetMapping("/open-apis/contact/v3/departments/batch")
    LarkResult<LarkDataVO<List<JSONObject>>> getDeptByIdList(@RequestParam("department_ids") List<String> departmentId,
                                                             @RequestParam("department_id_type")
                                                             String departmentIdType);

    @GetMapping("/open-apis/contact/v3/users/batch")
    LarkResult<LarkDataVO<List<JSONObject>>> getUserByIdList(@RequestParam("user_ids") List<String> userIds,
                                                             @RequestParam("user_id_type") String userIdType);

    @GetMapping("/open-apis/auth/v3/app_access_token/internal")
    LarkResult login(@RequestBody LarkDepartmentLoginRequest larkDepartmentLoginRequest);

    @GetMapping("/open-apis/authen/v2/oauth/token")
    LarkResult getUserAssess(@RequestBody LarkUserAssessTokenRequest larkUserAssessTokenRequest);


    @GetMapping("/open-apis/authen/v1/user_info")
    LarkResult<JSONObject> getLoginUserInfo(@RequestHeader("Authorization") String authorization);


    @GetMapping("/open-apis/im/v1/messages")
    LarkResult<JSONObject> sendMessage(@RequestParam("receive_id_type") String receiveIdType,
                                       @RequestBody LarkSendMessageRequest larkSendMessageRequest);

    @GetMapping("/open-apis/jssdk/ticket/get")
    LarkResult<JssdkTicketVO> jsapiAuth();

}
