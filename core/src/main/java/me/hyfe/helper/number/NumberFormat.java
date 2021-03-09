package me.hyfe.helper.number;

import org.apache.commons.lang.math.NumberUtils;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

// Inspired by StackOverFlow users assylias & Dónal
public class NumberFormat {
    private final static NavigableMap<Double, String> suffixes = new TreeMap<Double, String>() {{
        this.put(Math.pow(10, 3), "k");
        this.put(Math.pow(10, 6), "M");
        this.put(Math.pow(10, 9), "B");
        this.put(Math.pow(10, 12), "T");
        this.put(Math.pow(10, 15), "q");
        this.put(Math.pow(10, 18), "Q");
        this.put(Math.pow(10, 21), "s");
        this.put(Math.pow(10, 24), "S");
    }};
    private final static DecimalFormat format = new DecimalFormat("##.00");

    public static String format(double value) {
        if (value < 1000) {
            return Double.toString(value);
        }
        Map.Entry<Double, String> entry = suffixes.floorEntry(value);
        double divideBy = entry.getKey();
        String suffix = entry.getValue();

        double truncated = value / (divideBy / 10);
        return format.format(truncated / 10) + suffix;
    }

    public static double parse(String string) {
        boolean isNegative = string.charAt(0) == '-';
        int length = string.length();

        String number = isNegative ? string.substring(1, length - 1) : string.substring(0, length - 1);
        String suffix = Character.toString(string.charAt(length - 1));
        Number absoluteNumber = NumberUtils.createNumber(number);

        double exponent = 0;
        for (Map.Entry<Double, String> entry : suffixes.entrySet()) {
            if (entry.getValue().equals(suffix)) {
                exponent = Math.log10(entry.getKey());
                break;
            }
        }
        double factor = Math.pow(10, exponent);
        factor *= isNegative ? -1 : 1;
        return Math.round(absoluteNumber.floatValue() * factor);
    }
}
