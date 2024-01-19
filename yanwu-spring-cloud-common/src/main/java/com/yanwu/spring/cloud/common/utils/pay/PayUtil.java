package com.yanwu.spring.cloud.common.utils.pay;

import com.yanwu.spring.cloud.common.pojo.PayParamBO;
import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.CommandUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author XuBaofeng.
 * @date 2023/4/12 17:18.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class PayUtil {

    private PayUtil() {
        throw new UnsupportedOperationException("PayUtil should never be instantiated");
    }

    /***
     * 在线支付
     * @param payType 支付方式
     * @param param   支付参数
     * @return 支付结果
     */
    public static Result<?> pay(PayEnum payType, PayParamBO param) {
        try {
            if (!param.checkParam(payType)) {
                log.error("pay failed, because param check failed. payType: {}, param: {}.", payType, param);
                return Result.failed("支付失败: 参数校验不通过.");
            }
            Object payResult = CommandUtil.invoke(payType.getClazz(), "pay", param);
            log.info("pay done. payType: {}, param: {}, result: {}", payType, param, payResult);
            return JsonUtil.convertObject(payResult, Result.class);
        } catch (Exception e) {
            log.error("pay failed. payType: {}, param: {}.", payType, param, e);
            return Result.failed(e.getMessage());
        }
    }

    private static final AtomicLong TRADE_NO_SEQ = new AtomicLong(1);

    /***
     * 获取账单ID
     * @return 账单ID
     */
    public static synchronized String createTradeNo(PayEnum payType) {
        return payType.getTradeTag() + System.currentTimeMillis() + TRADE_NO_SEQ.incrementAndGet();
    }

    public static void main(String[] args) {
        pay(PayEnum.WECHAT, new PayParamBO());
        System.out.println(createTradeNo(PayEnum.ALI));
        System.out.println(createTradeNo(PayEnum.ALI));
        System.out.println(createTradeNo(PayEnum.ALI));
        System.out.println(createTradeNo(PayEnum.ALI));
        System.out.println(createTradeNo(PayEnum.ALI));

        System.out.println(createTradeNo(PayEnum.WECHAT));
        System.out.println(createTradeNo(PayEnum.WECHAT));
        System.out.println(createTradeNo(PayEnum.WECHAT));
        System.out.println(createTradeNo(PayEnum.WECHAT));
        System.out.println(createTradeNo(PayEnum.WECHAT));
    }


    /*** 支付类型 ***/
    @Getter
    public enum PayEnum {
        ALI(1, "AL", AliPayUtil.class),
        WECHAT(2, "WX", WechatPayUtil.class);

        private final int type;
        private final Class<?> clazz;
        private final String tradeTag;

        PayEnum(int type, String tradeTag, Class<?> clazz) {
            this.type = type;
            this.clazz = clazz;
            this.tradeTag = tradeTag;
        }
    }

}
