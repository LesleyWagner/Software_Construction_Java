package twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        
        Set<String> authors = new HashSet<>();
        for (Tweet tweet : tweets) {
            authors.add(tweet.getAuthor());          
        }
        
        for (String user : authors) {
            List<Tweet> tweetsBy = Filter.writtenBy(tweets, user); // get tweets written by user
            
            // Find mentions in tweets written by user and use as evidence for people the user follows
            Set<String> follows = Extract.getMentionedUsers(tweetsBy);
            follows.remove(user); // Do not include the user following himself 
            follows.addAll(getUsersWithSharedHashtags(tweets, authors, tweetsBy, user));
            followsGraph.put(user, follows);
        }
        
        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        Map<String, Integer> followersCount = new HashMap<>();
        
        for (String user : followsGraph.keySet()) {
            user = user.toLowerCase();
            followersCount.put(user, 0);
        }
        
        for (Set<String> follows : followsGraph.values()) {
            for (String user : follows) {
                user = user.toLowerCase();
                if (followersCount.containsKey(user)) {
                    followersCount.put(user, followersCount.get(user)+1);
                }
                else {
                    followersCount.put(user, 1);
                }
            }
        }
        
        // Sort users in followersCount by amount of followers in reverse order
        List<String> influencers = new ArrayList<>(followersCount.keySet());    
        Comparator<String> followerComparer = Comparator.comparing(influencer -> followersCount.get(influencer));
        influencers.sort(followerComparer.reversed());
        
        return influencers;
    }
    
    /**
     * Helper function to guessFollowsGraph(). Gets users who share >1 hashtags
     * with a user.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @param authors
     *            a list of all authors from the list of tweets
     * @param tweetsByUser
     *            a list of tweets by a particular user.
     * @param user
     *            a part
     * @return Set of users who the user follows by evidence of shared hashtags
     */
    private static Set<String> getUsersWithSharedHashtags(List<Tweet> tweets, Set<String> authors,
            List<Tweet> tweetsByUser, String user) {
        
        Set<String> follows = new HashSet<>();
        
        Set<String> hashtagsByUser = getHashtags(tweetsByUser);
        Map<String, Set<String>> hashtagsByOthers = new HashMap<>();
         
        for (String author : authors) {
            if (!author.equals(user)) {
                List<Tweet> tweetsBy = Filter.writtenBy(tweets, author); 
                hashtagsByOthers.put(author, getHashtags(tweetsBy));
            }
        }
        
        for (Map.Entry<String, Set<String>> entry: hashtagsByOthers.entrySet()) {
            int hashtagMatches = 0;
            for (String hashtag : hashtagsByUser) {
                if (entry.getValue().contains(hashtag)) {
                    hashtagMatches++;
                }
            }
            
            if (hashtagMatches > 1) {
                follows.add(entry.getKey());
            }
        }
        
        return follows;
    }
    
    /**
     * Helper function to getUsersWithSharedHashtags(). Gets all hashtags from a user by the text in tweets.
     * 
     * @param tweets
     *            a list of tweets from a user.
     * @return a set of all hashtags from the tweets of that user.
     */
    private static Set<String> getHashtags (List<Tweet> tweets) {
        Set<String> hashtags = new HashSet<>();
        
        for (Tweet tweet : tweets) {
            String[] tweetWords = tweet.getText().split(" ");
            
            for (String word : tweetWords) {
                if (word.startsWith("#")) {
                    hashtags.add(word);
                }
            }
        }
        
        return hashtags;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
