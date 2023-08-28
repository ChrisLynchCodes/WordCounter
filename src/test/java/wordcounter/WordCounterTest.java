package wordcounter;


import org.chris.wordcounter.Translator;
import org.chris.wordcounter.impl.WordCounter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class WordCounterTest {



    private static WordCounter wordCounter;
    @Mock
    private static Translator translator;
    //Setup
   @BeforeAll
    static void setUp()  {
        wordCounter = new WordCounter();
        translator = mock(Translator.class);
    }

    @Test
    void givenACollectionOfValidNonEnglishWords_whenAddWordsCalled_thenReturnEnglishTranslation(){
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
    void givenACollectionOfValidWords_whenAddWordsCalled_thenReturnTheCountOfAddedWords(){
        List<String> validWords = Arrays.asList("hello", "world");
        int wordCount = wordCounter.addWords(validWords);
        assertEquals(validWords.size(), wordCount);
    }
    @Test
    void givenACollectionWithOneInvalidWord_whenAddWordsCalled_thenReturnTheCountOfAddedWords(){
        List<String> validWords = Arrays.asList("hello", "12345");
        int wordCount = wordCounter.addWords(validWords);
        assertEquals(validWords.size() -1, wordCount);
    }
    @Test
    void givenACollectionOfInvalidWords_whenAddWordsCalled_thenReturnZero(){
        List<String> validWords = Arrays.asList("12345", "6789");
        int wordCount = wordCounter.addWords(validWords);
        assertEquals(0, wordCount);
    }

    @Test
    void givenAValidWord_whenGetCountCalled_thenReturnTheCountOfAddedWords(){
        String word = "hello";
        int count = wordCounter.getCount(word);
        assertEquals(1, count);
    }
    @Test
    void givenAnInvalidWord_whenGetCountCalled_thenReturnZero(){
        String word = "12345";
        int count = wordCounter.getCount(word);
        assertEquals(0, count);
    }
    @Test
    void givenAValidNotAddedWord_whenGetCountIsCalled_thenReturnZero(){
        String word = "zero";
        int count = wordCounter.getCount(word);
        assertEquals(0, count);

    }


}

