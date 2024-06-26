package org.globsframework.sql.utils;

public class StringPrettyWriter implements PrettyWriter {
    private StringBuilder builder = new StringBuilder();

    public StringPrettyWriter append(String s) {
        builder.append(s);
        return this;
    }

    public void appendIf(String s, boolean shouldAppend) {
        if (shouldAppend) {
            append(s);
        }
    }

    public String toString() {
        return builder.toString();
    }

    public PrettyWriter newLine() {
        builder.append("\n");
        return this;
    }

    public PrettyWriter removeLast() {
        builder.delete(builder.length() - 1, builder.length());
        return this;
    }

    public PrettyWriter removeLast(int len) {
        builder.delete(builder.length() - len, builder.length());
        return this;
    }
}
