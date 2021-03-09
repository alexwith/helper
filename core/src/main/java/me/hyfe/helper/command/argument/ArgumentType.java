package me.hyfe.helper.command.argument;

public interface ArgumentType<T> {

    T parse(String arg);
}
