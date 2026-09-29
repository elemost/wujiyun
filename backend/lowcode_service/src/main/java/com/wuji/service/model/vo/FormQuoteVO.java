package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormQuoteVO {
   private List<FormQuoteInfoVO> formQuoteInfoQuoteList;

   private List<FormQuoteInfoVO> formQuoteInfoQuotedList;
}
