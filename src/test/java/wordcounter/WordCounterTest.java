package wordcounter;


import org.chris.wordcounter.Translator;
import org.chris.wordcounter.impl.WordCounter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


class WordCounterTest {

    static WordCounter wordCounter;
    @Mock
    static Translator translator;

    @BeforeEach
    void setUp() {
        translator = mock(Translator.class);
        wordCounter = new WordCounter(translator);
    }

    // add tests
    @Test
    void givenACollectionOfValidNonEnglishWords_whenAddWordsCalled_thenReturnEnglishTranslation() {
        when(translator.translate("hola")).thenReturn("hello");
        when(translator.translate("mundo")).thenReturn("world");

        List<String> validNonEnglishWords = Arrays.asList("hola", "mundo");

        assertTrue(validNonEnglishWords.stream()
                .allMatch(word -> {
                    String translatedWord = translator.translate(word);
                    return translatedWord.equals("hello") || translatedWord.equals("world");
                }));

        verify(translator, times(2)).translate(anyString());
    }

    @Test
    void givenACollectionOfValidWords_whenAddWordsCalled_thenEnsureWordsAdded() {
        when(translator.translate("hello")).thenReturn("hello");
        when(translator.translate("world")).thenReturn("world");

        Collection<String> validWords = new ArrayList<>(Arrays.asList("hello", "world"));
        wordCounter.addWords(validWords);

        assertEquals(validWords.stream().anyMatch(word -> word.equals("hello")) ? 1 : 0, wordCounter.getCount("hello"));
        assertEquals(validWords.stream().anyMatch(word -> word.equals("world")) ? 1 : 0, wordCounter.getCount("world"));
    }
    @Test
    void givenACollectionWithOneInvalidWord_whenAddWordsCalled_thenAddOnlyOneWord(){
        when(translator.translate("hello")).thenReturn("hello");

        List<String> words = Arrays.asList("hello", "12345");
        wordCounter.addWords(words);

        assertEquals(words.stream().anyMatch(word -> word.equals("hello")) ? 1 : 0, wordCounter.getCount("hello"));
        assertEquals(words.stream().anyMatch(word -> word.equals("12345")) ? 0 : 1, wordCounter.getCount("12345"));

    }
    @Test
    void givenACollectionOfInvalidWords_whenAddWordsCalled_thenDoNotAdd(){
        Collection<String> invalidWords = new ArrayList<>(Arrays.asList("12345", "$$$"));
        wordCounter.addWords(invalidWords);

        assertEquals(invalidWords.stream().anyMatch(word -> word.equals("12345")) ? 0 : 1, wordCounter.getCount("12345"));
        assertEquals(invalidWords.stream().anyMatch(word -> word.equals("$$$")) ? 0 : 1, wordCounter.getCount("$$$"));
    }

    @Test
    void givenACollectionWithOneDuplicateWord_whenAddWordsCalled_thenReturnTheCountOfAddedWords() {
        when(translator.translate("hello")).thenReturn("hello");

        Collection<String> validWords = new ArrayList<>(Arrays.asList("hello", "hello"));
        wordCounter.addWords(validWords);

        assertEquals(validWords.stream().anyMatch(word -> word.equals("hello")) ? 2 : 0, wordCounter.getCount("hello"));

    }

    // get tests
    @ParameterizedTest
    @NullAndEmptySource
    void givenANullAndEmptyValue_whenGetCountCalled_thenReturnZero(String word) {
        int count = wordCounter.getCount(word);

        assertEquals(0, count);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello"})
    void givenAValidWord_whenGetCountCalled_thenReturnTheCountOfAddedWord(String word) {
        when(translator.translate("hello")).thenReturn("hello");

        wordCounter.addWords(Collections.singletonList(word));
        int count = wordCounter.getCount(word);

        assertEquals(1, count);
    }

    @ParameterizedTest
    @ValueSource(strings = {"zero", "1234"})
    void givenAValidNotAddedWordAndAnInvalidWord_whenGetCountIsCalled_thenReturnZero(String word) {
        int count = wordCounter.getCount(word);

        assertEquals(0, count);
    }


}

