package com.wuji.service.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.FormViewCalendarRequest;
import com.wuji.service.model.request.FormViewFieldGroupRequest;
import com.wuji.service.model.request.FormViewLevelRequest;
import com.wuji.service.model.request.FormViewMongoDbRequest;
import com.wuji.service.model.vo.FormFieldGroupVO;
import com.wuji.service.model.vo.LowcodeDataVO;

import java.util.List;

public interface FormMongoDbViewService {
    QueryPageVO<LowcodeDataVO> queryListView(FormViewMongoDbRequest formViewMongoDbRequest);

    QueryPageVO<LowcodeDataVO> queryListViewPrivilege(FormViewMongoDbRequest formViewMongoDbRequest);

    List<Long> monthlyData(FormViewCalendarRequest formViewCalendarRequest);

    List<LowcodeDataVO> queryDataByTime(FormViewCalendarRequest formViewCalendarRequest);

    FormFieldGroupVO fieldGroup(FormViewFieldGroupRequest formViewFieldGroupRequest);

    List<LowcodeDataVO> level(FormViewLevelRequest formViewLevelRequest);
}
