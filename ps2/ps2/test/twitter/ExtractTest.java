package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ExtractTest {

    /* 
     * Testing strategy for getTimespan()
     * 
     * Input partitions:
     * tweets.length(): 0, 1, >1
     * 
     * Exhaustive coverage of partitions
     * 
     * Testing strategy for getMentionedUsers()
     * 
     * Input partitions:
     * tweets.length(): 0, 1, >1
     * tweet.text in tweets: No usernames, one username, some usernames, some duplicate usernames, 
     * multiple usernames in one tweet, username at beginning of tweet, username at end of tweet
     * 
     * Username mentions: digit, letter, underscore, and hyphen before mention "@xxxx", invalid character (for username) after mention,
     * invalid character (for username) before mention, duplicate usernames with different cases
     * 
     * Covering each part 
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
    private static final Tweet tweet11 = new Tweet(11, "mvg180", "Fake adress great@mvg", d3);
    
    
    private static final String[] values2 = new String[] {"bbit-diddle"};
    private static final String[] values3 = new String[] {"bbit-diddle", "alyssa"};
    private static final String[] values4 = new String[] {"alyssa"};
    private static final String[] values5 = new String[] {"bbit-diddle"};
    private static final String[] values6 = new String[] {"bbit-diddle", "mvg180"};
    private static final Set<String> mentionsTest2 = new HashSet<>(Arrays.asList(values2));
    private static final Set<String> mentionsTest3 = new HashSet<>(Arrays.asList(values3));
    private static final Set<String> mentionsTest4 = new HashSet<>(Arrays.asList(values4));
    private static final Set<String> mentionsTest5 = new HashSet<>(Arrays.asList(values5));
    private static final Set<String> mentionsTest6 = new HashSet<>(Arrays.asList(values6));
    
   
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers tweets.length() = 0   
    @Test
    public void testGetTimespanNoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList());
        
        assertTrue("expected timespan", timespan.getStart().equals(timespan.getEnd()));
    }
    
    // Covers tweets.length() = 1  
    @Test
    public void testGetTimespanOneTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1));
        
        assertEquals("timespan equals", d1, timespan.getStart());
        assertEquals("timespan equals", d1, timespan.getEnd());
    }
    
    // Covers tweets.length() = 2   
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    // Covers implementation assumes tweets' timestamps are in order  
    @Test
    public void testGetTimespanAssumesInOrderBug() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet2, tweet1));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    // Covers multiple tweets with the same timestamp
    @Test
    public void testGetTimespanMultipleSameTimestamp() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet6, tweet7, tweet9));
        
        assertEquals("expected start", d4, timespan.getStart());
        assertEquals("expected end", d4, timespan.getEnd());
    }
    
    // Covers tweets.length() = 0
    @Test
    public void testGetMentionedUsersNoTweets() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList());
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // Covers tweet.text in tweets contains no username and tweets.length() > 1
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet2));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    // Covers tweet.text in tweets contains one username and tweets.length() = 1, invalid character after username
    @Test
    public void testGetMentionedUsersOneMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
        
        assertTrue("expected user", mentionedUsers.stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
    }
    
    // Covers tweet.text in tweets contains >1 usernames without duplicates, invalid character before username
    @Test
    public void testGetMentionedUsersSomeMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet2, tweet3, tweet5));
        
        assertTrue("expected user", mentionedUsers.stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected user", mentionedUsers.stream().anyMatch("alyssa"::equalsIgnoreCase));
    }
    
    // Covers tweet.text in tweets contains duplicate usernames and letter, hyphen before mention
    @Test
    public void testGetMentionedUsersDuplicateMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet4, tweet5, tweet8));
        
        assertTrue("expected user", mentionedUsers.stream().anyMatch("alyssa"::equalsIgnoreCase));
    }
    
    // Covers tweet.text in tweets contains duplicate usernames with different cases and digit before mention
    @Test
    public void testGetMentionedUsersDifferentCasesMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3, tweet9, tweet10));
        
        assertTrue("expected user", mentionedUsers.stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertEquals("expected singleton set", 1, mentionedUsers.size());
    }
    
    // Covers tweet.text in tweets contains multiple usernames in one tweet and underscore before mention
    @Test
    public void testGetMentionedUsersTwoInOneMentions() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1, tweet6, tweet7));
        
        assertTrue("expected user", mentionedUsers.stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected user", mentionedUsers.stream().anyMatch("mvg180"::equalsIgnoreCase));
    }
    
    // Covers tweet.text contains email adress, possibly mistaken for mention
    @Test
    public void testGetMentionedUsersEmailAdressBug() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet11));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
