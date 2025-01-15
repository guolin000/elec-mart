package com.example.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.common.Result;
import com.example.common.config.AliPayConfig;
//import com.example.mapper.OrderDetailMapper;
//import com.example.mapper.OrderMapper;
//import com.example.mapper.ProductMapper;
//import com.example.admin.pojo.AliPay;
//import com.example.admin.pojo.OrderDetail;
import com.example.common.config.AliPayConfig;
import com.example.entity.AliPay;
import com.example.entity.Orders;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dxp
 */
@RestController
@RequestMapping("/alipay")
public class AliPayController {
    private static final String GATEWAY_URL ="https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT ="JSON";
    private static final String CHARSET ="UTF-8";
    private static final String SIGN_TYPE ="RSA2";
    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private OrdersMapper orderMapper;


//    @ApiOperation(value =  "支付",notes = "支付")
    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public void pay(String  orderNo, HttpServletResponse httpResponse) throws Exception {

//        return Result.success("test");

//        System.out.println("!!!!!"+Integer.valueOf(orderNo));
        List<Orders> orders = orderMapper.selectByOrderNo(orderNo);
        if (orders ==null){
            return ;
        }
        // 1. 创建Client，通用SDK提供的Client，负责调用支付宝的API
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, aliPayConfig.getAppId(),
                aliPayConfig.getAppPrivateKey(), FORMAT, CHARSET, aliPayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建 Request并设置Request参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(aliPayConfig.getNotifyUrl());
//        System.out.println("!!!!!"+aliPayConfig.getAppPrivateKey());
//        System.out.println("!!!!!"+aliPayConfig.getAlipayPublicKey());
//        System.out.println("!!!!!"+aliPayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        double totalPrice=0;
        for (int i=0;i<orders.size();i++){
            totalPrice+=orders.get(i).getPrice();
        }
        bizContent.set("out_trade_no", orders.get(0).getOrderId());  // 我们自己生成的订单编号
        bizContent.set("total_amount", totalPrice); // 订单的总金额
        bizContent.set("subject", "您的订单");   // 支付的名称
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
//
//        bizContent.set("out_trade_no", 25);  // 我们自己生成的订单编号
//        bizContent.set("total_amount", 77.77); // 订单的总金额
//        bizContent.set("subject", "test");   // 支付的名称
//        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置

        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:8081/front/orders"); // 支付完成后自动跳转到本地页面的路径
        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
//        return Result.success();

    }
//    @ApiOperation(value =  "回调",notes = "回调")

    @PostMapping("/notify")  // 注意这里必须是POST接口
    public void payNotify(HttpServletRequest request) throws Exception {
        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
            }

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, aliPayConfig.getAlipayPublicKey(), "UTF-8"); // 验证签名
            // 支付宝验签
            if (checkSignature) {
                // 验签通过
                System.out.println("交易名称: " + params.get("subject"));
                System.out.println("交易状态: " + params.get("trade_status"));
                System.out.println("支付宝交易凭证号: " + params.get("trade_no"));
                System.out.println("商户订单号: " + params.get("out_trade_no"));
                System.out.println("交易金额: " + params.get("total_amount"));
                System.out.println("买家在支付宝唯一id: " + params.get("buyer_id"));
                System.out.println("买家付款时间: " + params.get("gmt_payment"));
                System.out.println("买家付款金额: " + params.get("buyer_pay_amount"));


//                String tradeNo = params.get("out_trade_no");
//                String gmtPayment = params.get("gmt_payment");
//                String alipayTradeNo = params.get("trade_no");
//                // 更新订单状态为已支付，设置支付信息
//                Orders orders = orderMapper.selectById(Integer.valueOf(tradeNo));
//                orders.setStatus("已支付");
//                orders.setOrderTime(gmtPayment);
//                orders.setPayNo(alipayTradeNo);
//                orderMapper.updateById(orders);

            }
        }
    }
}