package com.sdu.broker.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.sdu.broker.service.AccountService;
import com.sdu.broker.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class PayController {
//    private final String APP_ID = "2021000117637728";
    private final String APP_ID = "2021000117637649";
//    private final String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDCC7lWtWETdo9GMVTvWn8q4Tx+pU5B+/qY+RwTcKEliC6yoFRV2N++6wOuQIFLfZBuX2AwA8N6+jM0P12cCNTctNJG8k7yt5ELUzX3dTg3MsUbkM6WceK2Uusav7A44wAZnGNzbYbNonrqh7X+A0YAzDmISVooa/JVxAnKVWJmhS0irAGva2BE7xauk0SYWZ5Qm6g64+NecZROwEfBU2mT7X2de4hEqyjzP2hnTIQ0lq1wtLtNnzaNl9EZ+LhxIChTBKZvleasciwz/UboLnlTt5jYkMu3sznRuEpAHh6sKnsPxek+zKiL+2jmOkx0coXMHT5U91iK3G3nXvRbaxiLAgMBAAECggEAeiTxHGB948d8dl21LhlT9QTklH+vnB9l5gbgomgCN+LaKjx7+bHTXZUHsxcNjBxU3vUQUgj8cputwZSlcwOoIel8d4YtJeFg3rUZQlQ595zwr+Jd+Btl7wiQzzycS6KwPG7RtyXZdfVx09FrQKKE3RmwmVY69zjDyTLmSsOmdgsDycdlrGoG5Q3kcy8Hiwf3GiKysRmlhuM0dgPQEsz/AZbThKQKG28ECDSSBcOQ4j6d7TYlC2tqoHPHzFnrlTvfjg45DwPdVP0XFLykkjmqwZWTF7LkXoM7wZF+J/RnwvT31AodhEQv/o/ku9WyN+sEqCt0VaHoLV/9XQUR4KoKSQKBgQDnqDtPc/MqpIODep/NiBiXpTADfThvvyh48yjl43c10IczfVRT2zJzO+fMzEzSow0j1AUkHWFG/O7yGLllfHRBEt37jFcWMLPNybWKFODKrOKmvn6lOqDnjBDil0OmUk9/P2ckSrA2MJIhwr2ubZrXz1p6i+2G4hRXq4d0QObunQKBgQDWb7c9IJ6oUwzMwNcYQPowLaV8sO8Idd75851Z/n66cjmbxq70OXU87cfBEqbWcxoSQrkFgh2qlZcwlT8mlBc8ok55zlhpDi5UeY9l530i+wCtu6JP+6Mu9qFRpUArXkAq3WVn6RXpG/J50k78s6+U5ZtjFsZHP2Wa/xrU94knRwKBgQDixpGpKfz11a7Y2wXtEjng2diy0baZziw0mvWCdraQe96ezSXsITy2CgLVPXpHCU8nr7qIrCo6cAkJ/qIfc0HrIikkwzHNi0e5QKjyz5VvUudeDEW2QjqZ8UfmSeXzv8gK7Wpu4J63rbygSB+fzbQzZ81PAgQ4csEvQb+MUqy/EQKBgHuylARaBcvZpJ+Uej/Oc8RJAJZuRmUeAZM0OcfJK/eHnr3CnffaEUwlaj5w8PbyDM8N2EdaKJzXkBCBd51cfg8HHbfcFVn8yTMHsHW7VI9SNbECdP4ojZiwwpwjdGHQC7nuXunTJkWDTpuBevkA+j8tODav048T/husHyFM7bxPAoGAObeOHaxu/LFw9/CApEzLFYR9DULSXGuuDEG6DQgK7XEae2HgQWAH8ioRE+eDArNRBz+Jyp9sLsKd0WbRScKjv4YgsmLbAH8vqMXBqpuMs6afqCkYrOEYbZs1kOwGsBky8ACrY5UokXOYijCGO9wchkZJwxAy1z+IEdxZdDABiMc=";
    private final String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCAOIVJPqHbSDH/MmEkTW0+KWAFm4FUkDabuVLh4mDrsP02z7ccwylSMdvZd7/Zz3opnT/d90bKhAf2o5PbwP620d6YS/obyt/wm2xg0a0grGt7CAgyjUv3nov83sL+mDP8VFCJ5Ofww1pY+2hofFhMEL4kDMhE9r87+VHQCe/zT98qqfR0YHlVM6dt5HEhjrPFSlLlPh8pJ+UYlt0XUqR6hBRWDTgLZH+H6O2uM2e2+zxW9GbwftcGs/8I8e9syFshxEs0uFs6rcO8ml/4DkdYKl9zCjDk+gCyBbZW//qYw1tBX6x2z0TCc1y+FedyNxW2MZaBBugr9MCyn2lN6/NxAgMBAAECggEAC80n92XcX+J1N5fbeQvnUiFcxSda7/2WCR81kbTUVt0jcTgNEoij9PzBz3JGJjYCeHIQvs1TprAgS4MiyMcqqokGg936VzSFCBBGGWMzHB7ChcBngL+gjln5aHzYVbaMklzwTMDU2EQivK3nHjbTqayg05CZnj9Xup1n+k3v1+UPLcwClOWLSjZA8dS5taP5WWKb2gNl62TtrqbaygrJDL5/Sd5/T/h8eEf2DC3J+sx0h9RVyJf9KEQ27Vp1xIlnuQ20b0C4Lqnk/EoAcjstGubBboGUpR3y+mw52/0oWYj1UhzQ5FPO3mprDcWq3BHObrnvYHsjsoKj2QwaAcscQQKBgQD1i9NczKcQjDt+ctmGpboHUVhm+PNCmp6fCQLt+rO4c0WydFwXt5q7OxOYUJtql5GQZ1FQVixBDkHJ2vnrwgnChFmMWEZGZqpxm6BTBqN+vHMjONC0qbareDc2uL/ZPoYiZhaPOHItpmS+4W7/P3USIy3EVWDAE6LkBG3cKgcrTwKBgQCFrfs+qWqh1c5DzKB+wTEREEwDJ2a0i1wSTS2tNNRvcYiRD0gp5iGDwh69w0mjH9GYD9rfDSz+iFhwMKBI+N6rhhld4wRwhoOBZkhHgDPPJAo8AQ6SWxFrR2va8MvUVhv/WNF5i5NHOjhQyEABP80crSR+4ot/HM3bgWD0ccRFPwKBgQCdu0N8Ik7TV3HESZyoXP9CQiPqidMiHtb0TEKgguI+A3ZrK0oZRi1URp/QwT8E2vZ775KRa7mj1Le4Jj1TD+NbG8+2UuJZyQuGTTVRADu4ZvADWTTgSQD55xB4y3cZ44sgv7mQKO9XmcKvx3qY5reafhShQUMUP8jsdlfoDOtsCwKBgQCDrCiCw0tWFwwxuhgIYnDbNus4XBINGgvZtw2bwyLQlssG3z+cjaJLbuTq07XvqILBvjQ/anH8UP6Tm0H2PrTNzKulz9vqnoRf7Aib7QqbKQuvqPbacDtLKTFymaacAylWrfy/NR0flpgr8eMhl9+INH9zP+BZl1yprtl6Take7wKBgC7iqzFOAWntEQ7szrsZsxZdwHxF7jSaOZDBKEpiL9YAr2dxROUSXFHHlGZOIjM47K7G5bdnp3/HY7chs6r/1dxCBUJRGNMHRkAwUUG1GrE/Q5GPfw+BWQtlbYCNscOvjcSDSxbWsiJYSAv/ScHRC4meFOzI8HWi+1I2h2Cf4ced";
    private final String CHARSET = "UTF-8";
//    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj55harW0M9WX2c1FghwRjQxNitidgdbIufnS4dZBgQaBD/lZpWqgvFtCXpznS4JUj8PESE5dCIKJNjusoaE+BzhevqYDrkuDjYN6T2Ud8wNJlmnRFvVd9fvHUZvRbh7Vss1Qd8RJYEJyacpgx83dbOdCvxBpUJ7iY/UkyvO5HYLMJDILcNK5W3b7XU9BUUExmrQcsnc7h3CSZuYTF31glIY5B6hLYO9aURvk25cq+KkcHjbouftgVh74osQmP7IfZL2ADzaIQFrZnzpVEt9hy+TiCktgND0z2iULURNxA1ePRoX8lG06yVbIm3zaj0dfwQJYKaBthgydED7EwfAKBQIDAQAB";
    private final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhA25Hf9rBHLYpJplxRfx7PaZt7jqJBgri+pH0C2SRQpR3Ivw9CibMwSKX2X6jXHaJKS4Spi3rH/6uvzbNSRGNZpcWCEx6qqwfcg5uA2D65JtPPApRe6s896iugZSmetfxh91z+wgvOH2wCAHtJqCASX9IAiC0SsD0WxxckjdLHJdJBi7SbLv+QPDn/76Gw9n+B6+56ZHMgfRCoxwJZARMTTsKlklD8AfLr1qpEH6z5iirPihJ2tt7A3V3mohx42hl/ltZlUksXRpcKSHWUv4LjtVZaAmoC5vaHawFTnJMMlABPU2WfEMfb6x5F5uV894P5iGRT+X8Z6BDQQAlzWQFQIDAQAB";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    private final String GATEWAY_URL ="https://openapi.alipaydev.com/gateway.do";
    private final String FORMAT = "JSON";
    //签名方式
    private final String SIGN_TYPE = "RSA2";
    //支付宝异步通知路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    private final String NOTIFY_URL = "http://127.0.0.1/notifyUrl";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
    private final String RETURN_URL = "http://localhost:8443/returnUrl";

    @RequestMapping("/alipay")
    public void alipay(HttpServletResponse httpResponse) throws IOException {
        SecureRandom r= new SecureRandom();
        //实例化客户端,填入所需参数
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //在公共参数中设置回跳和通知地址
        request.setReturnUrl(RETURN_URL);
        request.setNotifyUrl(NOTIFY_URL);
        //商户订单号，商户网站订单系统中唯一订单号，必填
        //生成随机Id
        String out_trade_no = UUID.randomUUID().toString();
        //付款金额，必填
//        String total_amount =Integer.toString(r.nextInt(9999999)+1000000);
        String total_amount = Integer.toString(30);
        //订单名称，必填
        String subject ="平台充值30元";
        //商品描述，可空
        String body = "尊敬的用户, 欢迎续费";
        request.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        String form = "";
//        System.out.println(request.getBizContent());
        try {
            form = alipayClient.pageExecute(request).getBody(); // 调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
//        System.out.println(form);
        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);// 直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();

    }

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/returnUrl", method = RequestMethod.GET)
    public String returnUrl(HttpServletRequest request, HttpServletResponse response,
                            @RequestHeader("Authorization") String authorization)
            throws IOException, AlipayApiException {
        System.out.println("=================================同步回调=====================================");

        // 获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("utf-8"), "utf-8");
            params.put(name, valueStr);
        }

        System.out.println(params);//查看参数都有哪些
        boolean signVerified = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE); // 调用SDK验证签名
        //验证签名通过
        if(signVerified){
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            // 付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            System.out.println("商户订单号="+out_trade_no);
            System.out.println("支付宝交易号="+trade_no);
            System.out.println("付款金额="+total_amount);

            if (!TokenUtils.verify(authorization)) {
                response.setStatus(999);
                return "no";
            }
            accountService.recharge(TokenUtils.getUserId(authorization), Double.parseDouble(total_amount));

            //支付成功，修复支付状态
//            payService.updateById(Integer.valueOf(out_trade_no));
            return "ok";//跳转付款成功页面
        }else{
            return "no";//跳转付款失败页面
        }

    }

}
