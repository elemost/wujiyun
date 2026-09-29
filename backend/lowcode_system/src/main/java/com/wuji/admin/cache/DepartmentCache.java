package com.wuji.admin.cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.service.DepartmentService;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.utils.ToolSpring;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class DepartmentCache {
    private static long maximumSize = 2000L;
    private static long expireAfterWrite = 2L;
    private static LoadingCache<Long, List<DepartmentVO>> departmentCache =
            CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
                    .build(new CacheLoader<Long, List<DepartmentVO>>() {
                        private DepartmentService departmentService;


                        @Override
                        public List<DepartmentVO> load(Long companyId) throws Exception {

                            if (departmentService == null) {
                                departmentService = ToolSpring.getBean(DepartmentService.class);
                            }

                            return departmentService.queryList(companyId, DepartmentTypeEnum.INTERNAL_DEPT.getCode());
                        }
                    });

    private static final LoadingCache<Long, List<DepartmentVO>> departmentExternalCache =
            CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
                    .build(new CacheLoader<Long, List<DepartmentVO>>() {
                        private DepartmentService departmentService;


                        @Override
                        public List<DepartmentVO> load(Long companyId) throws Exception {

                            if (departmentService == null) {
                                departmentService = ToolSpring.getBean(DepartmentService.class);
                            }

                            return departmentService.queryList(companyId, DepartmentTypeEnum.EXTERNAL_DEPT.getCode());
                        }
                    });

    private static final LoadingCache<Long, List<DepartmentVO>> departmentAllCache =
            CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
                    .build(new CacheLoader<Long, List<DepartmentVO>>() {
                        private DepartmentService departmentService;


                        @Override
                        public List<DepartmentVO> load(Long companyId) throws Exception {

                            if (departmentService == null) {
                                departmentService = ToolSpring.getBean(DepartmentService.class);
                            }

                            return departmentService.queryList(companyId, null);
                        }
                    });

    public static List<DepartmentVO> getValue(Long companyId, String deptType) {
        try {
            if (DepartmentTypeEnum.EXTERNAL_DEPT.getCode().equals(deptType)) {
                return JSONArray.parseArray(JSONObject.toJSONString(departmentExternalCache.get(companyId)),
                        DepartmentVO.class);
            } else if (DepartmentTypeEnum.INTERNAL_DEPT.getCode().equals(deptType)) {
                return JSONArray.parseArray(JSONObject.toJSONString(departmentCache.get(companyId)),
                        DepartmentVO.class);
            } else {
                return JSONArray.parseArray(JSONObject.toJSONString(departmentAllCache.get(companyId)),
                        DepartmentVO.class);

            }
        } catch (Exception e) {
            log.error("get value error from cache by key[" + companyId + "]");
            return new ArrayList<>();
        }
    }

    public static void clear(Long companyId) {
        departmentCache.refresh(companyId);
        departmentExternalCache.refresh(companyId);
        departmentAllCache.refresh(companyId);
    }

    public static List<Long> getAllChildren(Long companyId, List<Long> deptIdList) {
        List<DepartmentVO> departmentVOList = new ArrayList<>();
        if (deptIdList.contains(0L)) {
            departmentVOList = getChildDepartmentList(companyId, deptIdList, DepartmentTypeEnum.INTERNAL_DEPT.name());
            List<Long> depts = JSONArray.parseArray(JSONObject.toJSONString(deptIdList), Long.class);
            depts.remove(0L);
            departmentVOList.addAll(getChildDepartmentList(companyId, depts, DepartmentTypeEnum.EXTERNAL_DEPT.name()));
        } else {
            departmentVOList = getChildDepartmentList(companyId, deptIdList, null);
        }
        return departmentVOList.stream().map(DepartmentVO::getDeptId).collect(Collectors.toList());
    }

    public static List<DepartmentVO> getChildDepartmentList(Long companyId, List<Long> deptIdList, String deptType) {

        List<DepartmentVO> departmentVOS = getValue(companyId, deptType);
        Map<Long, DepartmentVO> deptIdToVOMap =
                departmentVOS.stream().collect(Collectors.toMap(DepartmentVO::getDeptId, c -> c));
        List<DepartmentVO> departmentVOList = new ArrayList<>();
        for (Long deptId : deptIdList) {
            DepartmentVO departmentVO = deptIdToVOMap.get(deptId);
            if (departmentVO != null) {
                departmentVOList.add(departmentVO);
            }
            addChild(departmentVO, departmentVOList);
        }
        return departmentVOList;
    }

    private static void addChild(DepartmentVO parent, List<DepartmentVO> departmentVOList) {
        if (parent == null) {
            return;
        }
        if (CollectionUtils.isEmpty(parent.getChildren())) {
            return;
        }

        for (DepartmentVO departmentVO : parent.getChildren()) {
            if (0 == parent.getDeptId() && "10".equals(departmentVO.getDeptType())) {
                continue;
            }
            departmentVOList.add(departmentVO);
            addChild(departmentVO, departmentVOList);
        }
    }
}
