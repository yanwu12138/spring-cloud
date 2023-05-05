package com.yanwu.spring.cloud.common.pojo;

import com.yanwu.spring.cloud.common.utils.pay.PayUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2023/4/12 17:13.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class PayParamBO implements Serializable {
    private static final long serialVersionUID = 6092700009402779247L;

    // ============================== 商户信息 ============================== //
    /*** 商户ID ***/
    private String merchantSerial;
    /*** 商户号 ***/
    private String merchantId;
    /*** 商户私钥 ***/
    private String keyPath;

    // ============================== 应用相关配置 ============================== //
    /*** 应用ID ***/
    private String appId;
    /*** 客户ID ***/
    private String mchId;

    // ============================== 订单信息 ============================== //
    /*** 金额 ***/
    private Integer total;
    /*** 货币 ***/
    private String currency;
    /*** 订单备注信息 ***/
    private String billDec;

    /***
     * 支付参数校验
     * @param payType 支付类型
     * @return [true: 校验成功; false: 校验失败]
     */
    public boolean checkParam(PayUtil.PayEnum payType) {
        switch (payType) {
            case ALI:
                return aliCheck();
            case WECHAT:
                return wechatCheck();
            default:
                return false;
        }
    }

    private boolean aliCheck() {
        return true;
    }

    private boolean wechatCheck() {
        return true;
    }

}
