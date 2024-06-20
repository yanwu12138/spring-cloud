package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.RequestInfo;
import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/6/20 15:17.
 * <p>
 * description:
 */
@Slf4j
public class SmsUtil {

    public SmsUtil() {
        throw new UnsupportedOperationException("SmsUtil should never be instantiated");
    }

    public static void main(String[] args) {
        String phone = "xxxxxx";
        String url = "http://smssh1.253.com/msg/send/json";
        String account = "xxxxxxx", password = "xxxxxxx";
        String code = randomCode(6);
        SmsParam param = SmsParam.newInstance("【波星通】您的验证码是：" + code + "，5分钟内有效。如非您本人操作，请忽略。", phone, "true", null);
        SmsResult smsResult = sendSms(url, account, password, param);
        log.info(smsResult.toString());
    }

    public static SmsResult sendSms(String url, String account, String password, SmsParam param) {
        param.setAccount(account).setPassword(password);
        RequestInfo<SmsParam, SmsResult> requestInfo = RequestInfo.getInstance(HttpMethod.POST, url, SmsResult.class);
        requestInfo.buildBody(param).buildHeaders("Charset", "UTF-8").buildHeaders("Content-Type", "application/json");
        Result<SmsResult> execute = RestUtil.execute(requestInfo);
        log.info("phone: {}，The result of sending the SMS: {}", param, execute);
        return execute.getData();
    }

    public static String randomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(RandomUtils.nextInt(0, 10));
        }
        return code.toString();
    }

    @Data
    @Accessors(chain = true)
    public static class SmsParam implements Serializable {
        private static final long serialVersionUID = -3612771395199772499L;
        private String account;
        private String password;
        private String msg;
        private String phone;
        private String report;
        private String extend;

        private SmsParam() {
        }

        public static SmsParam newInstance(String msg, String phone, String extend) {
            return newInstance(msg, phone, "false", extend);
        }

        public static SmsParam newInstance(String msg, String phone, String report, String extend) {
            return new SmsParam().setMsg(msg).setPhone(phone).setReport(report).setExtend(extend);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class SmsResult implements Serializable {
        private static final long serialVersionUID = -1935932578444752071L;
        /*** 响应时间 ***/
        private String time;
        /*** 消息id ***/
        private String msgId;
        /*** 状态码说明（成功返回空） ***/
        private String errorMsg;
        /*** 状态码（详细参考提交响应状态码）***/
        private String code;

        public boolean successful() {
            return StringUtils.isNotBlank(code) && code.equals("0");
        }
    }

}
