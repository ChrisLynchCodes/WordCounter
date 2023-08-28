package wordcounter;


import org.chris.exceptions.TranslatorException;
import org.chris.wordcounter.Translator;
import org.chris.wordcounter.WordCounter;
import org.chris.wordcounter.impl.WordCounterImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class WordCounterTest {

    static WordCounter wordCounter;
    @Mock
    static Translator translator;
    private static final Logger logger = LoggerFactory.getLogger(WordCounterImpl.class);

    @BeforeEach
    void setUp() {
        translator = mock(Translator.class);
        wordCounter = new WordCounterImpl(translator);
    }

    // translate interface
    @Test
    void givenACollectionOfValidNonEnglishWords_whenAddWordsCalled_thenReturnEnglishTranslation() throws TranslatorException {
        when(translator.translate("hola")).thenReturn("hello");
        when(translator.translate("mundo")).thenReturn("world");

        List<String> validNonEnglishWords = Arrays.asList("hola", "mundo");

        assertTrue(validNonEnglishWords.stream()
                .allMatch(word -> {
                    String translatedWord = null;
                    try {
                        translatedWord = translator.translate(word);
                    } catch (TranslatorException e) {
                        logger.error(e.getMessage(), e);
                    }
                    return "hello".equals(translatedWord) || "world".equals(translatedWord);
                }));

        verify(translator, times(2)).translate(anyString());
    }
    // add tests
    @Test
    void givenACollectionOfValidWords_whenAddWordsCalled_thenIncrementWordCount() throws TranslatorException {
        when(translator.translate("hello")).thenReturn("hello");
        when(translator.translate("world")).thenReturn("world");

        Collection<String> validWords = new ArrayList<>(Arrays.asList("hello", "world"));
        wordCounter.addWords(validWords);

        assertEquals(1, wordCounter.getCount("hello"));
        assertEquals(1, wordCounter.getCount("world"));
        assertEquals(2, wordCounter.getWordCountMap().size());
        assertTrue(wordCounter.getWordCountMap().containsKey("hello"));
        assertTrue(wordCounter.getWordCountMap().containsKey("world"));
    }

    @Test
    void givenACollectionWithOneInvalidWord_whenAddWordsCalled_thenAddAndIncrementOnlyOneWord() throws TranslatorException {
        when(translator.translate("hello")).thenReturn("hello");

        List<String> words = Arrays.asList("hello", "12345");
        wordCounter.addWords(words);

        assertEquals(1, wordCounter.getCount("hello"));
        assertEquals(0, wordCounter.getCount("12345"));
        assertEquals(1, wordCounter.getWordCountMap().size());
        assertTrue(wordCounter.getWordCountMap().containsKey("hello"));
        assertFalse(wordCounter.getWordCountMap().containsKey("12345"));

    }

    @Test
    void givenACollectionOfInvalidWords_whenAddWordsCalled_thenDoNotAddAny() {
        Collection<String> invalidWords = new ArrayList<>(Arrays.asList("12345", "$$$"));
        wordCounter.addWords(invalidWords);

        assertEquals(0, wordCounter.getCount("12345"));
        assertEquals(0, wordCounter.getCount("$$$"));
        assertEquals(0, wordCounter.getWordCountMap().size());
        assertFalse(wordCounter.getWordCountMap().containsKey("12345"));
        assertFalse(wordCounter.getWordCountMap().containsKey("$$$"));

    }

    @Test
    void givenACollectionWithADuplicateWord_whenAddWordsCalled_thenIncrementForEachAppearance() throws TranslatorException {
        when(translator.translate("hello")).thenReturn("hello");

        Collection<String> validWords = new ArrayList<>(Arrays.asList("hello", "hello"));
        wordCounter.addWords(validWords);

        assertEquals(2, wordCounter.getCount("hello"));
        assertEquals(1, wordCounter.getWordCountMap().size());
        assertTrue(wordCounter.getWordCountMap().containsKey("hello"));

    }

    @Test
    void givenACollectionWithALongWord_whenAddWordsCalled_thenAddAndIncrement() throws TranslatorException {
        when(translator.translate(anyString())).thenReturn("longword");

        StringBuilder longWordsBuilder = new StringBuilder("longword");
        for (int i = 0; i < 10; i++) {
            longWordsBuilder.append(longWordsBuilder);
        }
        String longWords = longWordsBuilder.toString();
        Collection<String> wordsCollection = Arrays.asList("longword", longWords);
        wordCounter.addWords(wordsCollection);

        assertEquals(2, wordCounter.getCount("longword"));
        assertEquals(1, wordCounter.getWordCountMap().size());
        assertTrue(wordCounter.getWordCountMap().containsKey("longword"));

    }

    @Test
    void givenConcurrentAdditions_whenAddWordsIsCalled_thenWordCountsAreCorrect() throws TranslatorException, InterruptedException {
        when(translator.translate("hello")).thenReturn("hello");

        int threadCount = 10;
        int addsPerThread = 100;
        Collection<String> words = Collections.nCopies(addsPerThread, "hello");
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> wordCounter.addWords(words)));
        }

        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(threadCount * addsPerThread, wordCounter.getWordCountMap().get("hello").get());
    }

    // get tests
    @ParameterizedTest
    @NullAndEmptySource
    void givenANullAndEmptyValue_whenGetCountCalled_thenReturnZero(String word) {
        int count = wordCounter.getCount(word);

        assertEquals(0, count);
        assertEquals(0, wordCounter.getWordCountMap().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello"})
    void givenAValidWord_whenGetCountCalled_thenReturnTheCountOfAddedWord(String word) throws TranslatorException {
        when(translator.translate("hello")).thenReturn("hello");

        wordCounter.addWords(Collections.singletonList(word));
        int count = wordCounter.getCount(word);

        assertEquals(1, count);
        assertEquals(1, wordCounter.getWordCountMap().size());
        assertTrue(wordCounter.getWordCountMap().containsKey(word));
    }

    @ParameterizedTest
    @ValueSource(strings = {"zero", "1234"})
    void givenAValidNotAddedWordAndAnInvalidWord_whenGetCountIsCalled_thenReturnZero(String word) {
        int count = wordCounter.getCount(word);

        assertEquals(0, count);
        assertEquals(0, wordCounter.getWordCountMap().size());
    }


}

