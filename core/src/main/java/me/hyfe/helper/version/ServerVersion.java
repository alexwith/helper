package me.hyfe.helper.version;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public enum ServerVersion {

    Unknown(Integer.MAX_VALUE),
    MC1_7_R4(174),
    MC1_8_R1(181),
    MC1_8_R2(182),
    MC1_8_R3(183),
    MC1_9_R1(191),
    MC1_9_R2(192),
    MC1_10_R1(1101),
    MC1_11_R1(1111),
    MC1_12_R1(1121),
    MC1_13_R1(1131),
    MC1_13_R2(1132),
    MC1_14_R1(1141),
    MC1_15_R1(1151),
    MC1_16_R1(1161),
    MC1_16_R2(1162),
    MC1_16_R3(1163),
    MC_1_16_R4(1164);

    private static ServerVersion serverVersion;
    private final int versionId;

    ServerVersion(int versionId) {
        this.versionId = versionId;
    }

    public static ServerVersion getVersion() {
        if (serverVersion != null) {
            return serverVersion;
        }
        String versionOfPackage = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        try {
            serverVersion = ServerVersion.valueOf(versionOfPackage.replace("v", "MC"));
        } catch (IllegalArgumentException ex) {
            serverVersion = ServerVersion.Unknown;
        }
        if (serverVersion != ServerVersion.Unknown) {
            Bukkit.getLogger().log(Level.INFO, String.format("Successfully connected to version %s'!", serverVersion.name()));
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "Was not able to find the server version, this might result in errors.");
        }
        return serverVersion;
    }

    public static boolean isCurrentOver(ServerVersion version) {
        return getVersion().getVersionId() > version.getVersionId();
    }

    public int getVersionId() {
        return versionId;
    }

    public boolean isThis() {
        return this.versionId == getVersion().versionId;
    }
}