package org.chris.wordcounter.impl;

import org.chris.exceptions.TranslatorException;
import org.chris.wordcounter.Translator;
import org.chris.wordcounter.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordCounterImpl implements WordCounter {

    private final ConcurrentMap<String, AtomicInteger> wordCountMap;
    private final Translator translator;
    private final Pattern pattern;
    private static final Logger logger = LoggerFactory.getLogger(WordCounterImpl.class);

    public WordCounterImpl(Translator translator) {
        this.translator = translator;
        wordCountMap = new ConcurrentHashMap<>();
        pattern = Pattern.compile("[a-zA-Z]+");
    }

    @Override
    public void addWords(Collection<String> words) {
        if (null != words && !words.isEmpty()) {
            words.stream()
                    .filter(this::validWord)
                    .forEach(this::processWord);
        }
    }


    @Override
    public int getCount(String word) {
        if (null != word && validWord(word)) {
            return wordCountMap.getOrDefault(word, new AtomicInteger(0)).get();
        }
        return 0;
    }

    private void processWord(String word) {
        try {
            String englishWord = translator.translate(word);
            updateWordCount(englishWord);
        } catch (TranslatorException e) {
            logger.error("Translation error for word {}, Exception: {}", word, e.getMessage());

        }
    }

    private boolean validWord(String word) {
        Matcher matcher = pattern.matcher(word);
        if (!matcher.matches()) {
            logger.error("Invalid word {} dropped", word);
            return false;
        }
        return true;
    }


    private void updateWordCount(String word) {
        wordCountMap.compute(word, (key, value) -> value == null ? new AtomicInteger(1) : new AtomicInteger(value.incrementAndGet()));
        logger.info("New Count for word {} - Updated to {}", word, wordCountMap.get(word).get());
    }

    @Override
    public ConcurrentMap<String, AtomicInteger> getWordCountMap() {
        return this.wordCountMap;
    }


}
