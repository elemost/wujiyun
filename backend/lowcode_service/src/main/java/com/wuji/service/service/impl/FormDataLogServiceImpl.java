package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.TimeUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.enums.FormDataLogActionEnum;
import com.wuji.service.enums.FormDataLogContentSubOperateEnum;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.mapper.FormDataLogMapper;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.FormDataLogEntity;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.vo.FormDataLogContentSubVO;
import com.wuji.service.model.vo.FormDataLogContentVO;
import com.wuji.service.model.vo.FormDataLogVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataLogService;
import com.wuji.service.service.FormService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单数据日志 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-23
 */
@Service
@Slf4j
public class FormDataLogServiceImpl extends ServiceImpl<FormDataLogMapper, FormDataLogEntity>
        implements FormDataLogService {

    @Autowired
    private FormService formService;

    @Autowired
    private FormDataLogMapper formDataLogMapper;

    @Override
    public List<FormDataLogVO> list(String recordId, String applicationId) {
        LambdaQueryWrapper<FormDataLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataLogEntity::getRecordId, recordId);
        queryWrapper.eq(FormDataLogEntity::getApplicationId, applicationId);
        queryWrapper.orderByDesc(FormDataLogEntity::getCreateTime);
        List<FormDataLogEntity> formDataLogEntities = formDataLogMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formDataLogEntities)) {
            return new ArrayList<>();
        }
        return formDataLogEntities.stream().map(FormDataLogServiceImpl::getFormDataLogVO).collect(Collectors.toList());
    }

    @NotNull
    private static FormDataLogVO getFormDataLogVO(FormDataLogEntity entity) {
        FormDataLogVO formDataLogVO = new FormDataLogVO();
        formDataLogVO.setLogAction(entity.getLogAction());
        formDataLogVO.setCreator(entity.getCreator());
        formDataLogVO.setCreateTime(TimeUtils.formatDateTime(entity.getCreateTime(), TimeUtils.TIME_FORMAT));
        formDataLogVO.setList(JSON.parseArray(entity.getLogContent(), FormDataLogContentVO.class));
        return formDataLogVO;
    }

    @Override
    public FormDataLogVO lastLog(String recordId, String applicationId) {
        LambdaQueryWrapper<FormDataLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataLogEntity::getRecordId, recordId);
        queryWrapper.eq(FormDataLogEntity::getApplicationId, applicationId);
        queryWrapper.last(" limit 1 ");
        queryWrapper.orderByDesc(FormDataLogEntity::getCreateTime);
        FormDataLogEntity formDataLogEntity = formDataLogMapper.selectOne(queryWrapper);
        return formDataLogEntity != null ? getFormDataLogVO(formDataLogEntity) : null;
    }

    @Override
    public List<FormDataLogContentVO> recordLog(UserDomain user, LowcodeDataDomain previous, LowcodeDataDomain current,
                                                FormVO formVO, String status) {
        List<FormConfigCommon> formConfigCommonList =
                formService.getAllFormConfigCommonList(current.getFormId(), Boolean.FALSE, current.getApplicationId(),
                        Boolean.FALSE).getFields();
        List<FormDataLogContentVO> list =
                buildContentList(formConfigCommonList, previous == null ? null : previous.getInstValue(),
                        current.getInstValue());
        if (CollectionUtils.isNotEmpty(list)) {
            FormDataLogEntity entity = new FormDataLogEntity();
            entity.setId(ObjectId.getGuid());
            entity.setApplicationId(current.getApplicationId());
            entity.setFormId(current.getFormId());
            entity.setRecordId(current.getUuid());
            if (Objects.isNull(previous)) {
                entity.setLogAction(FormDataLogActionEnum.NEW.getAction());
            } else {
                if (FormDataStatusEnum.PASS.name().equals(status)) {
                    entity.setLogAction(FormDataLogActionEnum.SUBMIT.getAction());
                } else {
                    entity.setLogAction(FormDataLogActionEnum.SAVE.getAction());
                }
            }
            entity.setLogContent(JSONObject.toJSONString(list));
            entity.setCreator(user.getNickName());
            entity.setCreateTime(new Date());

            this.getBaseMapper().insert(entity);
        }
        return list;
    }

    private List<FormDataLogContentVO> buildContentList(List<FormConfigCommon> formConfigCommonList,
                                                        JSONObject previousInstValue, JSONObject currentInstValue) {
        List<FormDataLogContentVO> list = new ArrayList<>();
        try {
            // 根据新值初始化list
            for (String key : currentInstValue.keySet()) {
                FormDataLogContentVO formDataLogContentVO = new FormDataLogContentVO();
                formDataLogContentVO.setName(key);
                boolean isSubForm = false;
                List<FormConfigCommon> subFormConfigCommonList = null;
                for (FormConfigCommon formConfigCommon : formConfigCommonList) {
                    if (Objects.equals(key, formConfigCommon.getName())) {
                        formDataLogContentVO.setLabel(formConfigCommon.getLabel());
                        formDataLogContentVO.setType(formConfigCommon.getType());
                        if (Constants.SUB_FORM_TYPE.equals(formConfigCommon.getType())) {
                            isSubForm = true;
                            subFormConfigCommonList = formConfigCommon.getColumns();
                        }
                        break;
                    }
                }
                if (formDataLogContentVO.getType() == null) {
                    continue;
                }
                if (isSubForm) {
                    List<FormDataLogContentSubVO> formDataLogContentSubVOList = new ArrayList<>();
                    JSONArray currentInstValueJSONArray = currentInstValue.getJSONArray(key);
                    if (Objects.isNull(previousInstValue)) {
                        for (int i = 0; i < currentInstValueJSONArray.size(); i++) {
                            newLine(formDataLogContentSubVOList, subFormConfigCommonList, currentInstValueJSONArray, i);
                        }
                    } else {
                        String previousInstValueString = previousInstValue.getString(key);
                        if (StringUtils.isEmpty(previousInstValueString)) {
                            for (int i = 0; i < currentInstValueJSONArray.size(); i++) {
                                newLine(formDataLogContentSubVOList, subFormConfigCommonList, currentInstValueJSONArray,
                                        i);
                            }
                        } else {
                            List<JSONObject> previousJsonObjectList =
                                    JSONArray.parseArray(JSONObject.toJSONString(previousInstValue.getJSONArray(key)),
                                            JSONObject.class);
                            Map<String, JSONObject> previousMap = previousJsonObjectList.stream()
                                    .collect(Collectors.toMap(c -> c.getString("id"), c -> c));
                            Map<String, JSONObject> currentMap = currentInstValueJSONArray.stream().collect(
                                    Collectors.toMap(
                                            c -> JSONObject.parseObject(JSONObject.toJSONString(c)).getString("id"),
                                            c -> JSONObject.parseObject(JSONObject.toJSONString(c))));
                            for (int i = 0; i < currentInstValueJSONArray.size(); i++) {
                                JSONObject current = currentInstValueJSONArray.getJSONObject(i);
                                String id = current.getString("id");
                                JSONObject previous = previousMap.get(id);
                                if (previous == null) {
                                    newLine(formDataLogContentSubVOList, subFormConfigCommonList,
                                            currentInstValueJSONArray, i);
                                } else {
                                    updateLine(formDataLogContentSubVOList, subFormConfigCommonList, current, previous);
                                }
                            }
                            for (JSONObject previous : previousJsonObjectList) {
                                String id = previous.getString("id");
                                JSONObject current = currentMap.get(id);
                                if (current == null) {
                                    deleteLine(formDataLogContentSubVOList, subFormConfigCommonList, previous);
                                }
                            }
                        }
                    }
                    formDataLogContentVO.setChildren(formDataLogContentSubVOList);
                } else {
                    Object value = getValue(currentInstValue, key, formDataLogContentVO.getType());
                    formDataLogContentVO.setCurValue(value);
                    if (Objects.nonNull(previousInstValue)) {
                        String targetKey =
                                previousInstValue.keySet().stream().filter(target -> Objects.equals(target, key))
                                        .findFirst().orElse(null);
                        if (Objects.nonNull(targetKey)) {
                            Object previousValue = getValue(previousInstValue, key, formDataLogContentVO.getType());
                            formDataLogContentVO.setPreValue(previousValue);
                        }
                    }
                }

                list.add(formDataLogContentVO);
            }


            // 遍历前值，是否含有当前值不存在项
            // if (Objects.nonNull(previous) && Objects.nonNull(previous.getInstValue())) {
            //     previous.getInstValue().keySet().forEach(key -> {
            //         // 在当前值中找不到前值的key，表示该key被删除了
            //         if (current.getInstValue().keySet().stream().noneMatch(target -> Objects.equals(target, key))) {
            //             FormDataLogContentVO vo = new FormDataLogContentVO();
            //             vo.setName(key);
            //             vo.setLabel("");
            //             vo.setPreValue(previous.getInstValue().getString(key));
            //             vo.setCurValue(null);
            //             list.add(vo);
            //         }
            //     });
            // }
        } catch (Exception ex) {
            log.error("参数错误", ex);
        }
        // 只返回不一致的值
        return list.stream().filter(obj -> !Objects.equals(obj.getPreValue(), obj.getCurValue()) ||
                CollectionUtils.isNotEmpty(obj.getChildren())).collect(Collectors.toList());
    }

    private Object getValue(JSONObject currentInstValue, String key, String type) {
        Object curValue = currentInstValue.get(key);
        if (curValue == null) {
            return null;
        }
        if (curValue instanceof JSONObject || curValue instanceof LinkedHashMap) {
            return JSONObject.toJSONString(curValue);
        }
        if (curValue instanceof ArrayList) {
            List<Map<String, Object>> list = new ArrayList<>();
            JSONArray jsonArray = currentInstValue.getJSONArray(key);
            if (FormFieldTypeEnum.TREE_SELECT.getFieldType().equals(type) ||
                    FormFieldTypeEnum.CHECKBOXES.getFieldType().equals(type) ||
                    FormFieldTypeEnum.FORM_INPUT_ROLE_MULTIPLE.getFieldType().equals(type)) {
                return JSONArray.toJSONString(jsonArray);
            } else {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map<String, Object> sortedMap = new TreeMap<>(jsonObject);
                    list.add(sortedMap);
                }
            }
            return JSONArray.toJSONString(list);
        } else {
            return curValue;
        }
    }

    private void deleteLine(List<FormDataLogContentSubVO> formDataLogContentSubVOList,
                            List<FormConfigCommon> formConfigCommonList, JSONObject previous) {
        List<FormDataLogContentVO> formDataLogContentVOS = buildContentList(formConfigCommonList, null, previous);
        FormDataLogContentSubVO formDataLogContentSubVO = new FormDataLogContentSubVO();
        formDataLogContentSubVO.setId(previous.getString("id"));
        formDataLogContentSubVO.setOperate(FormDataLogContentSubOperateEnum.DELETE.getAction());
        formDataLogContentSubVO.setFormDataLogContentList(formDataLogContentVOS);
        formDataLogContentSubVOList.add(formDataLogContentSubVO);
    }

    private void newLine(List<FormDataLogContentSubVO> formDataLogContentSubVOList,
                         List<FormConfigCommon> formConfigCommonList, JSONArray currentInstValueJSONArray, int i) {
        JSONObject jsonObject = currentInstValueJSONArray.getJSONObject(i);
        List<FormDataLogContentVO> formDataLogContentVOS = buildContentList(formConfigCommonList, null, jsonObject);
        FormDataLogContentSubVO formDataLogContentSubVO = new FormDataLogContentSubVO();
        formDataLogContentSubVO.setId(jsonObject.getString("id"));
        formDataLogContentSubVO.setOperate(FormDataLogContentSubOperateEnum.NEW.getAction());
        formDataLogContentSubVO.setFormDataLogContentList(formDataLogContentVOS);
        formDataLogContentSubVOList.add(formDataLogContentSubVO);
    }

    private void updateLine(List<FormDataLogContentSubVO> formDataLogContentSubVOList,
                            List<FormConfigCommon> formConfigCommonList, JSONObject current, JSONObject previous) {
        List<FormDataLogContentVO> formDataLogContentVOS = buildContentList(formConfigCommonList, previous, current);
        if (CollectionUtils.isEmpty(formDataLogContentVOS)) {
            return;
        }
        FormDataLogContentSubVO formDataLogContentSubVO = new FormDataLogContentSubVO();
        formDataLogContentSubVO.setId(current.getString("id"));
        formDataLogContentSubVO.setOperate(FormDataLogContentSubOperateEnum.UPDATE.getAction());
        formDataLogContentSubVO.setFormDataLogContentList(formDataLogContentVOS);
        formDataLogContentSubVOList.add(formDataLogContentSubVO);
    }
}
