package me.twister915.corelite.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public final class CLUtil {
    public static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return false;
            for (File file1 : files) {
                if (!delete(file1)) return false;
            }
        }
        return file.delete();
    }

    public static void copy(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
        if (source.isDirectory()) {
            for (String s : source.list()) {
                copy(new File(source, s), new File(dest, s));
            }
        }
    }

    public static <T> boolean arrayContains(T[] ts, T t) {
        for (T t1 : ts) {
            if (t1 == t || t1.equals(t)) return true;
        }
        return false;
    }

    public static String formatSeconds(Integer seconds) {
        StringBuilder builder = new StringBuilder();
        int ofNext = seconds;
        for (TimeUnit unit : TimeUnit.values()) {
            int ofUnit;
            if (unit.perNext != -1) {
                ofUnit = ofNext % unit.perNext;
                ofNext = Math.floorDiv(ofNext, unit.perNext);
            }
            else {
                ofUnit = ofNext;
                ofNext = 0;
            }
            builder.insert(0, unit.shortName).insert(0, String.format("%02d", ofUnit));
            if (ofNext == 0) break;
        }
        return builder.toString();
    }

    private static enum TimeUnit {
        SECONDS(60, 's'),
        MINUTES(60, 'm'),
        HOURS(24, 'h'),
        DAYS('d');

        private final int perNext;
        private final char shortName;

        TimeUnit(int i, char h) {
            perNext = i;
            shortName = h;
        }


        TimeUnit(char d) {
            perNext = -1;
            shortName = d;
        }
    }
}
