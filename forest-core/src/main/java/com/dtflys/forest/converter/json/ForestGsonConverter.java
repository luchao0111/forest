package com.dtflys.forest.converter.json;

import com.dtflys.forest.exceptions.ForestConvertException;
import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * 使用Gson实现的消息转换实现类
 *
 * @author Gongjun
 * @since 2016-06-04
 */
public class ForestGsonConverter implements ForestJsonConverter {

    @Override
    public <T> T convertToJavaObject(String source, Class<T> targetType) {
        try {
            if (Map.class.isAssignableFrom(targetType)) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(source).getAsJsonObject();
                return (T) toMap(jsonObject, false);
            }
            else if (List.class.isAssignableFrom(targetType)) {
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonArray = jsonParser.parse(source).getAsJsonArray();
                return (T) toList(jsonArray);
            }
            Gson gson = new Gson();
            return (T) gson.fromJson(source, targetType);
        } catch (Throwable th) {
            throw new ForestConvertException("json", th);
        }
    }

    @Override
    public <T> T convertToJavaObject(String source, Type targetType) {
        try {
            if (targetType instanceof ParameterizedType
                    || targetType.getClass().getName().startsWith("com.google.gson")) {
                Gson gson = new Gson();
                return gson.fromJson(source, targetType);
            }
            return convertToJavaObject(source, (Class<? extends T>) targetType);
        } catch (Exception ex) {
            throw new ForestConvertException("json", ex);
        }
    }


    private static Map<String, Object> toMap(JsonObject json, boolean singleLevel){
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Map.Entry<String, JsonElement>> entrySet = json.entrySet();
        for (Iterator<Map.Entry<String, JsonElement>> iter = entrySet.iterator(); iter.hasNext(); ){
            Map.Entry<String, JsonElement> entry = iter.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            if (singleLevel) {
                if(value instanceof JsonArray) {
                    map.put(key, toList((JsonArray) value));
                }
                else if (value instanceof JsonPrimitive) {
                    map.put(key, toObject((JsonPrimitive) value));
                }
                else {
                    map.put(key, value);
                }
                continue;
            }
            if(value instanceof JsonArray) {
                map.put(key, toList((JsonArray) value));
            }
            else if(value instanceof JsonObject) {
                map.put(key, toMap((JsonObject) value, singleLevel));
            }
            else if (value instanceof JsonPrimitive) {
                map.put(key, toObject((JsonPrimitive) value));
            }
            else {
                map.put(key, value);
            }
        }
        return map;
    }

    private static Object toObject(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isBoolean()) {
            return jsonPrimitive.getAsBoolean();
        }
        if (jsonPrimitive.isString()) {
            return jsonPrimitive.getAsString();
        }
        if (jsonPrimitive.isNumber()) {
            BigDecimal num = jsonPrimitive.getAsBigDecimal();
            int index = num.toString().indexOf('.');
            if (index == -1) {
                if (num.compareTo(new BigDecimal(Long.MAX_VALUE)) == 1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Long.MIN_VALUE)) == -1) {
                    return num;
                }
                if (num.compareTo(new BigDecimal(Integer.MAX_VALUE)) == 1
                        || num.compareTo(new BigDecimal(Integer.MIN_VALUE)) == -1) {
                    return jsonPrimitive.getAsLong();
                }
                return jsonPrimitive.getAsInt();
            }
            double dvalue = jsonPrimitive.getAsDouble();
            float fvalue = jsonPrimitive.getAsFloat();
            if (String.valueOf(dvalue).equals(fvalue)) {
                return fvalue;
            }
            return dvalue;
        }
        if (jsonPrimitive.isJsonArray()) {
            return toList(jsonPrimitive.getAsJsonArray());
        }
        if (jsonPrimitive.isJsonObject()) {
            return toMap(jsonPrimitive.getAsJsonObject(), false);
        }
        return null;
    }

    private static List<Object> toList(JsonArray json){
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < json.size(); i++){
            Object value = json.get(i);
            if (value instanceof JsonArray){
                list.add(toList((JsonArray) value));
            }
            else if (value instanceof JsonObject) {
                list.add(toMap((JsonObject) value, false));
            }
            else if (value instanceof JsonPrimitive) {
                list.add(toObject((JsonPrimitive) value));
            }
            else{
                list.add(value);
            }
        }
        return list;
    }

    @Override
    public String encodeToString(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    @Override
    public Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return convertToJavaObject(obj.toString(), LinkedHashMap.class);
        }
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(obj);
        return toMap(jsonElement.getAsJsonObject(), true);
    }

    public String convertToJson(Object obj, Type type) {
        Gson gson = new Gson();
        return gson.toJson(obj, type);
    }

}
