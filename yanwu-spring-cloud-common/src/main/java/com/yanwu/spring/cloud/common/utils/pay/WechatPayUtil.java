package com.yanwu.spring.cloud.common.utils.pay;


import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.core.exception.ValidationException;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.yanwu.spring.cloud.common.pojo.PayParamBO;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XuBaofeng.
 * @date 2023/4/12 17:13.
 * <p>
 * description: 微信支付
 * https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay6_0.shtml
 */
@Slf4j
@SuppressWarnings("unused")
public class WechatPayUtil {

    private static final Map<String, Config> MERCHANT_CONFIG_CACHE = new ConcurrentHashMap<>();

    private WechatPayUtil() {
        throw new UnsupportedOperationException("WechatPayUtil should never be instantiated");
    }

    /***
     * 微信支付
     * @param param 支付参数
     * @return 支付结果
     */
    private static Result<String> pay(PayParamBO param) {
        try {
            PrepayResponse prepay = getServer(param).prepay(getRequest(param));
            log.info("wechat pay success. param: {}, result: {}", param, prepay);
            return Result.success(prepay.getCodeUrl());
        } catch (HttpException e) {
            log.error("wechat pay failed. param: {}", param, e);
            return Result.failed("支付失败: 调用微信支付失败");
        } catch (ValidationException e) {
            log.error("wechat pay failed. param: {}", param, e);
            return Result.failed("支付失败: 签名失败");
        } catch (ServiceException e) {
            log.error("wechat pay failed. param: {}", param, e);
            return Result.failed("支付失败: 微信支付服务内部错误");
        } catch (MalformedMessageException e) {
            log.error("wechat pay failed. param: {}", param, e);
            return Result.failed("支付失败: 微信支付服务返回异常");
        } catch (Exception e) {
            log.error("wechat pay failed. param: {}", param, e);
            return Result.failed("支付失败: 未知错误");
        }
    }

    private static final String API_V3_KEY = "";
    private static final String NOTIFY_URL = "";

    /***
     * 获取商户的配置缓存构建server
     * @param param 商户信息
     * @return server
     */
    private synchronized static NativePayService getServer(PayParamBO param) {
        Config config;
        if (MERCHANT_CONFIG_CACHE.containsKey(param.getMerchantId())) {
            config = MERCHANT_CONFIG_CACHE.get(param.getMerchantId());
        } else {
            config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(param.getMerchantId())
                    .merchantSerialNumber(param.getMerchantSerial())
                    .privateKeyFromPath(param.getKeyPath())
                    .apiV3Key(API_V3_KEY)
                    .build();
            MERCHANT_CONFIG_CACHE.put(param.getMerchantId(), config);
        }
        return new NativePayService.Builder().config(config).build();
    }

    private synchronized static PrepayRequest getRequest(PayParamBO param) {
        // ----- 金额和货币类型(默认用人民币)
        Amount amount = new Amount();
        amount.setTotal(param.getTotal());
        amount.setCurrency(param.getCurrency() == null ? "CNY" : param.getCurrency());
        // ----- 组装请求
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setAmount(amount);
        prepayRequest.setAppid(param.getAppId());
        prepayRequest.setMchid(param.getMchId());
        prepayRequest.setNotifyUrl(NOTIFY_URL);
        prepayRequest.setOutTradeNo(PayUtil.createTradeNo(PayUtil.PayEnum.WECHAT));
        prepayRequest.setDescription(param.getBillDec());
        return prepayRequest;
    }

}
