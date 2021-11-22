package com.yanwu.spring.cloud.common.rocket;

import lombok.Data;
import lombok.ToString;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:58.
 * <p>
 * description:
 */
@Data
@ToString
public class WrapperSendResult {

    private String messageId;

    private String topic;

}
