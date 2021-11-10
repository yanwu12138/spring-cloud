package com.yanwu.spring.cloud.netty.model.screen;

import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 14:45.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ScreenBaseBO extends DeviceBaseBO {
    private static final long serialVersionUID = 8343735829797804631L;

    /*** 帧头 */
    private String head;
    /*** 设备编号 */
    private String deviceNo;
    /*** 控制码 */
    private String code;
    /*** 数据域 */
    private String data;
    /*** 帧尾 */
    private String end;
    /*** crc校验码 */
    private String crc;

}
