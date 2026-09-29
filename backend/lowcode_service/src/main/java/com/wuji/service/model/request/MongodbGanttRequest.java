package com.wuji.service.model.request;


import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.MongodbSearchFilter;
import lombok.Data;

import java.util.List;

@Data
public class MongodbGanttRequest {

    private String formId;

    private String applicationId;

    private MongodbSearchFilter filter;

    private GanttWidget widget;

    @Data
    public static class GanttWidget {
        private GanttField ganttFields;

        private List<MongodbSearchField> fieldyList;

        private List<MongodbSearchField> fieldxList;

        private MongodbSearchFilter filter;

        private List<MongoSort> sorts;

        private ChartLabel chartLabel;

    }

    @Data
    public static class GanttField {
        private MongodbSearchField end;

        private MongodbSearchField progress;

        private MongodbSearchField start;
    }

    @Data
    public static class ChartLabel {
        private Boolean enable;

        private MongodbSearchField field;
    }
}
