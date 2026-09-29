package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.request.OrderPayRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.OrdersVO;
import com.wuji.admin.model.vo.PayExtraVO;
import com.wuji.admin.model.vo.PayVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.ItemService;
import com.wuji.admin.service.OrdersService;
import com.wuji.admin.service.PayService;
import com.wuji.common.config.PayConfig;
import com.wuji.common.utils.IpUtils;
import com.wuji.common.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PayServiceImpl implements PayService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CompanyAppService companyAppService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private CompanyService companyService;

    @Value("${pay.channelCode:1007}")
    private String PAY_CHANNEL;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Map<String, Object> orderSingleItem(OrderPayRequest orderPayRequest, HttpServletRequest request) {
        OrdersVO orders = ordersService.createSingleItemOrder(orderPayRequest);
        // 免费产品
        if (orders.getAmount().compareTo(new BigDecimal(0)) <= 0) {
            ordersService.payNotify(orders.getOrderNo(), "none");
            Map<String, Object> map = new HashMap<>();
            map.put("orderNo", orders.getOrderNo());
            map.put("free", 1);
            return map;
        }

        PayVO payVO = new PayVO();
        payVO.setChannelId(PAY_CHANNEL);
        payVO.setCompanyId(orders.getCompanyId().intValue());
        payVO.setCompanyOrderNo(orders.getOrderNo());
        payVO.setAmount(orders.getAmount().multiply(new BigDecimal(100)).longValue());
        payVO.setSubject(orders.getScreenName());
        CompanyVO companyVO = companyService.info(orders.getCompanyId());
        if (companyVO != null) {
            payVO.setSubject(payVO.getSubject() + "（" + companyVO.getCompanyName() + "）");
        }
        // type:3 购买产品
        payVO.setExtra(JSON.toJSONString(new PayExtraVO(orders.getOrderNo(), 3)));
        payVO.setTimeExpire(TimeUtils.formatDateTime(new Date(System.currentTimeMillis() + 600000)));
        payVO.setTradeType("NATIVE");
        payVO.setClientIp(IpUtils.getIpAddr(request));
        return payRequest(payVO);
    }

    private Map<String, Object> payRequest(PayVO payVO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<String>(JSON.toJSONString(payVO), headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(PayConfig.payUrl(), request, String.class);
        String body = responseEntity.getBody();
        String code = JSON.parseObject(body).getString("code");
        if (!code.equals("0")) {
            log.error("request:{},body:{}", JSON.toJSONString(payVO), body);
            throw new AdminException(AdminResultCode.PAY_ERROR);
        }
        log.info("payRequest:{}, Reponse:{}", JSON.toJSONString(payVO), body);
        String data = JSON.parseObject(body).getString("data");
        String codeUrl = JSON.parseObject(data).getString("codeUrl");
        Map<String, Object> map = new HashMap<>();
        map.put("orderNo", payVO.getCompanyOrderNo());
        map.put("codeUrl", codeUrl);
        return map;
    }
}
