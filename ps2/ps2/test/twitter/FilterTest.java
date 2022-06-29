package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FilterTest {

    /* 
     * Testing strategy for writtenBy()
     * 
     * Input partitions:
     * tweets.size(): 0, 1, >1
     * username: valid username
     * tweet.getAuthor() in tweets: 0, 1, >1 ocurrences of username 
     * 
     * Covering each part
     * 
     * Testing strategy for inTimespan()
     * 
     * Input partitions:
     * tweets.size(): 0, 1, >1
     * timespan: timespan of size 0, timespan that covers multiple tweets
     * timespan that covers no tweets, timespan that covers one tweet, timespan that covers multiple tweets with minimal length
     * 
     * 
     * Covering each part 
     * 
     * Testing strategy for Containing()
     * 
     * Input partitions:
     * tweets.size(): 0, 1, >1
     * words.size(): 0, 1, >1
     * tweet.text in tweets: No tweet contains word in words, one tweet contains word in words, multiple tweets contain a word in words,
     * multiple tweets contain multiple words in words, tweet contains word with different cases
     * 
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-03-17T10:00:00Z");
    private static final Instant d4 = Instant.parse("2016-04-17T10:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbit-diddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "Get off me. @bbit-diddle;", d2);
    private static final Tweet tweet4 = new Tweet(4, "bbit-diddle", "I will see you later ,@alyssa", d1);
    private static final Tweet tweet5 = new Tweet(5, "alyssa", "@alyssa Love you.", d3);
    private static final Tweet tweet6 = new Tweet(6, "alyssa", "How are you? @bbit-diddle @mvg180", d4);
    private static final Tweet tweet7 = new Tweet(7, "bbit-diddle", "You are awful _@alyssa", d4);
    private static final Tweet tweet8 = new Tweet(8, "mvg180", "What are you talking about? h@alyssa -@bbit-diddle", d4);
    private static final Tweet tweet9 = new Tweet(9, "mvg180", "Stop tweeting me. 43@alyssa", d4);
    private static final Tweet tweet10 = new Tweet(10, "bbit-diddle", "Shut up. @BBit-dIddlE", d3);
    private static final Tweet tweet11 = new Tweet(11, "bbit-diddle", "I am an artist.", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers tweets.size() = 0 and 0 occurences of username
    @Test
    public void testWrittenByNoTweets() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(), "alyssa");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    // Covers tweets.size() = 1 and 1 occurence of username
    @Test
    public void testWrittenByOneTweetSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2), "bbit-diddle");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet2));
    }
    
    // Covers tweets.size() >1 and 0 occurences of username
    @Test
    public void testWrittenByMultipleTweetsNoResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet3, tweet4, tweet5, tweet6), "mvg180");
        
        assertTrue("expected empty list", writtenBy.isEmpty());
    }
    
    // Covers tweets.size() > 1 and 1 occurence of username
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    // Covers tweets.size() >1 and >1 occurences of username
    @Test
    public void testWrittenByMultipleTweetsMultipleResults() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2, tweet4, tweet7), "bbit-diddle");
        
        assertEquals("expected list of size 3", 3, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet2));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet4));
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet7));
        assertEquals("expected same order", 0, writtenBy.indexOf(tweet2));
        assertEquals("expected same order", 1, writtenBy.indexOf(tweet4));
        assertEquals("expected same order", 2, writtenBy.indexOf(tweet7));
    }
    
    // Covers different cases in username
    @Test
    public void testWrittenByMustBeCaseInsensitive() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet2), "BBIT-diddle");
        
        assertTrue("expected tweet", writtenBy.contains(tweet2));
        assertEquals("expected singleton set", 1, writtenBy.size());
    }
    
    // Covers tweets.size() = 0
    @Test
    public void testInTimespanNoTweets() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    // Covers tweet.size() = 1, no tweet in timespan
    @Test
    public void testInTimespanOneTweetNoResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet5), new Timespan(testStart, testEnd));
        
        assertTrue("expected empty list", inTimespan.isEmpty());
    }
    
    // Covers tweet.size() = 1, one tweet in timespan     
    @Test
    public void testInTimespanOneTweetOneResult() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1)));
    }
    
    // Covers tweet.size() = 1, one tweet in timespan, size of timespan = 0    
    @Test
    public void testInTimespanOneTweetOneResultTimespanZero() {
        Instant testStart = Instant.parse("2016-02-17T10:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T10:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1)));
    }
    
    // Covers tweets.size >1, no tweet in timespan
    @Test
    public void testInTimespanMultipleTweetsNoResults() {
        Instant testStart = Instant.parse("2015-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2015-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertTrue("expected non-empty list", inTimespan.isEmpty());
    }
    
    // Covers tweets.size >1, one tweet in timespan
    @Test
    public void testInTimespanMultipleTweetsSingleResult() {
        Instant testStart = Instant.parse("2016-01-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet7, tweet8, tweet4), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet4)));
    }
    
    // Covers tweets.size >1, >1 tweets in timespan
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
    // Covers tweets.size >1, >1 tweets in timespan, size of timespan = 0
    @Test
    public void testInTimespanMultipleTweetsMultipleResultsTimespanZero() {
        Instant testStart = Instant.parse("2016-02-17T11:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T11:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet3), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet2, tweet3)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet2));
    }
    
    // Covers tweets.size >1, >1 tweets in timespan , timespan of minimal length
    @Test
    public void testInTimespanMultipleTweetsMultipleResultsMinimalLength() {
        Instant testStart = Instant.parse("2016-02-17T10:00:00Z");
        Instant testEnd = Instant.parse("2016-04-17T10:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2, tweet6, tweet7), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2, tweet6, tweet7)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
        assertEquals("expected same order", 1, inTimespan.indexOf(tweet2));
        assertEquals("expected same order", 2, inTimespan.indexOf(tweet6));
        assertEquals("expected same order", 3, inTimespan.indexOf(tweet7));
    }
    
    // Covers tweets.size() = 0
    @Test
    public void testContainingNoTweets() {
        List<Tweet> containing = Filter.containing(Arrays.asList(), Arrays.asList("talk"));
        
        assertTrue("expected non-empty list", containing.isEmpty());
    }
    
    // Covers tweets.size() > 1, words.size() = 0
    @Test
    public void testContainingMultipleTweetsNoWords() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList());
        
        assertTrue("expected non-empty list", containing.isEmpty());
    }
    
    // Covers tweets.size() = 1, one tweet contains word
    @Test
    public void testContainingOneTweetOneResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1), Arrays.asList("talk", "humble"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1)));
    }
    
    // Covers words.size() > 1, no tweet contains word
    @Test
    public void testContainingMultipleTweetsMultipleWordsNoResult() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("Pie", "grandma", "baby"));
        
        assertTrue("expected non-empty list", containing.isEmpty());
    }
    
    // Covers words.size() = 1, multiple tweets contain one word
    @Test
    public void testContainingMultipleTweetsOneWordMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet4, tweet8), Arrays.asList("you"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet4, tweet8)));
        assertEquals("expected same order", 0, containing.indexOf(tweet4));
    }
    
    // Covers multiple tweets contain multiple words
    @Test
    public void testContainingMultipleTweetsMultipleWordsMultipleResults() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet4), Arrays.asList("talk", "you"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet4)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
    }
    
    // Covers tweet contains word but with different cases
    @Test
    public void testContainingMultipleTweetsMultipleWordsOneResultDifferentCases() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet9), Arrays.asList("help", "jumbo", "stop"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet9)));
    }
    
    // Covers tweet contains word but with different cases
    @Test
    public void testContainingSubstringMatchBug() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet11), Arrays.asList("art"));
        
        assertTrue("expected empty list", containing.isEmpty());
    }
    
    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
