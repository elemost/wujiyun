package com.wuji.workflow.constant;

import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlowableConstant {

    /**
     * 流程发起人
     */
    public static final String PROCESS_INITIATOR = "_initiator";

    public static final String INITIATOR = "initiator";

    /**
     * 角色候选组前缀
     */
    public static final String ROLE_GROUP_PREFIX = "ROLE";

    public static final String POSITION_GROUP_PREFIX = "POSITION";

    /**
     * 部门候选组前缀
     */
    public static final String DEPT_GROUP_PREFIX = "DEPT";

    public static final String USER_GROUP_PREFIX = "USER";

    public static final String FORM_ID = "_formId";

    public static final String VARIABLE_DATA_UUID = "_dataUuid";

    public static final String VARIABLE_APPLICATION_ID = "_applicationId";

    public static final String FLOWABLE_CALLBACK_SERVICE = "_flowableCallBackService";
    /**
     * 自定义属性 流程状态
     */
    public static final String PROCESS_STATUS_SING_KEY = "_processSignStatus";

    public static final String PREVIOUS_TASK_ID = "_previousTaskId";


    public static final String PARENT_PROCESS_INSTANCE_ID = "_processInstanceParentId";

    public static List<String> taskCandidateGroupList() {
        UserDomain user = UserUtils.getUser();
        List<String> taskCandidateGroupList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(user.getDataScopeDeptIdList())) {
            taskCandidateGroupList =
                    user.getDeptIdList().stream().map(c -> DEPT_GROUP_PREFIX + "_" + c).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(user.getPostIdList())) {
            for (Long postId : user.getPostIdList()) {
                taskCandidateGroupList.add(POSITION_GROUP_PREFIX + "_" + postId);
            }
        }
        taskCandidateGroupList.add(USER_GROUP_PREFIX + "_" + user.getUserId());
        return taskCandidateGroupList;
    }

}
