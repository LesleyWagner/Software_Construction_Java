package twitter;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
        Instant start;
        Instant end;
        if (tweets.size() == 0) {
            start = Instant.EPOCH;
            end = Instant.EPOCH;
            
            return new Timespan(start, end);
        }
        
        // Get the earliest and latest timestamp from tweets
        start = tweets.get(0).getTimestamp();
        end = tweets.get(0).getTimestamp();
        for (Tweet tweet : tweets) {
            Instant timestamp = tweet.getTimestamp(); 
            if (timestamp.isBefore(start)) {
                start = timestamp;
            }
            else if (timestamp.isAfter(end)) {
               end = timestamp;
            }
        }
        
        return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets) {
        Set<String> users = new HashSet<>();
        
        for (Tweet tweet : tweets) {
            String[] splitText = tweet.getText().split("@"); // Split tweet text around '@' characters
            
            for (int i = 0; i < splitText.length; i++) {               
                if (i != 0) {
                    String textBefore = splitText[i-1]; // Text before at-symbol
                    String textAfter = splitText[i]; // Text after at-symbol
                    boolean secondAtSign = false;
                    
                    if (i+1 != splitText.length && !textAfter.contains(" ")) {
                        secondAtSign = true;
                    }
                   
                    // If there is no such character before '@' or beginning of tweet and there is no second at-sign
                    // and the mention doesn't start with an invalid character, it is a username mention 
                    if ((textBefore.equals("") || textBefore.substring(textBefore.length()-1).matches("[^a-zA-Z0-9\\-_]")) && 
                            !secondAtSign && textAfter.substring(0,1).matches("[a-zA-Z0-9\\-_]")) {
                        // Split text around invalid character to get the username mention, 
                        // need to add ';h' for String.split() to work
                        String mention = (textAfter+";h").split("[^a-zA-Z0-9\\-_]")[0];  
                        mention = mention.toLowerCase();
                        
                        users.add(mention);
                    }
                }          
            }
        }
        
        return users;
    }

    /* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
