package com.wuji.service.service.form;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.model.vo.SystemAllDataVO;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.info.FormConfigTab;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.form.FormSubmitCheck;
import com.wuji.service.service.FormDataService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormTabServiceImpl extends FormCommonServiceImpl implements FormDataService {
    @Override
    public String fieldType() {
        return FormFieldTypeEnum.TABS.getFieldType();
    }

    @Override
    public void dealWhileCreate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        for (FormConfigTab tab : tabs) {
            List<FormConfigCommon> columns = tab.getBody();
            for (FormConfigCommon subFormConfig : columns) {
                whileCreate(instValue, subFormConfig, info, formSubmitCheck);
            }
        }
    }

    @Override
    public void dealWhileUpdate(JSONObject instValue, FormConfigCommon formConfigCommon, FormVO info,
                                FormSubmitCheck formSubmitCheck) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        for (FormConfigTab tab : tabs) {
            List<FormConfigCommon> columns = tab.getBody();
            for (FormConfigCommon subFormConfig : columns) {
                whileUpdate(instValue, subFormConfig, info, formSubmitCheck);
            }
        }
    }

    @Override
    public void dealWhileReturn(List<LowcodeDataVO> lowcodeDataList, FormConfigCommon formConfigCommon, FormVO info,
                                SystemAllDataVO systemAllDataVO) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        for (FormConfigTab tab : tabs) {
            List<FormConfigCommon> columns = tab.getBody();
            for (FormConfigCommon subFormConfig : columns) {
                whileReturn(lowcodeDataList, subFormConfig, info, systemAllDataVO);
            }
        }
    }

    @Override
    public void customTemplate(JSONObject instValue, FormConfigCommon formConfigCommon, String formId,
                               SystemAllDataVO systemAllDataVO) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        for (FormConfigTab tab : tabs) {
            List<FormConfigCommon> columns = tab.getBody();
            for (FormConfigCommon subFormConfig : columns) {
                whileCustomTemplate(instValue, subFormConfig, formId, systemAllDataVO);
            }
        }
    }

    @Override
    public void getAllConfig(FormConfigCommon formConfigCommon, List<FormConfigCommon> subFormList,
                             Boolean needSubForm) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        for (FormConfigTab tab : tabs) {
            List<FormConfigCommon> columns = tab.getBody();
            for (FormConfigCommon subFormConfig : columns) {
                whileAddSubForm(subFormConfig, subFormList, needSubForm);
            }
        }
    }

    @Override
    public void dealWhileUseTemplate(JSONObject jsonObject, FormConfigCommon formConfigCommon, FormUser current,
                                     List<FormDept> currentDept, Boolean needTrans) {
        List<FormConfigTab> tabs = formConfigCommon.getTabs();
        if (CollectionUtils.isEmpty(tabs)) {
            return;
        }
        for (FormConfigTab tab : tabs) {
            for (FormConfigCommon subForm : tab.getBody()) {
                whileUseTemplate(jsonObject, subForm, current, currentDept, needTrans);
            }
        }
    }
}