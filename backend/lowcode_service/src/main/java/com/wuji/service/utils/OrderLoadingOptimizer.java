package com.wuji.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class OrderLoadingOptimizer {

    /**
     * 主方法：执行装车优化
     *
     * @param input 输入数据，包含 order 和 carInfo
     * @return 装载结果列表
     */
    public static List<Map<String, Object>> optimizeLoading(Map<String, Object> input) {
        // 提取订单信息
        Map<String, Object> order = (Map<String, Object>) input.get("order");
        String orderNo = (String) order.get("orderNo");
        List<Map<String, Object>> orderDetailList = (List<Map<String, Object>>) order.get("orderDetailList");

        // 提取车辆信息
        List<Map<String, Object>> carInfo = (List<Map<String, Object>>) input.get("carInfo");

        // 复制商品列表用于分配（保留原始数据）
        List<Map<String, Object>> products = new ArrayList<>();
        for (Map<String, Object> item : orderDetailList) {
            Map<String, Object> copy = new HashMap<>(item);
            products.add(copy);
        }

        // 按单位体积从大到小排序（优先处理大体积商品）
        // products.sort((p1, p2) -> Double.compare(Double.parseDouble(p2.get("volume").toString()), Double.parseDouble(p1.get("volume").toString())));

        // 存放结果
        List<Map<String, Object>> results = new ArrayList<>();

        // 遍历每辆车
        for (Map<String, Object> car : carInfo) {
            if (products.isEmpty()) {
                break; // 所有商品已装完
            }

            String licensePlateNumber = (String) car.get("licensePlateNumber");
            double carVolume = ((Number) car.get("volume")).doubleValue();
            double remainingCapacity = carVolume;

            // 当前车的装载结果
            Map<String, Object> loadingResult = new HashMap<>();
            loadingResult.put("orderNo", orderNo);
            loadingResult.put("licensePlateNumber", licensePlateNumber);
            loadingResult.put("volume", carVolume);
            loadingResult.put("realVolume", 0.0);

            List<Map<String, Object>> productList = new ArrayList<>();
            loadingResult.put("productList", productList);

            boolean hasLoaded = false;

            Iterator<Map<String, Object>> it = products.iterator();
            while (it.hasNext() && remainingCapacity > 0) {
                Map<String, Object> product = it.next();
                double unitVolume = Double.parseDouble(product.get("volume").toString());
                int availableCount = (Integer) product.get("count");

                if (unitVolume > remainingCapacity) {
                    continue; // 单个都放不下
                }

                int loadCount = Math.min(availableCount, (int) (remainingCapacity / unitVolume));
                if (loadCount <= 0) {
                    continue;
                }

                double loadedVolume = loadCount * unitVolume;

                // 创建装载商品项
                Map<String, Object> loadedProduct = new HashMap<>();
                loadedProduct.put("productCode", product.get("productCode"));
                loadedProduct.put("productName", product.get("productName"));
                loadedProduct.put("spec", product.get("spec"));
                loadedProduct.put("volume", unitVolume);
                loadedProduct.put("count", loadCount);
                loadedProduct.put("smallVolume", loadedVolume);


                productList.add(loadedProduct);

                // 更新剩余数量
                product.put("count", availableCount - loadCount);
                remainingCapacity -= loadedVolume;
                double currentRealVolume = loadingResult.get("realVolume") == null ? 0 :
                        Double.parseDouble(loadingResult.get("realVolume").toString());
                loadingResult.put("realVolume", currentRealVolume + loadedVolume);

                hasLoaded = true;

                // 如果该商品已用完，移除
                if (availableCount - loadCount == 0) {
                    it.remove();
                }
            }

            // 只有实际装了商品才加入结果
            if (hasLoaded) {
                results.add(loadingResult);
            }
        }

        return results;
    }

    // 测试主函数
    public static void main(String[] args) throws JsonProcessingException {
        // 构建输入数据
        Map<String, Object> input = new HashMap<>();

        // 订单详情
        List<Map<String, Object>> orderDetailList = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productCode", "P01");
        item1.put("productName", "钙片");
        item1.put("volume", 0.3);
        item1.put("count", 20);
        item1.put("smallVolume", 6.0);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("productCode", "P02");
        item2.put("productName", "毛片");
        item2.put("volume", 0.2);
        item2.put("count", 40);
        item2.put("smallVolume", 8.0);

        orderDetailList.add(item1);
        orderDetailList.add(item2);

        // 订单
        Map<String, Object> order = new HashMap<>();
        order.put("orderNo", "A001");
        order.put("totalVolume", 14.0);
        order.put("orderDetailList", orderDetailList);

        // 车辆
        List<Map<String, Object>> carInfo = new ArrayList<>();
        carInfo.add(createCar("001", 4.0));
        carInfo.add(createCar("002", 5.0));
        carInfo.add(createCar("003", 6.0));
        carInfo.add(createCar("004", 7.0));

        input.put("order", order);
        input.put("carInfo", carInfo);

        // 执行装车
        List<Map<String, Object>> result = optimizeLoading(input);

        // 输出 JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // 美化输出
        String json = mapper.writeValueAsString(result);
        System.out.println(json);
    }

    // 辅助方法：创建车辆 map
    private static Map<String, Object> createCar(String plate, double volume) {
        Map<String, Object> car = new HashMap<>();
        car.put("licensePlateNumber", plate);
        car.put("volume", volume);
        return car;
    }
}

