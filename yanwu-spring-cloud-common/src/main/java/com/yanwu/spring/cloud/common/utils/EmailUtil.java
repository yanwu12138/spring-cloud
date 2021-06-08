package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Encoding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-10-22 14:17.
 * <p>
 * description: 邮件工具类
 */
@Slf4j
@SuppressWarnings("unused")
public class EmailUtil {
    /*** 发件人地址 */
    private static final String SEND_ADDRESS = "499496273@qq.com";
    /*** 发件人账户名 */
    private static final String ACCOUNT = "499496273@qq.com";
    /*** 发件人账户密码[注：此密码不是邮箱登陆密码，而是邮箱授权密码] */
    private static final String PASSWORD = "xxxxxxxx";

    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";

    /*** 连接邮件服务器的参数配置 */
    private static final Properties PROPERTIES;

    static {
        PROPERTIES = new Properties();
        // ----- 设置用户的认证方式
        PROPERTIES.setProperty("mail.smtp.auth", "true");
        // ----- 设置传输协议
        PROPERTIES.setProperty("mail.transport.protocol", "smtp");
        // ----- 设置链接超时
        PROPERTIES.setProperty("mail.smtp.timeout", "10000");
        // ----- 设置发件人的SMTP服务器地址
        PROPERTIES.setProperty("mail.smtp.host", "smtp.qq.com");
        // ----- 设置ssl端口
        PROPERTIES.setProperty("mail.smtp.port", "465");
        PROPERTIES.setProperty("mail.smtp.socketFactory.port", "465");
        PROPERTIES.setProperty("mail.smtp.socketFactory.fallback", "false");
        PROPERTIES.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    private EmailUtil() {
        throw new UnsupportedOperationException("EmailUtil should never be instantiated");
    }

    /**
     * 发送邮件[使用指定的发件人信息]
     *
     * @param sendAddress 发送人地址
     * @param account     发送人账号
     * @param password    发送人密码
     * @param toRecipient 接收人地址
     * @param ccRecipient 抄送人地址 [为null则不抄送]
     * @param subject     主题
     * @param content     内容
     * @param attachments 附件 [为null则表示无附件]
     * @throws Exception e
     */
    public static void sendEmail(String sendAddress, String account, String password,
                                 String toRecipient, String[] ccRecipient,
                                 String subject, String content, String... attachments) throws Exception {
        log.info("发送人: [{}], 接收人: [{}], 抄送人: {}, 主题: [{}], 内容: [{}], 附件: {}", sendAddress, toRecipient, ccRecipient, subject, content, attachments);
        // ===== 参数校验
        Assert.isTrue(StringUtils.isNotBlank(sendAddress), "sender address cannot be empty!");
        Assert.isTrue(StringUtils.isNotBlank(account), "sender account cannot be empty!");
        Assert.isTrue(StringUtils.isNotBlank(password), "sender password cannot be empty!");
        Assert.isTrue(StringUtils.isNotBlank(toRecipient), "recipient address cannot be empty!");
        Assert.isTrue(StringUtils.isNotBlank(subject), "mail subject cannot be empty!");
        Assert.isTrue(StringUtils.isNotBlank(content), "mail content cannot be empty!");
        // ===== 创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(PROPERTIES);
        // ===== 创建邮件的实例对象
        Message msg = getMimeMessage(session, sendAddress, toRecipient, ccRecipient, subject, content, attachments);
        // ===== 根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport();
        // ----- 设置发件人的账户名和密码
        transport.connect(account, password);
        // ----- 发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());
        // ===== 关闭邮件连接
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     *
     * @param session     session
     * @param sendAddress 发送人地址
     * @param toRecipient 接收人地址
     * @param ccRecipient 抄送人地址
     * @param subject     主题
     * @param content     内容
     * @param attachments 附件
     * @return 邮件的实例对象
     * @throws Exception e
     */
    private static MimeMessage getMimeMessage(Session session, String sendAddress,
                                              String toRecipient, String[] ccRecipient,
                                              String subject, String content, String... attachments) throws Exception {
        // ----- 创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        // ===== 发件人地址
        msg.setFrom(new InternetAddress(sendAddress));
        // ===== 收件人地址
        msg.setRecipients(MimeMessage.RecipientType.TO, toRecipient);
        if (ArrayUtil.isNotEmpty(ccRecipient)) {
            // ----- 抄送人
            Address[] addresses = new InternetAddress[ccRecipient.length];
            for (int i = 0; i < ccRecipient.length; i++) {
                String cc = ccRecipient[i];
                Assert.isTrue(StringUtils.isNotBlank(cc), "Cc address cannot be empty!");
                addresses[i] = new InternetAddress(cc);
            }
            msg.setRecipients(MimeMessage.RecipientType.CC, addresses);
        }
        // ===== 邮件主题
        msg.setSubject(subject, Encoding.UTF_8);
        // ===== 邮件处理附件
        if (ArrayUtil.isEmpty(attachments)) {
            // ----- 邮件正文
            msg.setContent(content, CONTENT_TYPE);
        } else {
            // ----- 附件
            MimeMultipart mm = new MimeMultipart();
            // ----- 邮件正文
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(content, CONTENT_TYPE);
            mm.addBodyPart(text);
            // ----- 处理附件
            for (String path : attachments) {
                if (StringUtils.isBlank(path)) {
                    continue;
                }
                // ===== 创建附件节点
                MimeBodyPart attachment = new MimeBodyPart();
                // ----- 读取本地文件
                DataHandler dh = new DataHandler(new FileDataSource(path));
                // ----- 将附件数据添加到节点
                attachment.setDataHandler(dh);
                // ----- 设置附件的文件名（需要编码）
                attachment.setFileName(MimeUtility.encodeText(dh.getName(), Encoding.UTF_8, null));
                mm.addBodyPart(attachment);
            }
            mm.setSubType("mixed");
            msg.setContent(mm);
        }
        // ----- 设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
        return msg;
    }

}
