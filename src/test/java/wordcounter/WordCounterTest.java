package wordcounter;

import org.chris.wordcounter.impl.WordCounter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCounterTest {



    private WordCounter wordCounter;
    //Setup
    // TODO Mock Translate?
    @BeforeAll
    public void setUp()  {
        wordCounter = new WordCounter();
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

