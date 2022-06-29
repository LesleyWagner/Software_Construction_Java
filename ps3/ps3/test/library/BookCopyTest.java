package library;

import org.junit.Test;

import library.BookCopy.Condition;

import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * Test suite for BookCopy ADT.
 */
public class BookCopyTest {

    /*
     * Testing strategy for each operation in BookCopy
     * ==================
     * 
     * BookCopy()
     * book: Book
     * 
     * getBook
     * this: BookCopy
     * 
     * getCondition
     * this: BookCopy.condition = good, BookCopy.condition = damaged
     * 
     * setCondition
     * Condition: Condition.GOOD, Condition.DAMAGED
     *
     * toString()
     * this: Book
     * 
     * Covers every part
     */
    
    // Covers every part
    @Test
    public void testBookCopy() {
        Book book = new Book("Once upon a time", Arrays.asList("mickey", "mansell"), 1990);
        BookCopy copy = new BookCopy(book);
        assertEquals(book, copy.getBook());
        assertEquals(Condition.GOOD, copy.getCondition());
        assertTrue("String contains book.title", copy.toString().contains(book.getTitle()));
        assertTrue("String contains book.authors", copy.toString().contains(book.getAuthors().toString()));
        assertTrue("String contains book.year", copy.toString().contains(String.valueOf(book.getYear())));
        assertTrue("String contains copy.condition", copy.toString().contains(copy.getCondition().toString().toLowerCase()));
        
        copy.setCondition(Condition.GOOD);
        assertEquals(Condition.GOOD, copy.getCondition());
        
        copy.setCondition(Condition.DAMAGED);
        assertEquals(Condition.DAMAGED, copy.getCondition());
        
    }
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
