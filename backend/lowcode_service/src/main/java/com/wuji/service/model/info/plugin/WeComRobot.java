package com.wuji.service.model.info.plugin;

import com.wuji.service.model.info.FormMessageMarkdown;
import lombok.Data;

import java.util.List;

@Data
public class WeComRobot {

   private String url;

   private List<FormMessageMarkdown> markdownList;

}
