package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class MySocialNetworkTest {

    /* 
     * Testing strategy for guessFollowsGraph()
     * 
     * Input partitions:
     * tweets.size(): 0, 1, >1
     * tweet.text in tweets (mentions): No mention, one mention, multiple mentions in one tweet, multiple mentions of the same user,
     * multiple mentions of multiple users
     * tweet.text in tweets (hashtags): No hashtag, one hashtag, multiple hashtags in one tweet,
     * same hashtag by multiple users, >1 same hashtags by multiple users
     * 
     * Covering each part
     * 
     * Testing strategy for influencers()
     * 
     * Input partitions:
     * followsGraph (map String - Set<String>): Empty, keys only, multiple keys one empty, one key with Set.size() = 1, one key with set.size() > 1,
     * multiple keys with set.size() = 1, multiple keys with set.size() > 1, multiple keys with the same person in set
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
    private static final Tweet tweet4 = new Tweet(4, "bbit-diddle", "I will see you later ,@alyssa #lonely #sad", d1);
    private static final Tweet tweet5 = new Tweet(5, "alyssa", "@alyssa Love you.", d3);
    private static final Tweet tweet6 = new Tweet(6, "alyssa", "How are you? @bbit-diddle @mvg180", d4);
    private static final Tweet tweet7 = new Tweet(7, "bbit-diddle", "You are awful @alyssa", d4);
    private static final Tweet tweet8 = new Tweet(7, "mvg180", "What are you talking about? h@alyssa -@bbit-diddle", d4);
    private static final Tweet tweet9 = new Tweet(7, "mvg180", "Stop tweeting me. @alyssa", d4);
    private static final Tweet tweet10 = new Tweet(7, "bbit-diddle", "Shut up. @BBit-dIddlE", d3);
    private static final Tweet tweet11 = new Tweet(7, "guess_not", "Bad feels. #lonely #sad", d3);
    private static final Tweet tweet12 = new Tweet(7, "mvg180", "Won premier league #hype @pdc", d3);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // Covers tweets.size() = 0
    @Test
    public void testGuessFollowsNoTweets() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }
    
    // Covers tweets.size() = 1, multiple mentions in one tweet
    @Test
    public void testGuessFollowsGraphOneTweetMultipleMentions() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet6));
        
        assertEquals("expected singleton map", 1, followsGraph.size());
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("alyssa"::equalsIgnoreCase));
        assertTrue("expected set to contain user", followsGraph.get(followsGraph.keySet().stream().filter("alyssa"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("mvg180"::equalsIgnoreCase));
    }
    
    // Covers multiple hashtags in one tweet
    @Test
    public void testGuessFollowsGraphOneTweetOneMentionMultipleHashtags() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4));
        
        assertEquals("expected singleton map", 1, followsGraph.size());
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected set to contain user", followsGraph.get(followsGraph.keySet().stream().filter("bbit-diddle"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("alyssa"::equalsIgnoreCase));
    }
    
    // Covers tweets.size() > 1, no mentions, one hashtag
    @Test
    public void testGuessFollowsGraphMultipleTweetsNoMentionsOneHashtag() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));
    }
    
    // Covers multiple mentions of the same user, no hashtags
    @Test
    public void testGuessFollowsGraphMultipleTweetsMultipleMentionsSameUserNoHashtag() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7, tweet9));
        
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("mvg180"::equalsIgnoreCase));
        assertTrue("expected set to contain user", followsGraph.get(followsGraph.keySet().stream().filter("bbit-diddle"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("alyssa"::equalsIgnoreCase));
        assertTrue("expected set to contain user", followsGraph.get(followsGraph.keySet().stream().filter("mvg180"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("alyssa"::equalsIgnoreCase));
    }
    
    // Covers One mention, same hashtag by multiple users
    @Test
    public void testGuessFollowsGraphMultipleTweetsOneMentionSameHashtagMultipleUsers() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet2, tweet12));
        
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("mvg180"::equalsIgnoreCase));
        assertTrue("expected set to contain user", followsGraph.get(followsGraph.keySet().stream().filter("mvg180"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("pdc"::equalsIgnoreCase));
    }
    
    // Covers multiple mentions by multiple users, multiple of same hashtags by multiple users
    @Test
    public void testGuessFollowsGraphMultipleTweetsMultipleMentionsMultipleUsersSameHashtagsMultipleUsers() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet4, tweet5, tweet6, tweet7, tweet8, tweet9, tweet11));
        
        assertTrue("expected map to contain bbit-diddle", followsGraph.keySet().stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected map to contain alyssa", followsGraph.keySet().stream().anyMatch("alyssa"::equalsIgnoreCase));
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("mvg180"::equalsIgnoreCase));
        assertTrue("expected map to contain user", followsGraph.keySet().stream().anyMatch("guess_not"::equalsIgnoreCase));
        assertTrue("expected set to contain alyssa", followsGraph.get(followsGraph.keySet().stream().filter("bbit-diddle"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("alyssa"::equalsIgnoreCase));
        assertTrue("expected set to contain guess_not", followsGraph.get(followsGraph.keySet().stream().filter("bbit-diddle"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("guess_not"::equalsIgnoreCase));
        assertTrue("expected set to contain bbit-diddle", followsGraph.get(followsGraph.keySet().stream().filter("alyssa"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        assertTrue("expected set to contain mvg180", followsGraph.get(followsGraph.keySet().stream().filter("alyssa"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("mvg180"::equalsIgnoreCase));
        assertTrue("expected set to contain alyssa", followsGraph.get(followsGraph.keySet().stream().filter("mvg180"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("alyssa"::equalsIgnoreCase));
        assertTrue("expected set to contain bbit-diddle", followsGraph.get(followsGraph.keySet().stream().filter("guess_not"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
    }
    
    // Covers user mentions himself
    @Test
    public void testGuessFollowsGraphUserMentionsSelf() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet10));
        
        if (followsGraph.keySet().stream().anyMatch("bbit-diddle"::equalsIgnoreCase)) {
            assertFalse("expected set not to contain user", followsGraph.get(followsGraph.keySet().stream().filter("bbit-diddle"::equalsIgnoreCase).findFirst().get()).stream().anyMatch("bbit-diddle"::equalsIgnoreCase));
        }
       
    }
    
    // Covers no keys
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("expected empty list", influencers.isEmpty());
    }
    
    // Covers keys only
    @Test
    public void testInfluencersKeysOnly() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>());
        followsGraph.put("mvg180", new HashSet<String>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
    }
    
    // Covers one key with set.size() = 1
    @Test
    public void testInfluencersOneKeySizeOne() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("bbit-diddle")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 2", 2, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertEquals("expected descending order", 0, influencers.indexOf("bbit-diddle"));
    }
    
    // Covers one key with set.size() > 1
    @Test
    public void testInfluencersOneKeySizeMultiple() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("bbit-diddle", "mvg180")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 3", 3, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertTrue("expected list to contain user", influencers.contains("mvg180"));
        assertEquals("expected descending order", 2, influencers.indexOf("alyssa"));
    }
    
    // Covers multiple keys with one empty 
    @Test
    public void testInfluencersMultipleKeysOneEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("bbit-diddle")));
        followsGraph.put("bbit-diddle", new HashSet<String>(Arrays.asList("mvg180")));
        followsGraph.put("mvg180", new HashSet<String>(Arrays.asList()));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 3", 3, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertTrue("expected list to contain user", influencers.contains("mvg180"));
        assertEquals("expected descending order", 2, influencers.indexOf("alyssa"));
    }
    
    // Covers multiple keys all with set.size = 1
    @Test
    public void testInfluencersMultipleKeysSizeOne() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("bbit-diddle")));
        followsGraph.put("bbit-diddle", new HashSet<String>(Arrays.asList("mvg180")));
        followsGraph.put("mvg180", new HashSet<String>(Arrays.asList("bbit-diddle")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 3", 3, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertTrue("expected list to contain user", influencers.contains("mvg180"));
        assertEquals("expected descending order", 0, influencers.indexOf("bbit-diddle"));
        assertEquals("expected descending order", 1, influencers.indexOf("mvg180"));
    }
    
    // Covers multiple keys with set.size() > 1
    @Test
    public void testInfluencersMultipleKeysSizeMultiple() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("mvg180", "guess_not")));
        followsGraph.put("mvg180", new HashSet<String>(Arrays.asList("bbit-diddle", "alyssa")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 4", 4, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertTrue("expected list to contain user", influencers.contains("mvg180"));
        assertTrue("expected list to contain user", influencers.contains("guess_not"));
    }
    
    // Covers multiple keys with same person in set
    @Test
    public void testInfluencersMultipleKeysSamePerson() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("alyssa", new HashSet<String>(Arrays.asList("mvg180", "guess_not", "bbit-diddle")));
        followsGraph.put("mvg180", new HashSet<String>(Arrays.asList("bbit-diddle", "alyssa")));
        followsGraph.put("guess_not", new HashSet<String>(Arrays.asList("bbit-diddle", "alyssa")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("expected list of size 4", 4, influencers.size());
        assertTrue("expected list to contain user", influencers.contains("alyssa"));
        assertTrue("expected list to contain user", influencers.contains("bbit-diddle"));
        assertTrue("expected list to contain user", influencers.contains("mvg180"));
        assertTrue("expected list to contain user", influencers.contains("guess_not"));
        assertEquals("expected descending order", 0, influencers.indexOf("bbit-diddle"));
        assertEquals("expected descending order", 1, influencers.indexOf("alyssa"));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
