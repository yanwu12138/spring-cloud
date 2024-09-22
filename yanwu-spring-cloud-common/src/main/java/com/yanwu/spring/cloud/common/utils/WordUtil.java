package com.yanwu.spring.cloud.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/9/19 20:03.
 * <p>
 * description:
 */
@Slf4j
public class WordUtil {

    private WordUtil() {
        throw new UnsupportedOperationException("WordUtil should never be instantiated");
    }

    @Data
    @Accessors(chain = true)
    private static class ShipInfo implements Serializable {
        private static final long serialVersionUID = 1028297211426982642L;
        private String 船只ID;
        private String 船名;
        private String 船主姓名;
        private String 船只联系电话;
        private String 安装日期;
        private String 岸上联系电话;
        private String 其它卫星设备;
        private String 固话号码;
        private String 安装地点;
        private String 办事处;
        private String 安装政策;
        private String 安装类型;
        private String 天线型号;
        private String 猫型号;
        private Integer 主机盒型号;
        private String 主机盒安装位置;
        private String 供电方式;
        private String 天线序列号;
        private String 猫序列号;
        private String 主机盒序列号;
        private String 天线安装位置;
        private String 空载电压;
        private String 安装人员签字;

        public String build主机盒型号() {
            if (主机盒型号 == null) {
                return "";
            }
            switch (主机盒型号) {
                case 12:
                    return "N2";
                case 13:
                    return "N1";
                case 1:
                default:
                    return "主机盒";
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String shipInfoJson = "[{\"船只ID\":13845,\"船名\":\"闽福鼎渔09101\",\"船主姓名\":\"顾文忠\",\"船只联系电话\":\"13868242442\",\"安装日期\":\"2023.4.29 00:00:00\",\"岸上联系电话\":\"13758021200\",\"其它卫星设备\":null,\"固话号码\":\"\",\"安装地点\":\"闸口\",\"办事处\":\"舟山办事处\",\"安装政策\":\"普通渔船\",\"安装类型\":null,\"天线型号\":\"VS80H3\",\"猫型号\":\"Newtec\",\"主机盒型号\":1,\"主机盒安装位置\":\"船长室墙面\",\"供电方式\":\"稳压电源\",\"天线序列号\":\"HW000923N0843\",\"猫序列号\":\"7294400302784\",\"主机盒序列号\":\"201811000103\",\"天线安装位置\":\"右后方-3\",\"空载电压\":\"27.2\",\"安装人员签字\":\"陈雷明\"},{\"船只ID\":14263,\"船名\":\"闽福鼎渔09798\",\"船主姓名\":\"翁思立\",\"船只联系电话\":\"15960721935\",\"安装日期\":\"2023.6.16 00:00:00\",\"岸上联系电话\":\"13860717949  \",\"其它卫星设备\":null,\"固话号码\":\"0580-5851515\",\"安装地点\":\"晋江\",\"办事处\":\"泉州办事处\",\"安装政策\":\"普通渔船\",\"安装类型\":null,\"天线型号\":\"VS80E3\",\"猫型号\":\"Newtec\",\"主机盒型号\":1,\"主机盒安装位置\":\"船长室墙面\",\"供电方式\":\"稳压电源\",\"天线序列号\":\"EW001917N3335\",\"猫序列号\":\"7222700336722\",\"主机盒序列号\":\"20181100027b\",\"天线安装位置\":\"左前方-1\",\"空载电压\":\"\",\"安装人员签字\":\"欧明星\"},{\"船只ID\":64,\"船名\":\"闽狮渔06053\",\"船主姓名\":\"周宗庆\",\"船只联系电话\":\"17750715353\",\"安装日期\":\"2023.5.29 00:00:00\",\"岸上联系电话\":\"18759933635\",\"其它卫星设备\":null,\"固话号码\":\"0580-5851339\",\"安装地点\":\"祥芝\",\"办事处\":\"泉州办事处\",\"安装政策\":\"普通渔船\",\"安装类型\":null,\"天线型号\":\"VS80E3\",\"猫型号\":\"Newtec\",\"主机盒型号\":1,\"主机盒安装位置\":\"船长室墙面\",\"供电方式\":\"稳压电源\",\"天线序列号\":\"EW001917N3560\",\"猫序列号\":\"7294900307748\",\"主机盒序列号\":\"201906000562\",\"天线安装位置\":\"左后方-4\",\"空载电压\":\"24V\",\"安装人员签字\":\"蔡松勇，胡夏夫\"},{\"船只ID\":8750,\"船名\":\"闽狮渔06906\",\"船主姓名\":\"蔡天恭\",\"船只联系电话\":\"13850784566\",\"安装日期\":\"2020.12.15 00:00:00\",\"岸上联系电话\":\"13799882477\",\"其它卫星设备\":null,\"固话号码\":\"0580-5851378\",\"安装地点\":\"石狮\",\"办事处\":\"泉州办事处\",\"安装政策\":\"普通渔船\",\"安装类型\":null,\"天线型号\":\"VS80E3\",\"猫型号\":\"Newtec\",\"主机盒型号\":1,\"主机盒安装位置\":\"船长室墙面\",\"供电方式\":\"稳压电源\",\"天线序列号\":\"EW001917N3449\",\"猫序列号\":\"7222600336494\",\"主机盒序列号\":\"2019060005c7\",\"天线安装位置\":\"3和4中间\",\"空载电压\":\"24\",\"安装人员签字\":\"林庆平\"},{\"船只ID\":13986,\"船名\":\"浙普渔42768\",\"船主姓名\":\"胡鹏其\",\"船只联系电话\":\"18858380383\",\"安装日期\":\"2023.5.30 00:00:00\",\"岸上联系电话\":\"13506600690\",\"其它卫星设备\":null,\"固话号码\":\"\",\"安装地点\":\"\",\"办事处\":\"舟山办事处\",\"安装政策\":\"普通渔船\",\"安装类型\":null,\"天线型号\":\"VS80H3\",\"猫型号\":\"Newtec\",\"主机盒型号\":1,\"主机盒安装位置\":\"船长室墙面\",\"供电方式\":\"稳压电源\",\"天线序列号\":\"HW001052N1543\",\"猫序列号\":\"7294900307697\",\"主机盒序列号\":\"20190600052d\",\"天线安装位置\":\"左后方-4\",\"空载电压\":\"\",\"安装人员签字\":\"范江洪可杰\"}]";
        List<ShipInfo> shipInfos = JsonUtil.toObjectList(shipInfoJson, ShipInfo.class);
        for (ShipInfo info : shipInfos) {
            System.out.println(JsonUtil.toString(info));
            toShipDoc(info);
        }
    }

    private static final String SOURCE_TEMPLATE = "/Users/xubaofeng/yanwu/tools/确认单/波星通卫星通信终端安装调试确认单模版.docx";
    private static final String TARGET_PATH = "/Users/xubaofeng/yanwu/tools/确认单/target/";

    private static void toShipDoc(ShipInfo shipInfo) throws Exception {
        String target = TARGET_PATH + shipInfo.get船只ID() + "_" + shipInfo.get船名() + ".docx";
        // ----- 打开现有的Word文档
        try (FileInputStream fis = new FileInputStream(SOURCE_TEMPLATE)) {
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFTable> tables = document.getTables();
            if (CollectionUtils.isEmpty(tables)) {
                return;
            }
            XWPFTable xwpfTable = tables.get(0);
            for (int rowIndex = 0; rowIndex < xwpfTable.getNumberOfRows(); rowIndex++) {
                if (rowIndex == 0) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 船名
                    writeText(row.getCell(1), shipInfo.get船名());
                    // ----- 船长姓名
                    writeText(row.getCell(3), shipInfo.get船主姓名());
                    // ----- 船长电话
                    writeText(row.getCell(5), shipInfo.get船只联系电话());
                }
                if (rowIndex == 1) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 安装日期
                    if (StringUtils.isNotBlank(shipInfo.get安装日期())) {
                        writeText(row.getCell(1), shipInfo.get安装日期().split(" ")[0]);
                    }
                    // ----- 岸上联系电话
                    writeText(row.getCell(5), shipInfo.get岸上联系电话());
                }
                if (rowIndex == 2) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 其它卫星设备
                    replaceText(row.getCell(1), "无");
                    // ----- 固话号码
                    writeText(row.getCell(3), shipInfo.get固话号码());
                }
                if (rowIndex == 3) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 安装地点
                    writeText(row.getCell(1), shipInfo.get安装地点());
                    // ----- 办事处/代理
                    writeText(row.getCell(3), shipInfo.get办事处());
                    // ----- 安装政策
                    if (StringUtils.isNotBlank(shipInfo.get安装政策())) {
                        replaceText(row.getCell(5), ((StringUtils.isNotBlank(shipInfo.get安装政策()) && shipInfo.get安装政策().contains("买断")) ? "购买" : "借用"));
                    }
                }
                if (rowIndex == 4) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 安装类型
                    replaceText(row.getCell(3), "新装");
                }
                if (rowIndex == 5) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 天线型号
                    writeText(row.getCell(1), shipInfo.get天线型号());
                    // ----- 猫型号
                    replaceText(row.getCell(3), shipInfo.get猫型号());
                    // ----- 主机盒型号
                    writeText(row.getCell(5), shipInfo.build主机盒型号());
                }
                if (rowIndex == 6) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 主机盒安装位置
                    if (StringUtils.isNotBlank(shipInfo.get主机盒安装位置())) {
                        String insert = shipInfo.get主机盒安装位置().contains("墙面") ? "船长室墙面" : shipInfo.get主机盒安装位置().contains("桌面") ? "船长室桌面" : "船长室墙面";
                        replaceText(row.getCell(1), insert);
                    }
                    // ----- 主机盒供电方式
                    if (StringUtils.isNotBlank(shipInfo.get供电方式())) {
                        String PowerMode = shipInfo.get供电方式().equals("稳压电源") ? "稳压电源" : "交转直";
                        replaceText(row.getCell(3), PowerMode);
                    }
                }
                if (rowIndex == 8) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    // ----- 天线、猫、主机盒序列号
                    List<XWPFParagraph> paragraphs = row.getCell(0).getParagraphs();
                    String content = String.join("            ", filling(shipInfo.get天线序列号()), filling(shipInfo.get猫序列号()), filling(shipInfo.get主机盒序列号()));
                    XWPFRun run = paragraphs.get(2).createRun();
                    run.setFontSize(9);
                    run.setText(content);
                }
                if (rowIndex == 11 || rowIndex == 12) {
                    if (StringUtils.isNotBlank(shipInfo.get天线安装位置()) && shipInfo.get天线安装位置().contains("-")) {
                        String insert = shipInfo.get天线安装位置().split("-")[1];
                        switch (insert) {
                            case "1": {
                                XWPFTableRow row = xwpfTable.getRow(11);
                                for (XWPFParagraph paragraph : row.getCell(0).getParagraphs()) {
                                    if (paragraph.getText().contains("1")) {
                                        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                            paragraph.removeRun(i);
                                        }
                                        XWPFRun run = paragraph.createRun();
                                        run.setText("☑1");
                                    }
                                }
                                break;
                            }
                            case "2": {
                                XWPFTableRow row = xwpfTable.getRow(12);
                                for (XWPFParagraph paragraph : row.getCell(0).getParagraphs()) {
                                    if (paragraph.getText().contains("2")) {
                                        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                            paragraph.removeRun(i);
                                        }
                                        XWPFRun run = paragraph.createRun();
                                        run.setText("☑2");
                                    }
                                }
                                break;
                            }
                            case "3": {
                                XWPFTableRow row = xwpfTable.getRow(11);
                                for (XWPFParagraph paragraph : row.getCell(1).getParagraphs()) {
                                    if (paragraph.getText().contains("3")) {
                                        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                            paragraph.removeRun(i);
                                        }
                                        XWPFRun run = paragraph.createRun();
                                        run.setText("☑3");
                                    }
                                }
                                break;
                            }
                            case "4": {
                                XWPFTableRow row = xwpfTable.getRow(12);
                                for (XWPFParagraph paragraph : row.getCell(1).getParagraphs()) {
                                    if (paragraph.getText().contains("4")) {
                                        for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                            paragraph.removeRun(i);
                                        }
                                        XWPFRun run = paragraph.createRun();
                                        run.setText("☑4");
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if (rowIndex == 15) {
                    // ----- 空载电压tableRow
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    writeText(row.getCell(2), shipInfo.get空载电压());
                }
                if (rowIndex == 18 || rowIndex == 19 || rowIndex == 20 || rowIndex == 21) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    row.getCell(2).setText("☑");
                }
                if (rowIndex == 22) {
                    if (RandomUtils.nextBoolean()) {
                        XWPFTableRow row = xwpfTable.getRow(rowIndex);
                        row.getCell(2).setText("☑");
                    }
                }
                if (rowIndex == 23) {
                    XWPFTableRow row = xwpfTable.getRow(rowIndex);
                    for (XWPFParagraph paragraph : row.getCell(0).getParagraphs()) {
                        if (paragraph.getText().contains("用户签字")) {
                            String replace = "*用户签字：" + shipInfo.get船主姓名() + "                                        *安装人员签字：" + shipInfo.get安装人员签字();
                            for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                paragraph.removeRun(i);
                            }
                            XWPFRun run = paragraph.createRun();
                            run.setFontSize(10);
                            run.setText(replace);
                        }
                    }
                }
            }
            // ----- 保存修改后的文档
            FileUtil.deleteFile(target);
            FileUtil.checkFilePath(target, true);
            try (FileOutputStream out = new FileOutputStream(target)) {
                document.write(out);
            }
        }
    }

    private static void writeText(XWPFTableCell cell, String content) {
        if (StringUtils.isBlank(content) || content.equals("null") || content.equals("NULL")) {
            return;
        }
        cell.setText(content);
        cell.getParagraphs().forEach(paragraph -> paragraph.getRuns().forEach(run -> run.setFontSize(8)));
    }

    private static void replaceText(XWPFTableCell cell, String content) {
        if (StringUtils.isBlank(content) || content.equals("null") || content.equals("NULL")) {
            return;
        }
        for (XWPFParagraph paragraph : cell.getParagraphs()) {
            if (paragraph.getText().contains(content)) {
                String replace = paragraph.getText().replace("□" + content, "☑" + content);
                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                    paragraph.removeRun(i);
                }
                XWPFRun run = paragraph.createRun();
                run.setFontSize(9);
                run.setText(replace);
            }
        }
    }

    private static String filling(String content) {
        if (StringUtils.isBlank(content)) {
            content = "";
        }
        if (content.length() >= 20) {
            return content;
        }
        int diff = (20 - content.length()) / 2;
        StringBuilder builder = new StringBuilder(content);
        while (diff > 0) {
            builder = new StringBuilder(" " + builder + " ");
            diff--;
        }
        return builder.toString();
    }

}
