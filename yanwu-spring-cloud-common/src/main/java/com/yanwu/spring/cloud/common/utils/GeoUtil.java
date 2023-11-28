package com.yanwu.spring.cloud.common.utils;

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
    private static final BigDecimal MIN_LNG = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_LNG = BigDecimal.valueOf(180);
    private static final BigDecimal MIN_LAT = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LAT = BigDecimal.valueOf(90);
    private static final JtsSpatialContext JTS_GEO_SPATIAL = JtsSpatialContext.GEO;
    private static final BigDecimal KILOMETER_PER_SEA = BigDecimal.valueOf(1.852);
    private static final BigDecimal CONSTANT = BigDecimal.valueOf(600_000);

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
     * 将度分秒格式经纬度转换成十进制
     * @param coordinate 度分秒格式经纬度
     * @return 十进制经纬度
     */
    public static BigDecimal convertCoordinate(Integer coordinate) {
        if (coordinate == null) {
            return BigDecimal.valueOf(0L);
        }
        BigDecimal source = BigDecimal.valueOf(coordinate);
        BigDecimal divide = source.divide(CONSTANT, 0, RoundingMode.DOWN);
        BigDecimal subtract = source.subtract(divide.multiply(CONSTANT));
        return divide.add(subtract.divide(CONSTANT, 7, RoundingMode.DOWN));
    }

    /***
     * 将十进制格式经纬度转换成度分秒
     * @param coordinate 十进制格式经纬度
     * @return 度分秒经纬度
     */
    public static Integer convertCoordinate(BigDecimal coordinate) {
        if (coordinate == null) {
            return 0;
        }
        return coordinate.multiply(CONSTANT).intValue();
    }

}
