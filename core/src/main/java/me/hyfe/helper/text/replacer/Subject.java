package me.hyfe.helper.text.replacer;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class Subject {
    private final Map<String, Object> placeholders = Maps.newHashMap();
    private final Map<String, Supplier<Object>> suppliedPlaceholders = Maps.newHashMap();

    public static String to(String string, UnaryOperator<Subject> subject) {
        return subject.apply(new Subject()).applyTo(string);
    }

    public static Subject of(Map<String, String> placeholders) {
        Subject subject = new Subject();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            subject.set(entry.getKey(), entry.getValue());
        }
        return subject;
    }

    public Subject set(String placeholder, Object value) {
        this.placeholders.put("%" + placeholder + "%", value);
        return this;
    }

    public Subject set(String placeholder, Supplier<Object> supplier) {
        this.suppliedPlaceholders.put("%" + placeholder + "%", supplier);
        return this;
    }

    public String applyTo(String string) {
        String result = string;
        for (Map.Entry<String, Object> entry : this.placeholders.entrySet()) {
            result = result.replace(entry.getKey(), Objects.toString(entry.getValue()));
        }
        for (Map.Entry<String, Supplier<Object>> entry : this.suppliedPlaceholders.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), Objects.toString(entry.getValue().get()));
            }
        }
        return result;
    }
}