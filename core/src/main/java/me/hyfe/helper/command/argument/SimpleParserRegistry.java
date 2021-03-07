package me.hyfe.helper.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SimpleParserRegistry implements ArgumentParserRegistry {
    private final Map<TypeToken<?>, List<ArgumentParser<?>>> parsers = new ConcurrentHashMap<>();

    @Override
    public <T> ArgumentParser<T> find(TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return null;
        }
        return (ArgumentParser<T>) parsers.get(0);
    }

    @Override
    public <T> Collection<ArgumentParser<T>> findAll(TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return ImmutableList.of();
        }
        return (Collection) Collections.unmodifiableList(parsers);
    }

    @Override
    public <T> void register(TypeToken<T> type, ArgumentParser<T> parser) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(parser, "parser");
        List<ArgumentParser<?>> list = this.parsers.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>());
        if (!list.contains(parser)) {
            list.add(parser);
        }
    }
}