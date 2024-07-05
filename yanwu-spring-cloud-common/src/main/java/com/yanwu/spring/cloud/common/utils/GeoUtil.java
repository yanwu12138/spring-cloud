package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.enums.PositionEnum;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.impl.PointImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Baofeng Xu
 * @date 2021/8/15 20:55.
 * <p>
 * description: Gps/经纬度相关工具
 */
@SuppressWarnings("unused")
public class GeoUtil {
    private static final String RANGE_SPLIT = ";";
    private static final String POINT_SPLIT = ",";
    public static final BigDecimal MIN_LNG = BigDecimal.valueOf(-180);
    public static final BigDecimal MAX_LNG = BigDecimal.valueOf(180);
    public static final BigDecimal MIN_LAT = BigDecimal.valueOf(-90);
    public static final BigDecimal MAX_LAT = BigDecimal.valueOf(90);
    private static final JtsSpatialContext JTS_GEO_SPATIAL = JtsSpatialContext.GEO;
    private static final BigDecimal KILOMETER_PER_SEA = BigDecimal.valueOf(1.852);
    private static final String DEGREES = "°";
    private static final String MINUTES = "′";
    private static final String SECONDS = "′′";

    /*** 地球长半径: a=6378137 米 ***/
    public static final double EARTH_END_RADIUS = 6378137;
    /*** 地球短半径: b=6356752.3142 ***/
    public static final double EARTH_MINOR_RADIUS = 6356752.3142;
    /*** 地球扁率: f=1/298.2572236 ***/
    public static final double FLATNESS = 1 / 298.2572236;

    private GeoUtil() {
        throw new UnsupportedOperationException("GeoUtil should never be instantiated");
    }

    /**
     * 根据经纬度计算两点之间的距离
     * <b>（单位：米）</b>
     */
    public static Long getDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        return getDistance(geoPoint(lng1, lat1), geoPoint(lng2, lat2));
    }

    /**
     * 根据经纬度计算两点之间的距离
     * <b>（单位：米）</b>
     */
    public static Long getDistance(Point point1, Point point2) {
        double distance = JTS_GEO_SPATIAL.calcDistance(point1, point2) * DistanceUtils.DEG_TO_KM;
        return BigDecimal.valueOf(distance * 1000).longValue();
    }

    /**
     * 校验经纬度是否合法
     */
    public static boolean checkLocation(Point point) {
        return checkLocation(point.getLon(), point.getLat());
    }

    /**
     * 校验经纬度是否合法
     */
    public static boolean checkLocation(Double lng, Double lat) {
        return checkLocation(BigDecimal.valueOf(lng), BigDecimal.valueOf(lat));
    }

    /**
     * 校验经纬度是否合法
     */
    public static boolean checkLocation(BigDecimal lng, BigDecimal lat) {
        if (lng == null || lng.compareTo(MIN_LNG) < 0 || lng.compareTo(MAX_LNG) > 0) {
            return false;
        }
        return lat != null && lat.compareTo(MIN_LAT) >= 0 && lat.compareTo(MAX_LAT) <= 0;
    }

    /**
     * 将字符串经纬度转换成空间对象
     * 如：将 112.0200,24.8150;111.8260,24.7290;111.2700,24.3110;111.1990,24.2340;111.7780,25.4350 转换成成一个多边形控件对象
     */
    public static Shape toPolygon(String range) {
        ShapeFactory.PolygonBuilder builder = JTS_GEO_SPATIAL.getShapeFactory().polygon();
        String[] points = range.split(RANGE_SPLIT);
        for (String point : points) {
            String[] split = point.split(POINT_SPLIT);
            builder.pointLatLon(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
        }
        return builder.build();
    }

    /**
     * 将字符串经纬度转换成空间对象
     * 如：将 [[112.0200,24.8150], [111.8260,24.7290], [111.2700,24.3110], [111.1990,24.2340], [111.7780,25.4350]] 转换成成一个多边形控件对象
     */
    public static Shape toPolygon(Point[] points) {
        ShapeFactory.PolygonBuilder builder = JTS_GEO_SPATIAL.getShapeFactory().polygon();
        for (Point point : points) {
            builder.pointLatLon(point.getLat(), point.getLon());
        }
        return builder.build();
    }

    /**
     * 将字符串经纬度转换成空间对象
     * 如：将 [[112.0200,24.8150], [111.8260,24.7290], [111.2700,24.3110], [111.1990,24.2340], [111.7780,25.4350]] 转换成成一个多边形控件对象
     */
    public static Shape toPolygon(Double[][] points) {
        ShapeFactory.PolygonBuilder builder = JTS_GEO_SPATIAL.getShapeFactory().polygon();
        for (Double[] point : points) {
            builder.pointLatLon(point[1], point[0]);
        }
        return builder.build();
    }

    /**
     * 根据中心点与半径画圆
     */
    public static Shape toCircle(Double lng, Double lat, Long radius) {
        return toCircle(geoPoint(lng, lat), radius);
    }

    /**
     * 根据中心点与半径画圆
     */
    public static Shape toCircle(Point point, Long radius) {
        return JTS_GEO_SPATIAL.getShapeFactory().circle(point, radius);
    }

    /**
     * 判断点是否在多边形面内
     *
     * @return [true: 在; false: 不在]
     */
    public static boolean relate(Shape shape, double lon, double lat) {
        switch (shape.relate(geoPoint(lon, lat))) {
            case WITHIN:
            case CONTAINS:
            case INTERSECTS:
                return true;
            default:
                return false;
        }
    }

    /**
     * 将海里转换成千米，每海里等于：1.852KM
     * KM = SEA * 1.852
     *
     * @param distance 距离（海里）
     * @return 千米
     */
    public static Double seaToKm(BigDecimal distance) {
        return distance.multiply(KILOMETER_PER_SEA).doubleValue();
    }

    /**
     * 将千米转换成海里，每海里等于：1.852KM
     * SEA = KM / 1.852
     *
     * @param distance 千米
     * @return 距离（海里）
     */
    public static Double kmToSea(Double distance) {
        return BigDecimal.valueOf(distance).divide(KILOMETER_PER_SEA, RoundingMode.FLOOR).doubleValue();
    }

    private static Point geoPoint(double lon, double lat) {
        return new PointImpl(lon, lat, JTS_GEO_SPATIAL);
    }

    /***
     * 将十进制格式经纬度转换成度分秒
     * @param point 十进制格式经纬度
     * @return 度分秒经纬度
     */
    public static String convertCoordinate(Point point) {
        if (point == null || !checkLocation(point)) {
            return null;
        }
        return convertCoordinate(point.getLon(), point.getLat());
    }

    /***
     * 将十进制格式经纬度转换成度分秒
     * @param lng 十进制格式经度
     * @param lat 十进制格式纬度
     * @return 度分秒经纬度
     */
    public static String convertCoordinate(Double lng, Double lat) {
        if (!checkLocation(lng, lat)) {
            return null;
        }
        return convertCoordinate(BigDecimal.valueOf(lng), BigDecimal.valueOf(lat));
    }

    /***
     * 将十进制格式经纬度转换成度分秒
     * @param lng 十进制格式经度
     * @param lat 十进制格式纬度
     * @return 度分秒经纬度
     */
    public static String convertCoordinate(BigDecimal lng, BigDecimal lat) {
        if (!checkLocation(lng, lat)) {
            return null;
        }
        // ===== 计算经度
        String lngStr = buildPosition(lng);
        lngStr += PositionEnum.newInstance(lng, Boolean.TRUE).getCode();
        // ===== 计算纬度
        String latStr = buildPosition(lat);
        latStr += PositionEnum.newInstance(lat, Boolean.FALSE).getCode();
        return String.join(POINT_SPLIT, lngStr, latStr);
    }

    /***
     * 将十进制格式经纬度转换成度分秒
     * @param position 十进制格式经纬度
     * @return 度分秒经纬度
     */
    private static String buildPosition(BigDecimal position) {
        position = position.abs();
        // ===== 度
        String degrees = position.intValue() + DEGREES;
        // ===== 分
        position = position.subtract(BigDecimal.valueOf(position.intValue()));
        position = position.multiply(BigDecimal.valueOf(60));
        String minutes = position.intValue() + MINUTES;
        // ===== 秒
        position = position.subtract(BigDecimal.valueOf(position.intValue()));
        position = position.multiply(BigDecimal.valueOf(60));
        String seconds = position.intValue() + SECONDS;
        return degrees + minutes + seconds;
    }

    /***
     * 将度分秒格式经纬度转换成十进制
     * @param coordinate 度分秒格式经纬度
     * @return 十进制经纬度
     */
    public static Point convertCoordinate(String coordinate) {
        if (StringUtils.isBlank(coordinate) || !coordinate.contains(POINT_SPLIT)) {
            return null;
        }
        String[] split = coordinate.split(POINT_SPLIT);
        if (split.length != 2) {
            return null;
        }
        String lng = split[0], lat = split[1];
        if (StringUtils.isBlank(lng) || StringUtils.isBlank(lat)) {
            return null;
        }
        // ===== 将度分秒格式转换成十进制
        BigDecimal lngDecimal = buildPosition(lng), latDecimal = buildPosition(lat);
        if (lngDecimal == null || latDecimal == null) {
            return null;
        }
        lngDecimal = PositionEnum.calcSymbols(lngDecimal, lng);
        latDecimal = PositionEnum.calcSymbols(latDecimal, lat);
        return geoPoint(lngDecimal.doubleValue(), latDecimal.doubleValue());
    }

    /***
     * 将度分秒格式经纬度转换成十进制
     * @param position 度分秒格式经纬度
     * @return 十进制经纬度
     */
    private static BigDecimal buildPosition(String position) {
        if (!position.contains(DEGREES) || !position.contains(MINUTES) || !position.contains(SECONDS)) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = BigDecimal.ZERO;
        // ===== 度
        String degrees = position.substring(0, position.indexOf(DEGREES));
        result = result.add(BigDecimal.valueOf(Double.parseDouble(degrees)));
        // ===== 分
        String minutes = position.substring(position.indexOf(DEGREES) + 1, position.indexOf(MINUTES));
        result = result.add(BigDecimal.valueOf(Double.parseDouble(minutes)).divide(BigDecimal.valueOf(60), 6, RoundingMode.UP));
        // ===== 秒
        String seconds = position.substring(position.indexOf(MINUTES) + 1, position.indexOf(SECONDS));
        result = result.add(BigDecimal.valueOf(Double.parseDouble(seconds)).divide(BigDecimal.valueOf(3600), 6, RoundingMode.UP));
        return result;
    }

    /**
     * 已知一点经纬度，方位角，距离，求另一点经纬度
     * 通过三角函数求终点坐标-球面坐标系
     *
     * @param point    初始坐标
     * @param angle    角度(单位: 度)
     * @param distance 距离(单位: 米)
     * @return 目标位置经纬度
     */
    public static Point positionOffset(Point point, double angle, double distance) {
        double lon = point.getLon();
        double lat = point.getLat();

        double alpha1 = rad(angle);
        double sinAlpha1 = Math.sin(alpha1);
        double cosAlpha1 = Math.cos(alpha1);

        double tanU1 = (1 - FLATNESS) * Math.tan(rad(lat));
        double cosU1 = 1 / Math.sqrt((1 + tanU1 * tanU1));
        double sinU1 = tanU1 * cosU1;
        double sigma1 = Math.atan2(tanU1, cosAlpha1);
        double sinAlpha = cosU1 * sinAlpha1;
        double cosSqAlpha = 1 - sinAlpha * sinAlpha;
        double uSq = cosSqAlpha * (EARTH_END_RADIUS * EARTH_END_RADIUS - EARTH_MINOR_RADIUS * EARTH_MINOR_RADIUS) / (EARTH_MINOR_RADIUS * EARTH_MINOR_RADIUS);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));

        double cos2SigmaM = 0;
        double sinSigma = 0;
        double cosSigma = 0;
        double sigma = distance / (EARTH_MINOR_RADIUS * A), sigmaP = 2 * Math.PI;
        while (Math.abs(sigma - sigmaP) > 1e-12) {
            cos2SigmaM = Math.cos(2 * sigma1 + sigma);
            sinSigma = Math.sin(sigma);
            cosSigma = Math.cos(sigma);
            double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)
                    - B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            sigmaP = sigma;
            sigma = distance / (EARTH_MINOR_RADIUS * A) + deltaSigma;
        }

        double tmp = sinU1 * sinSigma - cosU1 * cosSigma * cosAlpha1;
        double lat2 = Math.atan2(sinU1 * cosSigma + cosU1 * sinSigma * cosAlpha1,
                (1 - FLATNESS) * Math.sqrt(sinAlpha * sinAlpha + tmp * tmp));
        double lambda = Math.atan2(sinSigma * sinAlpha1, cosU1 * cosSigma - sinU1 * sinSigma * cosAlpha1);
        double C = FLATNESS / 16 * cosSqAlpha * (4 + FLATNESS * (4 - 3 * cosSqAlpha));
        double L = lambda - (1 - C) * FLATNESS * sinAlpha
                * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));

        return geoPoint(lon + deg(L), deg(lat2));
    }

    /***
     * 度换成弧度
     * @param deg 度
     * @return 弧度
     */
    private static double rad(double deg) {
        return deg * Math.PI / 180;
    }


    /***
     * 弧度换成度
     * @param rad 弧度
     * @return 度
     */
    private static double deg(double rad) {
        return rad * 180 / Math.PI;
    }

    public static void main(String[] args) {
        Point source = geoPoint(120.72931D, -29.5481D);
        Point target = positionOffset(source, 90, 2 * 1.852 * 1000);
        System.out.println(source);
        System.out.println(target);
        System.out.println(getDistance(source, target));
    }

}
