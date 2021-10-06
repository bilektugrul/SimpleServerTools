package io.github.bilektugrul.simpleservertools.converting;

public abstract class Converter {

    public abstract boolean canConvert();

    public abstract FinalState convert();

    public abstract String getName();

    public abstract String getAuthor();

}