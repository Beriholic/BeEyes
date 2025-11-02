package cv.beriholic.beeyes.utils;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    static {
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_MISSING_VALUES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private JsonUtil() {
    }

    private static <T> T parseObject(String text, JavaType type) {
        try {
            return objectMapper.readValue(text, type);
        } catch (Exception ex) {
            log.error("parseObject fail:{} {}", ex, text);
            return null;
        }
    }

    public static <T> T parseObject(String content, Class<T> c) {
        if (StringUtils.isBlank(content)) {
            return null;
        } else {
            try {
                return objectMapper.readValue(content, c);
            } catch (IOException e) {
                log.error("parseObject fail:{}", content, e);
                return null;
            }
        }
    }

    public static boolean isValid(String content, Class c) {
        try {
            Object o = objectMapper.readValue(content, c);
            return o != null;
        } catch (Exception var3) {
            return false;
        }
    }

    public static boolean isListValid(String content, Class type) {
        try {
            List o = objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, type));
            return o != null;
        } catch (Exception var3) {
            return false;
        }
    }

    public static <T> T parseObject(String content, TypeReference valueTypeRef) {
        try {
            return (T) objectMapper.readValue(content, valueTypeRef);
        } catch (IOException e) {
            log.error("parseObject failed, content:{}", content, e);
            return null;
        }
    }

    public static <T> List<T> parseList(String text, Class<T> type) {
        return (List<T>) (StringUtils.isBlank(text) ? Lists.newArrayListWithCapacity(0) : (List) parseObject(text, objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, type)));
    }

    public static <T> List<T> parseList(URL src, Class<T> dataType) throws IOException, JsonParseException, JsonMappingException {
        return objectMapper.readValue(src, objectMapper.getTypeFactory().constructCollectionType(List.class, dataType));
    }

    public static <T> T parseObject(URL src, Class<T> c) {
        try {
            return objectMapper.readValue(src, c);
        } catch (IOException e) {
            log.error("parseObject fail:{}", src.getFile(), e);
            return null;
        }
    }

    public static JsonNode readTree(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            log.error("readTree fail:{}", text, e);
            return null;
        }
    }

    public static String toJSONString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            log.error("toJSONString fail:{} {}", object.toString(), object.getClass(), ex);
            return null;
        }
    }
}
