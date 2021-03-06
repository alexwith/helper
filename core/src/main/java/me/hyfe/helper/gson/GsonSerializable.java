package me.hyfe.helper.gson;

import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface GsonSerializable {

    static <T extends GsonSerializable> T deserialize(Class<T> clazz, JsonElement element) {
        Method deserializeMethod = getDeserializeMethod(clazz);
        if (deserializeMethod == null) {
            throw new IllegalStateException("Class does not have a deserialize method accessible.");
        }

        try {
            //noinspection unchecked
            return (T) deserializeMethod.invoke(null, element);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static GsonSerializable deserializeRaw(Class<?> clazz, JsonElement element) {
        Class<? extends GsonSerializable> typeCastedClass = clazz.asSubclass(GsonSerializable.class);
        return deserialize(typeCastedClass, element);
    }

    static Method getDeserializeMethod(Class<?> clazz) {
        if (!GsonSerializable.class.isAssignableFrom(clazz)) {
            return null;
        }
        Method deserializeMethod;
        try {
            //noinspection JavaReflectionMemberAccess
            deserializeMethod = clazz.getDeclaredMethod("deserialize", JsonElement.class);
            deserializeMethod.setAccessible(true);
        } catch (Exception e) {
            return null;
        }
        if (!Modifier.isStatic(deserializeMethod.getModifiers())) {
            return null;
        }
        return deserializeMethod;
    }

    JsonElement serialize();
}