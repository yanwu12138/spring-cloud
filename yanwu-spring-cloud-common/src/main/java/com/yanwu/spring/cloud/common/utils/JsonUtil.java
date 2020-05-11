package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public final class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final ObjectMapper XSS_SERIALIZER_OBJECT_MAPPER = makeBaseXssSerializerObjectMapper();
    private static final ObjectMapper SERVLET_XSS_SERIALIZER_OBJECT_MAPPER = makeBaseXssSerializerObjectMapper();

    static {
        // The original two ObjectMappers allow comments.
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        XSS_SERIALIZER_OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        XSS_SERIALIZER_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // The servlet version of the XssSerializerObjectMapper sets inclusion
        // level differently.
        SERVLET_XSS_SERIALIZER_OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SERVLET_XSS_SERIALIZER_OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper makeBaseXssSerializerObjectMapper() {
        CharacterEscapes characterEscapes = new CharacterEscapes() {
            private static final long serialVersionUID = -7677406402762106256L;
            private final int[] escapeCodes = CharacterEscapes.standardAsciiEscapesForJSON();

            {
                // To help protect against XSS attacks, escape characters
                // significant to HTML
                escapeCodes['<'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['>'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['&'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['"'] = CharacterEscapes.ESCAPE_STANDARD;
                escapeCodes['\''] = CharacterEscapes.ESCAPE_STANDARD;
            }

            @Override
            public int[] getEscapeCodesForAscii() {
                return escapeCodes;
            }

            @Override
            public SerializableString getEscapeSequence(final int ch) {
                return null;
            }
        };
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(characterEscapes);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    public static ObjectMapper getServletXssSerializerObjectMapper() {
        return SERVLET_XSS_SERIALIZER_OBJECT_MAPPER;
    }

    private static ObjectMapper getObjectMapper(final boolean decodeForXss) {
        return decodeForXss ? XSS_SERIALIZER_OBJECT_MAPPER : MAPPER;
    }

    public static String toJsonStringRaw(final Object obj) throws IOException {
        return toJsonStringRaw(obj, false);
    }

    public static String toJsonStringRaw(final Object obj, final boolean encodeForXss) throws IOException {
        Writer sw = new StringWriter();
        getObjectMapper(encodeForXss).writerWithDefaultPrettyPrinter().writeValue(sw, obj);
        return sw.toString();
    }

    public static <T> T toObjectRaw(final String json, final Class<T> valueType) throws IOException {
        return MAPPER.readValue(json, valueType);
    }

    public static <T> T toObjectRaw(final String json, final Class<T> valueType, final boolean decodeForXss) throws IOException {
        return getObjectMapper(decodeForXss).readValue(json, valueType);
    }

    public static String toJsonString(final Object obj) {
        try {
            return toJsonStringRaw(obj);
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return null;
    }

    public static String toCompactJsonString(final Object obj) {
        return toCompactJsonString(obj, false);
    }

    public static String toCompactJsonString(final Object obj, final boolean decodeForXss) {
        StringWriter sw = new StringWriter();
        try {
            getObjectMapper(decodeForXss).writer().writeValue(sw, obj);
            return sw.toString();
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return null;
    }

    public static <T> T toObject(final String json, final Class<T> valueType) {
        return toObject(json, valueType, true);
    }

    public static <T> T toObject(final String json, final Class<T> valueType, final boolean decodeForXss) {
        try {
            return toObjectRaw(json, valueType, decodeForXss);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, valueType, e);
        }
        return null;
    }

    public static <T> T toObject(final String json, final Class<T> valueType, final Class<?>... parameterTypes) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(valueType, parameterTypes));
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, valueType, e);
        }
        return null;
    }

    public static <T> List<T> toObjectList(final String json, final Class<T> valueType) {
        return toObjectList(json, valueType, true);
    }

    public static <T> List<T> toObjectList(final String json, final Class<T> valueType, final boolean decodeForXss) {
        try {
            return toObjectListRaw(json, valueType, decodeForXss);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: List<{}>", json, valueType, e);
        }
        return null;
    }

    public static <T> List<T> toObjectListRaw(final String json, final Class<T> valueType, final boolean decodeForXss) throws IOException {
        ObjectMapper mapper = getObjectMapper(decodeForXss);
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }

    private JsonUtil() {
        throw new AssertionError("JsonUtil should never be instantiated");
    }

    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        StringBuilder result = new StringBuilder();
        int length = json.length();
        int number = 0;
        char key;
        // 遍历输入字符串。
        for (int i = 0; i < length; i++) {
            // 1、获取当前字符。
            key = json.charAt(i);
            // 2、如果当前字符是前方括号、前花括号做如下处理：
            if ((key == '[') || (key == '{')) {
                // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }
                // （2）打印：当前字符。
                result.append(key);
                // （3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');
                // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));
                // （5）进行下一次循环。
                continue;
            }
            // 3、如果当前字符是后方括号、后花括号做如下处理：
            if ((key == ']') || (key == '}')) {
                // （1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');
                // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));
                // （3）打印：当前字符。
                result.append(key);
                // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }
                // （5）继续下一次循环。
                continue;
            }
            // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            // 5、打印：当前字符。
            result.append(key);
        }
        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < number; i++) {
            result.append("   ");
        }
        return result.toString();
    }

}