package org.chris.wordcounter.impl;

import java.util.Collection;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCounter {

    private ConcurrentMap<String, AtomicInteger> wordCountMap;

    public int addWords(Collection<String> words) {
        return 0;
    }
    public int getCount(String word){
        return 0;
    }
    private boolean validWord(String word){
        return false;
    }


}
