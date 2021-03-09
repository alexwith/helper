package me.hyfe.helper.oldcommand.argument;

import com.google.common.reflect.TypeToken;

import java.util.Collection;

public interface ArgumentParserRegistry {

    <T> ArgumentParser<T> find(TypeToken<T> type);

    default <T> ArgumentParser<T> find(Class<T> clazz) {
        return find(TypeToken.of(clazz));
    }

    <T> Collection<ArgumentParser<T>> findAll(TypeToken<T> type);

    default <T> Collection<ArgumentParser<T>> findAll(Class<T> clazz) {
        return findAll(TypeToken.of(clazz));
    }

    <T> void register(TypeToken<T> type, ArgumentParser<T> parser);

    default <T> void register(Class<T> clazz, ArgumentParser<T> parser) {
        register(TypeToken.of(clazz), parser);
    }
}