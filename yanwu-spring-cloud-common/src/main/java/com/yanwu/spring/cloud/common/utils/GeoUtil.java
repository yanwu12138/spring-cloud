package com.yanwu.spring.cloud.common.utils;

import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
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
     * 判断点是否在多边形年内
     *
     * @return [true: 在; false: 不在]
     */
    public static boolean relate(Shape shape, double lon, double lat) {
        SpatialRelation relate = shape.relate(geoPoint(lon, lat));
        switch (relate) {
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

    public static void main(String[] args) {
        double lon = 122.6889877319336, lat = 35.44651794433594;
        String beam7 = "120.436,38.682;120.079,38.561;119.445,38.205;119.153,37.965;118.855,37.686;118.502,37.240;118.324,36.952;118.105,36.517;117.889,35.849;117.875,35.800;117.770,35.087;117.778,34.380;117.887,33.679;118.095,32.984;118.407,32.293;118.802,31.658;118.835,31.608;119.400,30.928;119.632,30.701;120.161,30.250;120.378,30.090;121.079,29.670;121.296,29.572;121.751,29.380;122.401,29.199;123.034,29.116;123.653,29.126;124.259,29.232;124.853,29.443;124.963,29.501;125.438,29.770;125.894,30.135;126.011,30.243;126.474,30.782;126.570,30.925;126.871,31.439;127.101,32.010;127.137,32.104;127.293,32.779;127.345,33.463;127.294,34.158;127.135,34.862;126.855,35.578;126.734,35.796;126.433,36.307;125.932,36.936;125.829,37.050;125.154,37.655;124.940,37.810;124.388,38.166;123.631,38.524;123.392,38.601;122.885,38.760;122.153,38.881;121.438,38.890;120.745,38.786;120.436,38.682";
        System.out.println("beam7: " + relate(toPolygon(beam7), lon, lat));

        String beam8 = "118.429,32.973;117.911,32.765;117.325,32.385;117.262,32.329;116.783,31.819;116.679,31.674;116.341,31.018;116.316,30.937;116.161,30.364;116.104,29.713;116.148,29.064;116.284,28.420;116.511,27.779;116.835,27.140;117.180,26.634;117.273,26.506;117.853,25.873;117.950,25.784;118.652,25.243;118.656,25.241;119.317,24.874;119.957,24.628;120.064,24.602;120.579,24.479;121.185,24.421;121.779,24.450;122.309,24.559;122.360,24.570;122.931,24.786;123.490,25.122;123.518,25.144;124.034,25.627;124.133,25.745;124.524,26.353;124.553,26.419;124.772,26.969;124.904,27.592;124.939,28.222;124.881,28.860;124.730,29.505;124.473,30.158;124.091,30.821;124.069,30.852;123.548,31.494;123.323,31.716;122.755,32.181;122.598,32.293;121.888,32.692;121.374,32.892;121.188,32.962;120.501,33.124;119.826,33.186;119.168,33.150;118.528,33.012;118.429,32.973";
        System.out.println("beam8: " + relate(toPolygon(beam8), lon, lat));

        String beam9 = "118.257,27.103;117.755,26.933;117.182,26.622;117.037,26.512;116.637,26.160;116.421,25.910;116.135,25.475;116.042,25.307;115.819,24.704;115.733,24.232;115.711,24.102;115.699,23.502;115.778,22.903;115.944,22.307;116.118,21.907;116.203,21.713;116.561,21.121;116.897,20.700;117.048,20.529;117.580,20.043;117.716,19.939;118.224,19.602;118.736,19.347;118.844,19.297;119.447,19.094;120.037,18.981;120.616,18.948;121.184,18.994;121.744,19.124;122.161,19.292;122.295,19.349;122.835,19.686;123.033,19.851;123.364,20.183;123.554,20.418;123.873,20.946;123.897,20.990;124.114,21.568;124.229,22.151;124.254,22.739;124.192,23.332;124.040,23.930;123.788,24.535;123.501,25.014;123.416,25.146;122.889,25.765;122.816,25.835;122.150,26.374;122.118,26.395;121.493,26.753;120.846,27.012;120.724,27.044;120.206,27.176;119.575,27.250;118.955,27.236;118.348,27.132;118.257,27.103";
        System.out.println("beam9: " + relate(toPolygon(beam9), lon, lat));
    }

}
