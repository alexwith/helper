package me.hyfe.helper.utils;

public class Translate {

    public static <T, R> R apply(T from, Class<R> to) {
        return to.cast(from);
    }
}
