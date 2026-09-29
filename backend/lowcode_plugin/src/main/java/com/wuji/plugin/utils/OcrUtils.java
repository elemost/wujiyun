package com.wuji.plugin.utils;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.ContentTypeEnum;
import com.wuji.common.enums.MethodEnum;
import com.wuji.common.model.request.ApiRequest;
import com.wuji.common.utils.HttpUtils;

public class OcrUtils {
    public static JSONObject getOcrResult(Object object) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(object));
        // 是否需要识别结果中每一行的置信度，默认不需要。 true：需要 false：不需要
        jsonObject.put("prob", false);
        // 是否需要单字识别功能，默认不需要。 true：需要 false：不需要
        jsonObject.put("charInfo", false);
        // 是否需要成行返回功能，默认不需要。true：需要 false：不需要
        jsonObject.put("figure", true);
        // 是否需要自动旋转功能，默认不需要。 true：需要 false：不需要
        jsonObject.put("rotate", true);
        // 是否需要去除印章功能，默认不需要。true：需要 false：不需要
        jsonObject.put("noStamp", true);
        // 是否需要表格识别功能，默认不需要。 true：需要 false：不需要
        jsonObject.put("table", false);
        // 字块返回顺序，false表示从左往右，从上到下的顺序，true表示从上到下，从左往右的顺序，默认false
        jsonObject.put("sortPage", false);
        // 是否需要成行返回功能，默认不需要。true：需要 false：不需要
        jsonObject.put("row", true);
        // 是否需要分段功能，默认不需要。true：需要 false：不需要
        jsonObject.put("paragraph", false);
        // 图片旋转后，是否需要返回原始坐标，默认不需要。true：需要  false：不需要
        jsonObject.put("oricoord", false);

        ApiRequest apiRequest = new ApiRequest();
        apiRequest.setRequestJsonBody(jsonObject);
        apiRequest.setUrl("https://gjbsb.market.alicloudapi.com/ocrservice/advanced");
        apiRequest.setMethod(MethodEnum.POST);
        apiRequest.putHeader("Authorization", "APPCODE " + "e2cfdc84f7eb4847a64305f43914f8f9");
        apiRequest.setContentTypeEnum(ContentTypeEnum.JSON);
        return JSONObject.parseObject(HttpUtils.apiRequest(apiRequest));
    }
}
