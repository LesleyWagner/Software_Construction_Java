package library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Book is an immutable type representing an edition of a book -- not the physical object, 
 * but the combination of words and pictures that make up a book.  Each book is uniquely
 * identified by its title, author list, and publication year.  Alphabetic case and author 
 * order are significant, so a book written by "Fred" is different than a book written by "FRED".
 */
public class Book {
    // reps
    private final String title;
    private final List<String> authors;
    private final int year;
    
    // Rep invariant:
    //  title must contain at least one non-space character
    //  authors must have at least one name and each name must contain at least one non-space character
    //  year must be nonnegative
    // Abstraction function:
    //  represents edition of a book, uniquely identified by its title, author list and publication year
    // Safety from rep exposure:
    //  All fields are private;
    //  title and year (string, int) are immutable;
    //  authors is mutable, so Book's constructor and getAuthor's make defensive copies
    
    /**
     * Make a Book.
     * @param title Title of the book. Must contain at least one non-space character.
     * @param authors Names of the authors of the book.  Must have at least one name, and each name must contain 
     * at least one non-space character.
     * @param year Year when this edition was published in the conventional (Common Era) calendar.  Must be nonnegative. 
     */
    public Book(String title, List<String> authors, int year) {
        this.title = title;
        this.authors = new ArrayList<String>(authors);
        this.year = year;
        
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert this.title.matches("(.*)[^\\s]+(.*)");
        assert this.year >= 0;
        assert this.authors.size() > 0;
        
        for (String author : this.authors) {
            assert author.matches("(.*)[^\\s]+(.*)");
        }
    }
    
    /**
     * @return the title of this book
     */
    public String getTitle() {
        return this.title;
    }
    
    /**
     * @return the authors of this book
     */
    public List<String> getAuthors() {
        return new ArrayList<String>(this.authors);
    }

    /**
     * @return the year that this book was published
     */
    public int getYear() {
        return this.year;
    }

    /**
     * @return human-readable representation of this book that includes its title,
     *    authors, and publication year
     */
    public String toString() {      
        return "{ title: " + this.title + ", authors: " + this.authors + 
                ", year: " + this.year + " }";
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof Book)) return false;
        Book thatBook = (Book) that;
        
        if (!this.title.equals(thatBook.getTitle())) return false;
        if (!(this.year == thatBook.getYear())) return false;
        if (!this.authors.equals(thatBook.getAuthors())) return false;
        
        return true;
    }
     
    @Override
    public int hashCode() {
        return this.title.hashCode() + this.year + this.authors.hashCode();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
