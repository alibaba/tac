package com.alibaba.tac.sdk.tangram4tac.render;

import com.alibaba.tac.sdk.tangram4tac.Cell;
import com.alibaba.tac.sdk.tangram4tac.FieldExcluder;
import com.alibaba.tac.sdk.tangram4tac.FieldNameMapper;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by longerian on 2017/11/5.
 */
public class DefaultRender implements IRender<Cell, Map<String, Object>> {

    private static Map<Class, Field[]> classFiledCache = new HashMap<Class, Field[]>();

    private static Map<Class<?>, List<Class<?>>> typesCache = new HashMap<Class<?>, List<Class<?>>>();

    private static Map<Field, FieldExcluder> fieldExcluderCache = new HashMap<Field, FieldExcluder>();

    private static Map<Field, String> fieldNameMapCache = new HashMap<Field, String>();

    public DefaultRender() {

    }

    @Override
    public Map<String, Object> renderTo(Cell cell) {
        return internalRenderForClass(cell);
    }

    private Map<String, Object> internalRenderForClass(Object object) {
        Map<String, Object> outputData = new HashMap<String, Object>();
        if (object != null) {
            Class selfClass = object.getClass();
            List<Class<?>> clazzes = lookupCellTypes(selfClass);
            for (int i = 0, size = clazzes.size(); i < size; i++) {
                Class<?> clazz = clazzes.get(i);
                internalRenderFields(object, clazz, outputData);
            }
        }
        return outputData;
    }

    private void internalRenderFields(Object object, Class clazz, Map<String, Object> output) {
        Field[] selfFields = classFiledCache.get(clazz);
        Field oneField;
        String name;
        Object value;
        FieldNameMapper fieldNameMapper;
        FieldExcluder fieldExcluder;
        if (selfFields == null) {
            selfFields = clazz.getDeclaredFields();
            for (int i = 0 , length = selfFields.length; i < length; i++) {
                oneField = selfFields[i];
                oneField.setAccessible(true);
                fieldExcluder = oneField.getAnnotation(FieldExcluder.class);
                if (fieldExcluder != null) {
                    fieldExcluderCache.put(oneField, fieldExcluder);
                }
                fieldNameMapper = oneField.getAnnotation(FieldNameMapper.class);
                if (fieldNameMapper != null) {
                    name = fieldNameMapper.key();
                    if (name != null && name.length() > 0) {
                        fieldNameMapCache.put(oneField, name);
                    }
                }
            }
            classFiledCache.put(clazz, selfFields);
        }
        for (int i = 0 , length = selfFields.length; i < length; i++) {
            oneField = selfFields[i];
            fieldExcluder = fieldExcluderCache.get(oneField);
            if (fieldExcluder != null) {
                continue;
            }
            if (fieldNameMapCache.containsKey(oneField)) {
                name = fieldNameMapCache.get(oneField);
            } else {
                name = oneField.getName();
            }
            try {
                value = oneField.get(object);
                internalRenderField(name, value, output);
            } catch (IllegalAccessException e) {
            }
        }
    }

    private void internalRenderField(String name, Object object, Map<String, Object> output) {
        if (object != null) {
            Object value = getFieldValue(object);
            if (value != null) {
                output.put(name, value);
            }
        }
    }

    private Object getFieldValue(Object object) {
        Class oneFieldClazz = object.getClass();
        if (isBasicType(object)) {
            return getNonDefaultValueFromBasicType(object);
        } else if (Collection.class.isAssignableFrom(oneFieldClazz)) {
            Collection lists = (Collection) object;
            List<Object> outputLists = new ArrayList<Object>();
            Iterator<Object> itr = lists.iterator();
            while (itr.hasNext()) {
                Object item = itr.next();
                Object value = getFieldValue(item);
                if (value != null) {
                    outputLists.add(value);
                } else {
                    outputLists.add(item);
                }
            }
            if (outputLists.isEmpty()) {
                return null;
            } else {
                return outputLists;
            }
        } else if (Map.class.isAssignableFrom(oneFieldClazz)) {
            Map maps = (Map) object;
            Map<String, Object> outputMaps = new HashMap<String, Object>();
            Iterator<Object> itr = maps.keySet().iterator();
            while (itr.hasNext()) {
                Object key = itr.next();
                Object item = maps.get(key);
                Object value = getFieldValue(item);
                if (value != null) {
                    outputMaps.put(String.valueOf(key), value);
                }
            }
            if (outputMaps.isEmpty()) {
                return null;
            } else {
                return outputMaps;
            }
        } else if (oneFieldClazz.isArray()) {
            int length = Array.getLength(object);
            Collection lists = new ArrayList();
            for (int a = 0; a < length; a++) {
                lists.add(Array.get(object, a));
            }
            List<Object> outputLists = new ArrayList<Object>();
            Iterator<Object> itr = lists.iterator();
            while (itr.hasNext()) {
                Object item = itr.next();
                Object value = getFieldValue(item);
                if (value != null) {
                    outputLists.add(value);
                } else {
                    outputLists.add(item);
                }
            }
            if (outputLists.isEmpty()) {
                return null;
            } else {
                return outputLists;
            }
        } else {
            return internalRenderForClass(object);
        }
    }

    private List<Class<?>> lookupCellTypes(Class<?> selfClass) {
        List<Class<?>> types = typesCache.get(selfClass);
        if (types == null) {
            types = new ArrayList<Class<?>>();
            Class<?> clazz = selfClass;
            while (clazz != null && !clazz.equals(Object.class)) {
                types.add(clazz);
                clazz = clazz.getSuperclass();
            }
            typesCache.put(selfClass, types);
        }
        return types;
    }

    private boolean isBasicType(Object object) {
        return object instanceof Integer
                || object instanceof Float
                || object instanceof Double
                || object instanceof Short
                || object instanceof Long
                || object instanceof String
                || object instanceof Boolean;
    }

    private Object getNonDefaultValueFromBasicType(Object object) {
        if (object instanceof Integer) {
            if (((Integer) object).intValue() == 0) {
                return null;
            }
        }
        if (object instanceof Float) {
            if (((Float) object).floatValue() == 0.0f) {
                return null;
            }
        }
        if (object instanceof Double) {
            if (((Double) object).doubleValue() == 0.0) {
                return null;
            }
        }
        if (object instanceof Short) {
            if (((Short) object).shortValue() == 0) {
                return null;
            }
        }
        if (object instanceof Long) {
            if (((Long) object).longValue() == 0) {
                return null;
            }
        }
        if (object instanceof Boolean) {
            if (((Boolean) object).booleanValue() == false) {
                return null;
            }
        }
        if (object instanceof String) {
            if (((String) object).length() == 0) {
                return null;
            }
        }
        return object;
    }

}
