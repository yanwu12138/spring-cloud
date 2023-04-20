package com.yanwu.spring.cloud.common.utils.pay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AgreementParams;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.yanwu.spring.cloud.common.pojo.PayParamBO;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XuBaofeng.
 * @date 2023/4/12 17:13.
 * <p>
 * description: 支付宝支付
 * https://opendocs.alipay.com/pre-open/20170601105911096277new/hug4rm?pathHash=fe045966#%E6%9C%8D%E5%8A%A1%E7%AB%AF%E4%BD%BF%E7%94%A8%20SDK%20%E5%8A%A0%E7%AD%BE%E5%8F%8A%E8%B0%83%E7%94%A8%20Demo
 */
@Slf4j
@SuppressWarnings("unused")
public class AliPayUtil {

    private static final String ALI_PAY_URL = "https://openapi.alipay.com/gateway.do";
    private static final String NOTIFY_URL = "";
    private static final Map<String, AlipayClient> CLIENT_CACHE = new ConcurrentHashMap<>();

    private AliPayUtil() {
        throw new UnsupportedOperationException("AliPayUtil should never be instantiated");
    }

    private static ResponseEnvelope<?> pay(PayParamBO param) {
        try {
            AlipayClient alipayClient = getClient(param);
            // ----- 支付参数
            AlipayTradePayModel bizModel = new AlipayTradePayModel();
            bizModel.setTotalAmount(String.valueOf(param.getTotal()));
            bizModel.setSubject(param.getBillDec());
            bizModel.setOutTradeNo(PayUtil.createTradeNo(PayUtil.PayEnum.ALI));
            bizModel.setProductCode("GENERAL_WITHHOLDING");
            // ----- 协议参数，其中填充协议号
            AgreementParams agreementParams = new AgreementParams();
            agreementParams.setAgreementNo(param.getMerchantSerial());
            bizModel.setAgreementParams(agreementParams);
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            request.setBizModel(bizModel);
            // ----- 异步通知地址
            request.setNotifyUrl(NOTIFY_URL);
            AlipayTradePayResponse response = alipayClient.execute(request);
            return response.isSuccess() ? ResponseEnvelope.success(response.getBody()) : ResponseEnvelope.failed("支付失败: 生成参数失败");
        } catch (Exception e) {
            log.error("wechat pay failed. param: {}", param, e);
            return ResponseEnvelope.failed("支付失败: 未知错误");
        }
    }

    /***
     * 获取预支付客户端
     * @param param 商户信息
     */
    private synchronized static AlipayClient getClient(PayParamBO param) {
        if (CLIENT_CACHE.containsKey(param.getAppId())) {
            return CLIENT_CACHE.get(param.getAppId());
        }
        AlipayClient client = new DefaultAlipayClient(ALI_PAY_URL, param.getAppId(), "yourprivate_key", "json", "GBK", "alipay_public_key", "RSA2");
        CLIENT_CACHE.put(param.getAppId(), client);
        return client;
    }

}
