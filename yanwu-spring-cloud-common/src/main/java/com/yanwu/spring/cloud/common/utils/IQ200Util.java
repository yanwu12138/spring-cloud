package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author XuBaofeng.
 * @date 2023/4/19 15:26.
 * <p>
 * description:
 */
@Slf4j
public class IQ200Util {

    private IQ200Util() {
        throw new UnsupportedOperationException("IQ200Util should never be instantiated");
    }

    /**
     * 读取IQ200波束列表
     *
     * @param mapPath 地图文件绝对路径
     */
    public static List<IQ200Antenna> readIq200Antenna(String mapPath) {
        try {
            String beamMapJson = new String(FileUtil.read(mapPath));
            IQ200Beam iq200Beam = JsonUtil.toObject(beamMapJson, IQ200Beam.class);
            if (iq200Beam == null || iq200Beam.getConstellation() == null || CollectionUtils.isEmpty(iq200Beam.getConstellation().getSatellites())) {
                return Collections.emptyList();
            }
            ArrayList<IQ200Antenna> iq200Antennas = new ArrayList<>();
            iq200Beam.getConstellation().getSatellites().forEach(satellite -> iq200Antennas.addAll(IQ200Antenna.getInstances(satellite)));
            return iq200Antennas;
        } catch (Exception e) {
            log.error("read iq200 antenna sql failed. file: {}", mapPath, e);
            return Collections.emptyList();
        }
    }

    /**
     * 将IQ200波束列表转换成SQL(insert)语句
     *
     * @param antennas IQ200波束列表
     */
    public static String antennasToSql(List<IQ200Antenna> antennas) {
        if (CollectionUtils.isEmpty(antennas)) {
            return null;
        }
        AtomicLong id = new AtomicLong();
        StringBuilder builder = new StringBuilder("DELETE FROM t_iq_antenna_beam;\r\n");
        antennas.forEach(antenna -> builder.append(antenna.toInsertSql(id.incrementAndGet())));
        return builder.toString();
    }


    public static void main(String[] args) throws Exception {
        List<IQ200Antenna> antennas = readIq200Antenna("/Users/xubaofeng/yanwu/document/beam_map.json");
        log.info("read iq200 antenna, antennas: {}", antennas);
        String insertSql = antennasToSql(antennas);
        log.info("read iq200 antenna, inserts: {}", insertSql);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(insertSql);
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

    @Data
    @Accessors(chain = true)
    private static class IQ200Antenna implements Serializable {
        private Integer beamId;
        private BigDecimal frequency;
        private BigDecimal symbolRate;
        private String polarisation;
        private BigDecimal satelliteLon;
        private BigDecimal rollOff;
        private String mainSite;

        private static List<IQ200Antenna> getInstances(Satellite satellite) {
            if (satellite == null || CollectionUtils.isEmpty(satellite.getCarrier())) {
                return Collections.emptyList();
            }
            ArrayList<IQ200Antenna> result = new ArrayList<>();
            for (Carrier carrier : satellite.getCarrier()) {
                IQ200Antenna instance = new IQ200Antenna();
                instance.setBeamId(carrier.getBeamId());
                instance.setFrequency(carrier.getCenterFreq());
                instance.setSymbolRate(carrier.getSymbolRate());
                instance.setPolarisation(carrier.getPolarization());
                instance.setSatelliteLon(satellite.getLongitude());
                instance.setRollOff(satellite.getLatitudeWander());
                instance.setMainSite(satellite.getSatelliteId());
                result.add(instance);
            }
            return result;
        }

        private String toInsertSql(long id) {
            return "INSERT IGNORE INTO t_iq_antenna_beam VALUES (" + id + ", NOW(), NOW(), " + beamId + ", " + frequency + ", " + symbolRate + ", '" + polarisation + "', " + satelliteLon + ", " + rollOff + ", '" + mainSite + "');\r\n";
        }
    }


    @Data
    @Accessors(chain = true)
    private static class IQ200Beam implements Serializable {
        @JsonProperty("CONSTELLATION")
        private Constellation constellation;
        @JsonProperty("NMS")
        private Nms nms;
    }

    @Data
    @Accessors(chain = true)
    private static class Constellation implements Serializable {
        @JsonProperty("SATELLITES")
        private List<Satellite> satellites;
        @JsonProperty("constellation_name")
        private String constellationName;
    }

    @Data
    @Accessors(chain = true)
    private static class Nms implements Serializable {
        @JsonProperty("element_id")
        private String elementId;
        @JsonProperty("element_parent_id")
        private String elementParentId;
        @JsonProperty("type")
        private String type;
    }

    @Data
    @Accessors(chain = true)
    private static class Satellite implements Serializable {
        @JsonProperty("BEAM")
        private List<Beam> beam;
        @JsonProperty("CARRIER")
        private List<Carrier> carrier;
        @JsonProperty("latitude_wander")
        private BigDecimal latitudeWander;
        @JsonProperty("longitude")
        private BigDecimal longitude;
        @JsonProperty("preference")
        private BigDecimal preference;
        @JsonProperty("satellite_id")
        private String satelliteId;
        @JsonProperty("skew_polarization")
        private BigDecimal skewPolarization;
    }

    @Data
    @Accessors(chain = true)
    private static class Beam implements Serializable {
        @JsonProperty("beam_id")
        private Integer beamId;
        @JsonProperty("CONTOUR")
        private List<Contour> contour;
    }

    @Data
    @Accessors(chain = true)
    private static class Carrier implements Serializable {
        @JsonProperty("beam_id")
        private Integer beamId;
        @JsonProperty("carrier_type")
        private String carrierType;
        @JsonProperty("center_freq")
        private BigDecimal centerFreq;
        @JsonProperty("polarization")
        private String polarization;
        @JsonProperty("search_priority")
        private BigDecimal searchPriority;
        @JsonProperty("symbol_rate")
        private BigDecimal symbolRate;
    }

    @Data
    @Accessors(chain = true)
    private static class Contour implements Serializable {
        @JsonProperty("type")
        private Integer type;
        @JsonProperty("points")
        private List<List<BigDecimal>> points;
    }

}
