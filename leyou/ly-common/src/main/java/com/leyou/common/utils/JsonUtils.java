package com.leyou.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj.getClass() == String.class) {
            return (String) obj;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("json序列化出错：" + obj, e);
            return null;
        }
    }

    public static <T> T toBean(String json, Class<T> tClass) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            logger.error("json解析出错：" + json, e);
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User{
        private Integer id;
        private String name;
    }

    public static void main(String[] args) {
        /*List<User> users = Arrays.asList(new User(1, "小飞飞"), new User(2, "小鸭鸭"));
        String userStr = toString(users);
        System.out.println(userStr);

        System.out.println("map==========================================");
        String mapString = "{\"id\":1,\"name\":\"小飞飞mmmmmmmmm\"}";
        Map<String, Object> map = toMap(mapString, String.class, Object.class);
        System.out.println(map);*/

        System.out.println("================>变态版的map的方法");
        String mapListString = "{\"key1\": [{\"id\":1,\"name\":\"小飞飞\"},{\"id\":1,\"name\":\"小鸭鸭\"}], \"key2\": [{\"id\":1111,\"name\":\"樱木花道\"},{\"id\":2222,\"name\":\"流川枫\"}]}";
        System.out.println("字符串："+mapListString);
        Map<String, List<User>> stringListMap = nativeRead(mapListString, new TypeReference<Map<String, List<User>>>() {
        });
        System.out.println("转成了对象"+stringListMap);
    }
}










