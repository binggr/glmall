package com.binggr.glmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.binggr.glmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private String app_id = "2021000116666697";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCaWEOvX+6WpQdD4V6oczAIiKGLEwUvA1PThFwBu++ZK7x1id2GWi3Dvbi3XB/52dBJeyBBHCKz2pdxzXkM6ZAENH7X38FGqTo0pOXfvCOZ6yct2opbSN4KdsiJ6cZ9uTcz6IaoFmyIp7IoZsZjAWufGutY4tUSW5LsJH5RaEkbG9YMAX++jAc9Lb61Tj2/m2K3+XS0jBhxrugAKgQ0sqzPQNq5McyOPFwIDV7h4SwnRkjJbuGhrGmFWlrgU9dpzL2iPTzfN8nZX+0qxelrM5Hjaptj2cPI0qfSG8PxZHY5x8maDxC0LzvfSENIHO79K1DnvA1JTgRz2VmZb29rp9PlAgMBAAECggEANc51cisQYwYPHVPxCup1IVR4dKJh2TzguUlYReZ9pblAjv1L8+12LNn0DkAYiaKsYSGCYhBr7KVUzICkJaM66KxgV+OSXo7aUsX1uWS+SIdY9jxMpXwmpCE60hFkeCncm8UBRJHkrlM/4aPKn3M8UJAy4Z7aRvcTpi0muDyR19f2hsUovlGmJfIrqBzleDfl9kBi0QyAHzwOLvxP+TunIOZp6NNJQWkCLWmFGzOWdQ8qzHrKTO5vehnv6q7LulJx61KDD4R2Yz3lMg9SfaLeSVeKOrpW7U1bQYrhrQeIWtWFIRF1JWw/HqsSE3uSfsCXdmkO1OfZYjoZgu3agsW+AQKBgQDUT20Oha4d9s7w1hzrE63SljG5s4C4vdjgoUPmjcRhnLlhudLiEqvlba7LfS4wPZ+U5x3vOwbmAo1sirRaUpPVSDmq/bG/EWqoD8FkuN6h4XmdBAztaR0PPcI69cRdRI8mFsItAqP3GCb2l74/4wL1FUigkWqq8VnYn5cv8dbkgQKBgQC6GzK6qlXPdpIPaBPep25ABqNKiO9NsBdzLzo1qIDzmhc2UGcmlA4wDUHle+40xUuV3rC9mzNoriL/Bd6F8PpQXrcXdIErRIJnIrLONoBp53Rr7BdatWbdFnuVpUFPcY2SpI+zyEwCsR8SmJ6HuWm3AhPPWEp0XgJenwY6bTUtZQKBgHkd8n4FXwYzBW05CHhG2rO3ARkKQRWjIDVNaln9aBmzecI5aweTVULmJoBpok7c6MveaSJPzyiqLhnMqwSX9UQIFvXoEl2IxwqnKlGWcVNyOJmNdP5/J5fJSKOwDtySPwOgmhdpWTaeLaQhfuQdUlOUV8FaE7r/Qp0ktfAhC9GBAoGBAIb/Qa4b7wD9vneEhJ1HEHdLGVnwuL2+1VPNV0kjAN8z0FCqxBg834nFpB5F5oXK88gz8DqpgQIXBvW8mSzJn5l7Cw7tiOEM4hbUGaXx7PZlWZMLf+lhmssXCvCDHjv1X7+WtVKxIUn2l1aS9LP5MBD7ZclK/z8oDkn6yrbJDwDtAoGAEUYMNBwskvk6nBWS75ZH1MMmdqu3zhBgMJxXcLntQdb+Yx4sdjE+QZfTBaPrnM2mSVoc+xqyUgroKz/Q6sxU9H78CgEmpRnb6Af6NG3Iwrg2flB+Zo4PYAp2XWynmizAsbugAtM6KiEkPHh1FQ7ICq6obm5U26oQQ4waFzR4K+A=";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1v594TwCx4FLphyF5cKzUY5q5AHsMzgSY53K+RDNpJYyi5OUYP4oG/+KKaR6KpTpL37Cq1GwLluRRmi6q8Xkfd+rvXF8F+y3fAXXIOkUjpu853EIfD+8RMSxlJKiUb0ImdGEVLMisbZ5N2Y0eXQJK2q43NH06+/rvMIJiQVdVAG9X87cgoZHXjkfzvNkHtGu7/aHVOsjKAQ7zfdEYYfBjOR81xnb6bfEy8uAJ6+3Qv1Y3U3cezsLkE8eFQpOFdAEn4SC6CYHFXxO7t5STk9KuPMaDN6GPcCKOnI8ewIm+LPSVJPkAcEyWn0iAslSB4IS8xB1Y9tQDsEMTIn/grKsOwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private String notify_url = "http://ucrqusgies.52http.net/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private String return_url = "http://member.glmall.com/memberOrder.html";

    // 签名方式
    private String sign_type = "RSA2";

    // 字符编码格式
    private String charset = "utf-8";

    //超时时间
    private String timeout_express = "30m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        //超时1min取消支付
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"timeout_express\":\"" + timeout_express + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应：" + result);

        return result;

    }
}
