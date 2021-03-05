package me.hyfe.helper.utils;

import me.hyfe.helper.internal.LoaderUtils;

public final class Log {

    public static void info(String msg) {
        LoaderUtils.getPlugin().getLogger().info(msg);
    }

    public static void warn(String msg) {
        LoaderUtils.getPlugin().getLogger().warning(msg);
    }

    public static void severe(String msg) {
        LoaderUtils.getPlugin().getLogger().severe(msg);
    }
}