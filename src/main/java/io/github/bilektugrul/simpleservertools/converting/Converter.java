package io.github.bilektugrul.simpleservertools.converting;

public interface Converter {

    boolean canConvert();

    FinalState convert();

    String getName();

    String getAuthor();

}
