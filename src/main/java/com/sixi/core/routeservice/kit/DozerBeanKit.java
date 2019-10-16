package com.sixi.core.routeservice.kit;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.dozer.DozerBeanMapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * @author MiaoWoo
 */
public class DozerBeanKit {
    private static DozerBeanMapper map = new DozerBeanMapper();

    private DozerBeanKit() {
    }

    public static <T> T map(Object source, Class<T> cls) {
        return source == null ? null : map.map(source, cls);
    }

    @SuppressWarnings("unchecked")
    public static <T> T map(Object source, Object target) {
        map.map(source, target);
        return (T) target;
    }

    public static <T> List<T> map(List<?> source, Class<T> cls) {
        List<T> listTarget = new ArrayList<>();
        if (source != null) {
            for (Object object : source) {
                listTarget.add(map(object, cls));
            }
        }
        return listTarget;
    }

    public static Map<String, String> toStringMap(Object obj) {
        return toMapHandler(String::valueOf, obj);
    }

    public static Map<String, String> toJsonMap(Object obj) {
        return toMapHandler(JSON::toJSONString, obj);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> toMap(Object obj) {
        if (obj == null) {
            return Collections.emptyMap();
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            Map<String, T> map = new HashMap<>(fields.length);
            for (Field field : fields) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                map.put(field.getName(), (T) field.get(obj));
            }
            return map;
        }
    }

    public static Map<String, String> toMapHandler(Function<Object, String> func, Object obj) {
        Map<String, Object> temp = toMap(obj);
        Map<String, String> stringMap = new HashMap<>(temp.size());
        temp.forEach((s, o) -> stringMap.put(s, Optional.ofNullable(o).map(func).orElse(null)));
        return stringMap;
    }
}
