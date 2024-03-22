package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

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
    private static final ObjectMapper EXCLUDE_NULL_MAPPER = makeBaseXssSerializerObjectMapper();

    static {
        // The original two ObjectMappers allow comments.
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // The servlet version of the XssSerializerObjectMapper sets inclusion
        // level differently.
        EXCLUDE_NULL_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        EXCLUDE_NULL_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JsonUtil() {
        throw new UnsupportedOperationException("JsonUtil should never be instantiated");
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * 获取json字符串中所有的Field
     *
     * @param json json字符串
     * @return Field集合
     */
    public static Set<String> findAllField(String json) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptySet();
        }
        return findAllField(toJsonNode(json));
    }

    /**
     * 获取jsonNode中所有的Field
     *
     * @param node jsonNode
     * @return Field集合
     */
    public static Set<String> findAllField(JsonNode node) {
        if (node == null) {
            return Collections.emptySet();
        }
        HashSet<String> fieldSet = new HashSet<>();
        findAllField(node, fieldSet);
        return fieldSet;
    }

    private static void findAllField(JsonNode node, HashSet<String> fieldSet) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                fieldSet.add(entry.getKey());
            }
        } else if (node.isArray()) {
            node.forEach(item -> findAllField(item, fieldSet));
        }
    }

    /**
     * 从JsonNode取出字符串
     *
     * @param node json
     * @param key  K
     * @return V
     */
    public static String pathText(JsonNode node, String key) {
        return pathText(node, key, "");
    }

    /**
     * 从JsonNode取出字符串
     *
     * @param node         json
     * @param key          K
     * @param defaultValue 当json为空或者key不存在时返回该默认值
     * @return V || DV
     */
    public static String pathText(JsonNode node, String key, String defaultValue) {
        if (node == null || StringUtils.isBlank(key)) {
            return defaultValue;
        }
        return node.findPath(key).asText(defaultValue);
    }

    /**
     * 将对象转换成json字符串
     *
     * @param obj object
     * @return json字符串
     */
    public static String formatJson(final Object obj) {
        try {
            return toJsonStringRaw(obj);
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return null;
    }

    /**
     * 将对象转换成紧凑的json字符串
     *
     * @param obj object
     * @return json字符串
     */
    public static String toString(final Object obj) {
        return toString(obj, false);
    }

    /**
     * 将对象转换成紧凑的json字符串
     *
     * @param obj object
     * @return json字符串
     */
    public static String toString(final Object obj, final boolean excludeNull) {
        StringWriter sw = new StringWriter();
        try {
            getObjectMapper(excludeNull).writer().writeValue(sw, obj);
        } catch (Exception e) {
            log.error("Failed convert {} to JSON", obj, e);
        }
        return sw.toString();
    }

    /**
     * 将json字符串转换成JsonNode对象
     *
     * @param json json字符串
     * @return JsonNode
     */
    public static JsonNode toJsonNode(final String json) {
        return toJsonNode(json, true);
    }

    /**
     * 将json字符串转换成JsonNode对象
     *
     * @param json json字符串
     * @return JsonNode
     */
    public static JsonNode toJsonNode(final String json, final boolean excludeNull) {
        try {
            if (StringUtils.isBlank(json)) {
                return null;
            }
            return getObjectMapper(excludeNull).readTree(json);
        } catch (Exception e) {
            log.error("Failed convert jsonNode, json:{}", json, e);
            return null;
        }
    }

    /**
     * 将json字符串转换成对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return object
     */
    public static <T> T toObject(final String json, final Class<T> clazz) {
        return toObject(json, clazz, true);
    }

    /**
     * 将json字符串转换成对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return object
     */
    public static <T> T toObject(final String json, final Class<T> clazz, final boolean excludeNull) {
        try {
            return toObjectRaw(json, clazz, excludeNull);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, clazz, e);
        }
        return null;
    }

    /**
     * 将json字符串转换成对象
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return object
     */
    public static <T> T toObject(final String json, final Class<T> clazz, final Class<?>... parameterTypes) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(clazz, parameterTypes));
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: {}", json, clazz, e);
        }
        return null;
    }

    /**
     * 将source转换到clazz对象中
     *
     * @param source 源数据
     * @param clazz  目标对象类型
     * @param <T>    泛型
     * @return object
     */
    public static <T> T convertObject(Object source, final Class<T> clazz) {
        if (source == null) {
            return null;
        }
        return toObject(toString(source), clazz);
    }

    /**
     * 将json字符串转换成对象List
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return object
     */
    public static <T> List<T> toObjectList(final String json, final Class<T> clazz) {
        return toObjectList(json, clazz, true);
    }

    /**
     * 将json字符串转换成对象List
     *
     * @param json  json字符串
     * @param clazz 对象类型
     * @param <T>   泛型
     * @return object
     */
    public static <T> List<T> toObjectList(final String json, final Class<T> clazz, final boolean excludeNull) {
        try {
            return toObjectListRaw(json, clazz, excludeNull);
        } catch (Exception e) {
            log.error("Failed convert JSON: {} to: List<{}>", json, clazz, e);
        }
        return null;
    }

    /**
     * 将source转换到clazz List中
     *
     * @param sourceList 源数据
     * @param clazz      目标对象类型
     * @param <T>        泛型
     * @return object
     */
    public static <T> List<T> convertObjectList(Collection<?> sourceList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (Object source : sourceList) {
            result.add(convertObject(source, clazz));
        }
        return result;
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

    private static ObjectMapper makeBaseXssSerializerObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(new CharacterEscapes() {
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
        });
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper;
    }

    private static ObjectMapper getObjectMapper(final boolean excludeNull) {
        return excludeNull ? EXCLUDE_NULL_MAPPER : MAPPER;
    }

    private static String toJsonStringRaw(final Object obj) throws IOException {
        return toJsonStringRaw(obj, false);
    }

    private static String toJsonStringRaw(final Object obj, final boolean excludeNull) throws IOException {
        Writer sw = new StringWriter();
        getObjectMapper(excludeNull).writerWithDefaultPrettyPrinter().writeValue(sw, obj);
        return sw.toString();
    }

    private static <T> T toObjectRaw(final String json, final Class<T> valueType) throws IOException {
        return toObjectRaw(json, valueType, false);
    }

    private static <T> T toObjectRaw(final String json, final Class<T> valueType, final boolean excludeNull) throws IOException {
        return getObjectMapper(excludeNull).readValue(json, valueType);
    }

    private static <T> List<T> toObjectListRaw(final String json, final Class<T> valueType, final boolean excludeNull) throws IOException {
        ObjectMapper mapper = getObjectMapper(excludeNull);
        return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, valueType));
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