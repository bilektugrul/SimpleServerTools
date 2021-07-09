package io.github.bilektugrul.simpleservertools.features.placeholders;

public record CustomPlaceholder(String name, String value) {

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
