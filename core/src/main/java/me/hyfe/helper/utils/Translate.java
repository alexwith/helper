package me.hyfe.helper.utils;

public class Translate {

    @SuppressWarnings("unchecked")
    public static <T, R> R apply(T from, Class<R> to) {
        return (R) from.getClass().cast(to);
    }
}
