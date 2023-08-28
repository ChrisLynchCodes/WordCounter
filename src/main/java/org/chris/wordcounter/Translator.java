package org.chris.wordcounter;

import org.chris.exceptions.TranslatorException;

public interface Translator {
    String translate(String word) throws TranslatorException;

}
