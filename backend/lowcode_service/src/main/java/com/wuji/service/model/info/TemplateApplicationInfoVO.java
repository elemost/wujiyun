package com.wuji.service.model.info;

import com.wuji.service.model.vo.TemplateApplicationVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import com.wuji.service.model.vo.TemplateFormExtraFunctionVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import com.wuji.service.model.vo.TemplateFormPrivilegeVO;
import com.wuji.service.model.vo.TemplateFormPublicPublishVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import com.wuji.service.model.vo.TemplateFormVO;
import com.wuji.workflow.model.vo.FlowableConfigVO;
import lombok.Data;

import java.util.List;

@Data
public class TemplateApplicationInfoVO {

    private TemplateApplicationVO templateApplicationVO;

    private List<TemplateApplicationCategoryVO> applicationCategoryVOS;

    private List<TemplateFormVO> templateFormList;

    private List<TemplateFormPrivilegeVO> templateFormPrivilegeVOS;

    private List<FlowableConfigVO> formModels;

    private List<TemplateFormExtraFunctionVO> formExtraFunctionVOS;

    private List<TemplateFormQuoteVO> formQuoteList;

    private List<TemplateFormPublicPublishVO> formPublicPublishList;

    private List<TemplateFormAggregateVO> formAggregateList;

    private List<TemplateFormDataStreamVO> formDataStreamList;

    private List<TemplateFormInfoVO> formInfoList;

    private List<TemplateFormRuleVO> formRuleList;

    private List<TemplateFormDataFactoryVO> formDataFactoryList;

    private List<TemplateFormModuleVO> formModuleList;
}
