package me.hyfe.helper.text.replacer;

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface Replacer extends UnaryOperator<Subject> {
}