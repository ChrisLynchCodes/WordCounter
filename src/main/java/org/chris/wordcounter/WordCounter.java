package org.chris.wordcounter;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public interface WordCounter {
    void addWords(Collection<String> words);

    int getCount(String word);

    ConcurrentMap<String, AtomicInteger> getWordCountMap();
}
