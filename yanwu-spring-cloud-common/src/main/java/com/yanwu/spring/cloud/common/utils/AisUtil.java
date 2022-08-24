package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.AisDynamicDataBO;
import com.yanwu.spring.cloud.common.pojo.AisStaticDataBO;
import dk.tbsalling.aismessages.AISInputStreamReader;
import dk.tbsalling.aismessages.ais.messages.Error;
import dk.tbsalling.aismessages.ais.messages.*;
import dk.tbsalling.aismessages.ais.messages.types.TransponderClass;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/7/5 18:22.
 * <p>
 * description: AIS 相关工具
 */
@Slf4j
public class AisUtil {
    private static final String AIS_SOURCE_PATH = "/home/admin/tmp/file/source/ais/";
    /*** 船舶自身的广播信息 ***/
    private static final String AIVDO = "AIVDO";
    /*** 本船收到的其它船舶的信息 ***/
    private static final String AIVDM = "AIVDM";

    public static void main(String[] args) throws IOException {
        log.info("-------------------- AISMessages Demo App Start --------------------");
        List<AISMessage> message = getMessage(DEMO_NMEA_STRINGS);
        message.forEach((aisMessage) -> log.info("Received AIS message: {}", aisMessage));
        analysis(message);
        log.info("-------------------- AISMessages Demo App End--------------------");
    }

    private AisUtil() {
        throw new UnsupportedOperationException("AisUtil should never be instantiated");
    }

    public static List<AISMessage> getMessage(String message) {
        return StringUtils.isNotBlank(message) ? getMessage(message.getBytes()) : Collections.emptyList();
    }

    public static List<AISMessage> getMessage(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return Collections.emptyList();
        }
        List<AISMessage> result = new ArrayList<>();
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            new AISInputStreamReader(inputStream, result::add).run();
        } catch (Exception e) {
            log.error("ais getMessage error. bytes: {}", ByteUtil.printBytes(bytes), e);
        }
        return result;
    }

    public static void analysis(List<AISMessage> messageList) {
        if (CollectionUtils.isEmpty(messageList)) {
            return;
        }
        messageList.forEach(message -> {
            if (!message.isValid()) {
                return;
            }
            Boolean ownship = ownship(message);
            if (ownship == null) {
                return;
            }
            int mmsi = message.getSourceMmsi().getMMSI();
            if (message instanceof StaticDataReport) {
                staticData(mmsi, ownship, (StaticDataReport) message);
            } else if (message instanceof DynamicDataReport) {
                dynamicData(mmsi, ownship, (DynamicDataReport) message);
            } else if (message instanceof BaseStationReport) {
                stationReport(mmsi, ownship, (BaseStationReport) message);
            } else if (message instanceof Error) {
                error(mmsi, ownship);
            }
        });
    }


    // ============================== 静态数据处理 ============================== //

    /***
     * 静态数据处理
     */
    private static void staticData(int mmsi, boolean ownship, StaticDataReport message) {
        if (message instanceof ShipAndVoyageData) {
            voyageRelated(mmsi, ownship, (ShipAndVoyageData) message);
        } else if (message instanceof ClassBCSStaticDataReport) {
            bClassStaticData(mmsi, ownship, (ClassBCSStaticDataReport) message);
        } else {
            log.warn("ais static data failed. mmsi: {}, message: {}", mmsi, message);
        }
    }

    /**
     * 船只静态航行数据
     */
    private static void voyageRelated(int mmsi, Boolean ownship, ShipAndVoyageData message) {
        AisStaticDataBO staticClass = new AisStaticDataBO();
        staticClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        staticClass.setImo(message.getImo().getIMO())
                .setCallcode(message.getCallsign())
                .setName(message.getShipName())
                .setDestination(message.getDestination())
                .setStype(message.getShipType())
                .setTrng(message.getToStern())
                .setRrng(message.getToStarboard())
                .setDraught(BigDecimal.valueOf(message.getDraught()));
        staticClass(staticClass);
    }

    /**
     * B类-静态数据
     */
    private static void bClassStaticData(int mmsi, Boolean ownship, ClassBCSStaticDataReport message) {
        AisStaticDataBO staticClass = new AisStaticDataBO();
        staticClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        staticClass.setCallcode(message.getCallsign())
                .setName(message.getShipName())
                .setStype(message.getShipType())
                .setTrng(message.getToStern())
                .setRrng(message.getToStarboard());
        staticClass(staticClass);
    }


    // ============================== 动态数据处理 ============================== //

    /**
     * 动态数据
     */
    private static void dynamicData(int mmsi, boolean ownship, DynamicDataReport message) {
        if (message instanceof PositionReport) {
            positionReport(mmsi, ownship, (PositionReport) message);
        } else if (message instanceof ExtendedClassBEquipmentPositionReport) {
            bClassExtended(mmsi, ownship, (ExtendedClassBEquipmentPositionReport) message);
        } else if (message instanceof LongRangeBroadcastMessage) {
            longRangeBroadcast(mmsi, ownship, (LongRangeBroadcastMessage) message);
        } else if (message instanceof StandardSARAircraftPositionReport) {
            sarAircraft(mmsi, ownship, (StandardSARAircraftPositionReport) message);
        } else if (message instanceof StandardClassBCSPositionReport) {
            bClassDynamicData(mmsi, ownship, (StandardClassBCSPositionReport) message);
        } else {
            log.warn("ais dynamic data failed. mmsi: {}, message: {}", mmsi, message);
        }
    }

    /**
     * A类-航行数据
     */
    private static void positionReport(int mmsi, Boolean ownship, PositionReport message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setNavstatus(message.getNavigationStatus())
                .setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()))
                .setHeading(BigDecimal.valueOf(message.getTrueHeading()))
                .setCourse(BigDecimal.valueOf(message.getCourseOverGround()))
                .setSog(BigDecimal.valueOf(message.getSpeedOverGround()))
                .setRturn(message.getRateOfTurn());
        dynamicClass(dynamicClass);
    }

    /**
     * B类-航行数据
     */
    private static void bClassExtended(Integer mmsi, boolean ownship, ExtendedClassBEquipmentPositionReport message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()))
                .setHeading(BigDecimal.valueOf(message.getTrueHeading()))
                .setCourse(BigDecimal.valueOf(message.getCourseOverGround()))
                .setSog(BigDecimal.valueOf(message.getSpeedOverGround()));
        dynamicClass(dynamicClass);
    }

    /**
     * 远程广播信息
     */
    private static void longRangeBroadcast(Integer mmsi, boolean ownship, LongRangeBroadcastMessage message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()))
                .setCourse(BigDecimal.valueOf(message.getCourseOverGround()))
                .setSog(BigDecimal.valueOf(message.getSpeedOverGround()));
        dynamicClass(dynamicClass);
    }

    /**
     * 飞机
     */
    private static void sarAircraft(Integer mmsi, boolean ownship, StandardSARAircraftPositionReport message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()))
                .setCourse(BigDecimal.valueOf(message.getCourseOverGround()))
                .setSog(BigDecimal.valueOf(message.getSpeedOverGround()));
        dynamicClass(dynamicClass);
    }

    /**
     * B类-航行数据
     */
    private static void bClassDynamicData(Integer mmsi, boolean ownship, StandardClassBCSPositionReport message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(message.getTransponderClass())
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()))
                .setHeading(BigDecimal.valueOf(message.getTrueHeading()))
                .setCourse(BigDecimal.valueOf(message.getCourseOverGround()))
                .setSog(BigDecimal.valueOf(message.getSpeedOverGround()));
        dynamicClass(dynamicClass);
    }


    // ============================== 基站数据处理 ============================== //

    /**
     * 基站数据处理
     */
    private static void stationReport(Integer mmsi, boolean ownship, BaseStationReport message) {
        AisDynamicDataBO dynamicClass = new AisDynamicDataBO();
        dynamicClass.setMmsi(mmsi).setSclass(TransponderClass.BS)
                .setOwnship(ownship).setCtime(System.currentTimeMillis());
        dynamicClass.setLon(BigDecimal.valueOf(message.getLongitude()))
                .setLat(BigDecimal.valueOf(message.getLatitude()));
        dynamicClass(dynamicClass);
    }


    // ============================== 无效数据处理 ============================== //

    /**
     * 无效数据
     */
    private static void error(Integer mmsi, boolean ownship) {
        AisStaticDataBO staticClass = new AisStaticDataBO();
        staticClass.setMmsi(mmsi).setSclass(null).setOwnship(ownship).setCtime(System.currentTimeMillis());
        staticClass(staticClass);
    }


    // ============================== 将数据写到本地文件（一个小时一个文件） ============================== //

    private static void staticClass(AisStaticDataBO staticClass) {
        if (staticClass == null) {
            return;
        }
        try {
            String aisPath = AIS_SOURCE_PATH + getPath() + "_static";
            FileUtil.appendWrite(aisPath, (JsonUtil.toString(staticClass) + "\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("write ais static data failed.", e);
        }
    }

    private static void dynamicClass(AisDynamicDataBO dynamicClass) {
        if (dynamicClass == null) {
            return;
        }
        try {
            String aisPath = AIS_SOURCE_PATH + getPath() + "_dynamic";
            FileUtil.appendWrite(aisPath, (JsonUtil.toString(dynamicClass) + "\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("write ais dynamic data failed.", e);
        }
    }

    /**
     * 判断是否是本船
     *
     * @param message AIS
     * @return 【true: 本船; false: 它船; null: 消息不正确】
     */
    private static Boolean ownship(AISMessage message) {
        NMEAMessage[] nmeaMessages = message.getNmeaMessages();
        if (nmeaMessages == null || nmeaMessages.length == 0) {
            return null;
        }
        return AIVDO.equals(nmeaMessages[0].getMessageType());
    }

    private static String getPath() {
        return DateUtil.toTimeStr(System.currentTimeMillis(), DateUtil.DateFormat.YYYYMMDDHH);
    }

    private final static String DEMO_NMEA_STRINGS = "!AIVDM,1,1,,A,18UG;P0012G?Uq4EdHa=c;7@051@,0*53\n" +
            "!AIVDM,2,1,1,,539L8BT29ked@90F220I8TE<h4pB22222222220o1p?4400Ht00000000000,0*49\n" +
            "!AIVDM,2,2,1,,00000000008,2*6C\n" +
            "!AIVDM,1,1,,A,15NIrB0001G?endE`CpIgQSN08K6,0*02\n" +
            "!AIVDM,1,1,,B,152Hn;?P00G@K34EWE0d>?wN28KB,0*12\n" +
            "!AIVDM,1,1,,B,138Ngv0OinG>DFnDekIF6lkN00Rk,0*2E\n" +
            "!AIVDM,1,1,,B,15N06LPP00G?Sf6Egkh0TwwL0HKO,0*2B\n" +
            "!AIVDM,1,1,,A,15N:Ie0P00G@6VpEa4n68?wL0HKf,0*2C\n" +
            "!AIVDM,1,1,,B,15MqdBP000G@qoLEi69PVGaN0D0=,0*3A\n" +
            "!AIVDM,1,1,,B,B5NJ;PP005l4onUIsc@03woUoP06,0*3A\n" +
            "!AIVDM,1,1,,B,15Mv4a0P00G?<pHEeU59nwwN08L3,0*7D\n" +
            "!AIVDM,1,1,,A,35Ml=50Oh@o>Lf2EVPJI>nqP017A,0*51\n" +
            "!AIVDM,1,1,,A,15Mw0J0P01G?aLVE`VfaM?wN00RV,0*3B\n" +
            "!AIVDM,1,1,,B,16:252002lo=Gn8E7k?=0bGN0@LH,0*04\n" +
            "!AIVDM,1,1,,A,19NWsbP000o@58pE`8pHhSGP00SE,0*0B\n" +
            "!AIVDM,1,1,,B,35AjiT5000G@4vhE`ok8a6sR0Dbb,0*06\n" +
            "!AIVDM,1,1,,B,15MwksP000G@6TDEa501Uc5P08Cq,0*3B\n" +
            "!AIVDM,1,1,,A,15N59@PP00G?iGhEW<9P0?wL0HLg,0*3E\n" +
            "!AIVDM,1,1,,B,15N:`e0000G@6IlEa5O`V93L0@Lt,0*22\n" +
            "!AIVDM,1,1,,B,15Ms0FPP00o?arNEdfdUw?wR08M3,0*09\n" +
            "!AIVDM,1,1,,B,13U8W:002;o>lC`EWMwaaWiR8D10,0*09\n" +
            "!AIVDM,1,1,,B,35MA9T0Oino<fFPE1=cG75iR0000,0*4D\n" +
            "!AIVDM,1,1,,B,4h3Ovk1udq`Dio>jPHEdjdW008MI,0*63\n" +
            "!AIVDM,1,1,,B,4h3Ovl1udq`DioCkldEpGh70051@,0*25\n" +
            "!AIVDM,1,1,,B,4h3OvkQudq`Djo?UhFEf=Ko00<18,0*43\n" +
            "!AIVDM,1,1,,B,4h3Ovl1udq`DjoCkllEpGh70051@,0*2E\n" +
            "!AIVDM,1,1,,B,35OqO05vh0G@8GREWEmVVwwT0000,0*3D\n" +
            "!AIVDM,1,1,,B,35Ml=5000=o>LeVEVPH96ns`0000,0*50\n" +
            "!AIVDM,1,1,,B,15>gpr0PAuG=AglDjcc68Ts200S2,0*4A\n" +
            "!AIVDM,1,1,,B,18UG;P000pG?UgdEdOeeec6t08DW,0*0A\n" +
            "!AIVDM,1,1,,B,85MwpKiKf0wLgSt5BlHF<3FMlaSRCjf1?Nq;4TAA7Mj:oOH5bs=8,0*7D\n" +
            "!AIVDM,1,1,,A,152Hn;?P00G@K3HEWDot<gw82HDi,0*5B\n" +
            "!AIVDM,1,1,,B,152SGj001so?U5fEg5j8?VU808Dm,0*19\n" +
            "!AIVDM,1,1,,B,15NIrB0001G?envE`Cp9gQG80D18,0*09\n" +
            "!AIVDM,1,1,,B,15MwpWhP1so?KpFEaiOL<Ow60HE>,0*14\n" +
            "!AIVDM,1,1,,B,16:252002uo=FHHE86H=8:G600S?,0*5F\n" +
            "!AIVDM,1,1,,A,138Ngv001uG>EINDeV;654k:0@EJ,0*69\n" +
            "!AIVDM,1,1,,B,15N:Ie0P00G@6W>Ea4ollOw600S0,0*53\n" +
            "!AIVDM,1,1,,A,15N06LPP00G?SdvEgki0Tww80@ET,0*02\n" +
            "!AIVDM,1,1,,B,13U8W:002@o>ipDEWH19d7k88@El,0*5A\n" +
            "!AIVDM,1,1,,B,18UG7V0019G?ithE`a;m;D;600SB,0*2B\n" +
            "!AIVDM,1,1,,B,15MA9T001no<fEpE0wno25i:0@F7,0*49\n" +
            "!AIVDM,1,1,,B,33TWed1001G?tg@EUg3cBV?80000,0*38\n" +
            "!AIVDM,1,1,,A,15MwksP000G@6T`Ea501Ms5:0D0w,0*77\n" +
            "!AIVDM,1,1,,A,15MiuGg000o?<b6EeVq8;aW:0HF=,0*50\n" +
            "!AIVDM,1,1,,B,19NWsbP000o@59BE`8qFJ3G<0HFK,0*79\n" +
            "!AIVDM,1,1,,B,15Ml=50P@Do>LR`EVNsHQFc>00RJ,0*77\n" +
            "!AIVDM,1,1,,B,15Mw0J0P02G?aLRE`Vf`mOw<08Fd,0*32\n" +
            "!AIVDM,1,1,,A,15Mv4a0P00G?<plEeU3anww<0HFi,0*56\n" +
            "!AIVDM,1,1,,A,15N:`e0000G@6InEa5OTDq160<11,0*71\n" +
            "!AIVDM,1,1,,B,15N59@PP00G?iGhEW<9P0?w:0<16,0*13\n" +
            "!AIVDM,1,1,,A,35MA9T001no<fF6E0wVG25k>0000,0*1A\n" +
            "!AIVDM,1,1,,A,Dh3Ovk0nIN>4,0*38\n" +
            "!AIVDM,1,1,,B,15ND4kP001G@6I@Ea5AM;I3>0<0w,0*04\n" +
            "!AIVDM,1,1,,B,Dh3Ovl0sqN>4,0*19\n" +
            "!AIVDM,1,1,,A,Dh3Ovl0mUN>4,0*20\n" +
            "!AIVDM,1,1,,B,Dh3Ovk0tMN>4,0*25\n" +
            "!AIVDM,1,1,,A,Dh3Ovl0mMN>4,0*38\n" +
            "!AIVDM,1,1,,A,13:112002?o@FRnDS<bdu:E:08GQ,0*77\n" +
            "!AIVDM,1,1,,A,4h3Ovk1udq`FWo>jPHEdjdW0051H,0*2C\n" +
            "!AIVDM,1,1,,A,15N6r>P000G<dG0Esaod<:U@08GM,0*53\n" +
            "!AIVDM,1,1,,A,4h3OvkQudq`F`o?UhFEf=Ko00D1;,0*33\n" +
            "!AIVDM,1,1,,B,15Ph;00Oi@o@V?PDmKanwUaB08Gs,0*02\n" +
            "!AIVDM,1,1,,A,15Mva0P00no?Ui>EdS;MobMB08Gt,0*19\n" +
            "!AIVDM,1,1,,B,15NGH8POi8G?ii4E`bPE74?p0U1H,0*58\n" +
            "!AIVDM,1,1,,A,15MwDf0P00G?<k4EeSU@Ugw@00Sm,0*1C\n" +
            "!AIVDM,1,1,,B,15MvlfP000G?lwrEd9aJIicD0D1;,0*2B\n" +
            "!AIVDM,1,1,,A,16:252002io=FE@E87S=3:IB0<09,0*47\n" +
            "!AIVDM,1,1,,B,15MwlV0P00G@6N8Ea5FujwwD08I0,0*7B\n" +
            "!AIVDM,1,1,,A,15NGdT?001G?eWRE`E9r8QoF2D11,0*10\n" +
            "!AIVDM,1,1,,A,15ND4kP000G@6I@Ea5AGhI3D0HI6,0*69\n" +
            "!AIVDM,1,1,,B,15M67FO000G@7EHEa28cvRsF251H,0*4B\n" +
            "!AIVDM,1,1,,B,15NH7?PP00G@>aTEWwd<<wwJ0@It,0*25\n" +
            "!AIVDM,1,1,,A,15MQqQ0P00G?iH>EW<<@0?wD08J4,0*01\n" +
            "!AIVDM,1,1,,B,15NHHAP000G@rn<Ei:<5c1eJ00Ss,0*2B\n" +
            "!AIVDM,1,1,,A,15?ECL001=G<wHPEON52>QeH08JK,0*47\n" +
            "!AIVDM,1,1,,B,13:112002?o@FNbDS=Ntu:EF00ST,0*3C\n" +
            "!AIVDM,1,1,,A,15>gpr001sG=AnHDjb>V3TwF08Jd,0*72\n" +
            "!AIVDM,1,1,,A,152SGj001to?TvlEg4`H?6UL08Jo,0*36";
}
