package me.hyfe.helper.command.argument;

public class SimpleArgument implements Argument {
    protected final int index;
    protected final String value;

    public SimpleArgument(int index, String value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int index() {
        return this.index;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return this.value != null;
    }
}