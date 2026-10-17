package ChimeraMonsters.util;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.function.Function;

public interface DataUtil {
    WeakHashMap<Object, HashMap<String, Object>> customData = new WeakHashMap<>();

    Object getHandler();

    default boolean hasDataMap() {
        return hasDataMap(getHandler());
    }

    default HashMap<String, Object> getDataMap() {
        return getDataMap(getHandler());
    }

    default void removeDataMap() {
        removeDataMap(getHandler());
    }

    default boolean hasData(String key) {
        return hasData(getHandler(), key);
    }

    default <T> T getData(String key) {
        return getData(getHandler(), key);
    }

    default <T> T getOrMakeData(String key, T val) {
        return getOrMakeData(getHandler(), key, val);
    }

    default <T> T getOrMakeData(String key, Function<? super String, ? extends T> func) {
        return getOrMakeData(getHandler(), key, func);
    }

    default <T> void setData(String key, T val) {
        setData(getHandler(), key, val);
    }

    default void removeData(String key) {
        removeData(getHandler(), key);
    }

    static boolean hasDataMap(Object o) {
        return customData.containsKey(o);
    }

    static HashMap<String, Object> getDataMap(Object o) {
        return customData.computeIfAbsent(o, obj -> new HashMap<>());
    }

    static void removeDataMap(Object o) {
        customData.remove(o);
    }

    static boolean hasData(Object o, String key) {
        return hasDataMap(o) && customData.get(o).containsKey(key);
    }

    static <T> T getData(Object o, String key) {
        return (T) getDataMap(o).get(key);
    }

    static <T> T getOrMakeData(Object o, String key, T val) {
        HashMap<String, Object> map = getDataMap(o);
        if (map.containsKey(key)) {
            return (T) map.get(key);
        }
        map.put(key, val);
        return val;
    }

    static <T> T getOrMakeData(Object o, String key, Function<? super String, ? extends T> func) {
        return (T) getDataMap(o).computeIfAbsent(key, func);
    }

    static <T> void setData(Object o, String key, T val) {
        getDataMap(o).put(key, val);
    }

    static void removeData(Object o, String key) {
        if (customData.containsKey(o)) {
            customData.get(o).remove(key);
        }
    }
}
