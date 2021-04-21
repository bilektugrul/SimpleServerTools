package io.github.bilektugrul.simpleservertools.features.placeholders;

public class CustomPlaceholder {

    private final String name;
    private final String value;

    public CustomPlaceholder(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
