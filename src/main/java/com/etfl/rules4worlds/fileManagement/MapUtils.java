package com.etfl.rules4worlds.fileManagement;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class MapUtils {
    @SuppressWarnings("unchecked")
    public static boolean mapsUnequal(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == null || !map1.equals(map2)) return true;

        Iterator<Map.Entry<String, Object>> it1 = map1.entrySet().iterator();
        Iterator<Map.Entry<String, Object>> it2 = map2.entrySet().iterator();

        while (it1.hasNext() && it2.hasNext()) {
            Map.Entry<String, Object> entry1 = it1.next();
            Map.Entry<String, Object> entry2 = it2.next();

            if (!entry1.getKey().equals(entry2.getKey())) return true;

            if (entry1.getValue() instanceof Map && mapsUnequal((Map<String, Object>) entry1.getValue(), (Map<String, Object>) entry2.getValue())) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, Object> deepCopy(Map<String, Object> map) {
        Map<String, Object> copy = new LinkedHashMap<>(map);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                copy.put(entry.getKey(), deepCopy((Map<String, Object>) value));
            } else {
                copy.put(entry.getKey(), value);
            }
        }
        return copy;
    }
}
