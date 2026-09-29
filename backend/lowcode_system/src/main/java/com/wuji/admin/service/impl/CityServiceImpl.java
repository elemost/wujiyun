package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractCityConverter;
import com.wuji.admin.mapper.CityMapper;
import com.wuji.admin.model.entity.CityEntity;
import com.wuji.admin.model.vo.CityVO;
import com.wuji.admin.service.CityService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-04
 */
@Service
@DS("slave")
public class CityServiceImpl extends ServiceImpl<CityMapper, CityEntity> implements CityService {

    @Autowired
    private CityMapper cityMapper;


    @Override
    public List<CityVO> getList() {
        List<CityEntity> cityEntities = cityMapper.selectList(new LambdaQueryWrapper<>());
        List<CityVO> cityVOS =
                cityEntities.stream().map(AbstractCityConverter.INSTANCE::toVO).collect(Collectors.toList());
        Map<Integer, List<CityVO>> cityMap = cityVOS.stream().collect(Collectors.groupingBy(CityVO::getPid));
        List<CityVO> parentList = cityMap.get(0);
        buildChild(parentList, cityMap);
        return parentList;
    }

    private static void buildChild(List<CityVO> parentList, Map<Integer, List<CityVO>> cityMap) {
        if (CollectionUtils.isEmpty(parentList)) {
            return;
        }
        for (CityVO cityVO : parentList) {
            List<CityVO> children = cityMap.get(cityVO.getCode());
            cityVO.setChildren(children);
            buildChild(children, cityMap);
        }
    }
}
