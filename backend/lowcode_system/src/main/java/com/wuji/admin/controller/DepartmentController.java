package com.wuji.admin.controller;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.admin.aspect.corp.annotation.CorpCoopResource;
import com.wuji.admin.cache.DepartmentCache;
import com.wuji.admin.enums.DepartmentTypeEnum;
import com.wuji.admin.model.request.DepartmentCreateRequest;
import com.wuji.admin.model.request.DepartmentUpdateRequest;
import com.wuji.admin.service.DepartmentService;
import com.wuji.common.model.vo.DepartmentVO;
import com.wuji.common.privilege.enums.IdentifierLocationEnum;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/department")
@Slf4j
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    @ApiOperation("获取部门树形列表")
    @PostMapping("/tree")
    @CorpCoopResource(location = IdentifierLocationEnum.REQ_PRAM)
    public List<DepartmentVO> tree(@RequestParam(value = "deptType", required = false) String deptType,
                                   @RequestParam(value = "companyUuid", required = false) @CorpCoop String companyUuid) {
        List<DepartmentVO> departmentVOList = DepartmentCache.getValue(UserUtils.getUser().getCompanyId(), deptType);
        if (DepartmentTypeEnum.EXTERNAL_DEPT.getCode().equals(deptType)) {
            return departmentVOList.stream().filter(c -> c.getParentId().toString().equals("0"))
                    .collect(Collectors.toList());
        } else {
            return departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                    .collect(Collectors.toList());
        }
    }

    @ApiOperation("获取部门树形列表")
    @GetMapping("/selectTree")
    public List<DepartmentVO> tree(@RequestParam(value = "departmentId", required = false) List<Long> department,
                                   @RequestParam(value = "deptType", required = false, defaultValue = "00")
                                   String deptType) {
        List<DepartmentVO> departmentVOList = departmentService.querySelectList(department, deptType);
        if (DepartmentTypeEnum.EXTERNAL_DEPT.getCode().equals(deptType)) {
            return departmentVOList.stream().filter(c -> c.getParentId().toString().equals("0"))
                    .collect(Collectors.toList());
        } else {
            return departmentVOList.stream().filter(c -> c.getDeptId().toString().equals("0"))
                    .collect(Collectors.toList());
        }
    }

    @ApiOperation("拉取数据")
    @PostMapping("/pullData")
    public void pullData() {
        log.info("拉取部门数据开始");
        departmentService.pullData(UserUtils.getUser().getCompanyId());
        log.info("拉取部门数据结束");
    }


    @ApiOperation("创建部门")
    @PostMapping("/create")
    public void create(@RequestBody DepartmentCreateRequest departmentCreateRequest) {
        departmentService.create(departmentCreateRequest);
    }

    @ApiOperation("修改部门")
    @PostMapping("/update")
    public void update(@RequestBody DepartmentUpdateRequest departmentUpdateRequest) {
        departmentService.update(departmentUpdateRequest);
    }

    @ApiOperation("修改部门")
    @PostMapping("/delete/{deptId}")
    public void delete(@PathVariable Long deptId) {
        departmentService.delete(deptId);
    }

}
