package library;

import java.time.Year;
import java.time.ZoneId;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for Book ADT.
 */
public class BookTest {

    /*
     * Testing strategy for each operation in Book
     * ==================
     * 
     * Book()
     * title.length(): 1, >1
     * title: Contains only alphanumeric characters, contains spaces, contains other characters
     * authors.size(): 1, >1
     * authors: Contains author name that contains only alphanumeric characters, contains spaces, contains other characters,
     * contains duplicate author names, author names that differ only by cases, authors has different order only
     * year: 0, >1 and <current year, equal to current year, >current year
     * 
     * getTitle(), getAuthors(), getYear()
     * this: Book
     *
     * toString()
     * this: Book
     * 
     * equals()
     * that.title: this.title.equals(that.title), !this.title.equals(that.title)
     * that.authors: this.authors contains everything in that.authors and that.authors contains everything in this.authors,
     * negation of previous
     * that.year: this.year = that.year, this.year != that.year
     * 
     * hashCode()
     * this: this.equals(that) -> this.hashCode() = that.hashCode(),
     * !this.equals(that) -> this.hashCode() != that.hashCode()
     * 
     * Covers every part
     */
    
    // title.length() = 1, title contains alphanumerics, authors.size >1, 
    // authors contains spaces, contains duplicate author names, year > 1, <current year, books are equal
    @Test
    public void testEqualBooksDuplicateAuthors() {
        Book book1 = new Book("a", Arrays.asList("Hey Man", "Hey Man", "Sup"), 11);
        Book book2 = new Book("a", Arrays.asList("Hey Man", "Hey Man", "Sup"), 11);
        assertEquals("a", book1.getTitle());
        assertTrue("Contains Hey Man", book1.getAuthors().contains("Hey Man"));
        assertTrue("Contains Sup", book1.getAuthors().contains("Sup"));
        assertEquals(11, book1.getYear());
        
        assertTrue("Books are equal", book1.equals(book2));
        assertTrue("Hashcodes are equal", book1.hashCode() == book2.hashCode());
    }
    
    // title.length() = 1, title contains alphanumerics, authors.size >1, 
    // authors contains spaces, contains duplicate author names, year > 1, <current year,
    // books unequal by year only
    @Test
    public void testUnequalBooksDuplicateAuthors() {
        Book book1 = new Book("a", Arrays.asList("Hey Man", "Hey Man", "Sup"), 1880);
        Book book2 = new Book("a", Arrays.asList("Hey Man", "Sup"), 1881);
        assertEquals("a", book1.getTitle());
        assertTrue("Contains Hey Man", book1.getAuthors().contains("Hey Man"));
        assertTrue("Contains Sup", book1.getAuthors().contains("Sup"));
        assertEquals(1880, book1.getYear());
        
        assertFalse("Books are unequal", book1.equals(book2));
        assertFalse("Hashcodes are unequal", book1.hashCode() == book2.hashCode());
    }
    
    // title.length() >1, title contains spaces, authors.size >1, author names differ by case only, 
    // authors contains other characters, year = 0, books are unequal by authors only
    @Test
    public void testUnequalBooksAuthorsDifferByCase() {
        Book book1 = new Book("Great book", Arrays.asList("hey Man", "1.Sup"), 0);
        Book book2 = new Book("Great book", Arrays.asList("Hey Man", "1.Sup"), 0);
        assertEquals("Great book", book1.getTitle());
        assertTrue("Contains hey Man", book1.getAuthors().contains("hey Man"));
        assertTrue("Contains 1.Sup", book1.getAuthors().contains("1.Sup"));
        assertEquals(0, book1.getYear());
        
        assertFalse("Books are unequal", book1.equals(book2));
        assertFalse("Hashcodes are unequal", book1.hashCode() == book2.hashCode());
    }
    
    // title.length() >1, title contains other characters, authors.size = 1, 
    // authors contains alphanumerics only, year = current year, books are equal
    @Test
    public void testEqualBooksInCurrentYear() {
        Book book1 = new Book("Hey, don't", Arrays.asList("sup"), Year.now(ZoneId.of("Europe/Amsterdam")).getValue());
        Book book2 = new Book("Hey, don't", Arrays.asList("sup"), Year.now(ZoneId.of("Europe/Amsterdam")).getValue());
        assertEquals("Hey, don't", book1.getTitle());
        assertTrue("Contains sup", book1.getAuthors().contains("sup"));
        assertEquals(Year.now(ZoneId.of("Europe/Amsterdam")).getValue(), book1.getYear());
        
        assertTrue("Books are equal", book1.equals(book2));
        assertTrue("Hashcodes are equal", book1.hashCode() == book2.hashCode());
    }
    
    // title.length() >1, title contains other characters, authors.size = 1, 
    // authors contains alphanumerics only, year = current year, Books unequal by title only
    @Test
    public void testUnequalBooksInCurrentYear() {
        Book book1 = new Book("Hey, don't do it", Arrays.asList("sup"), Year.now(ZoneId.of("Europe/Amsterdam")).getValue());
        Book book2 = new Book("Hey, don't", Arrays.asList("sup"), Year.now(ZoneId.of("Europe/Amsterdam")).getValue());
        assertEquals("Hey, don't do it", book1.getTitle());
        assertTrue("Contains sup", book1.getAuthors().contains("sup"));
        assertEquals(Year.now(ZoneId.of("Europe/Amsterdam")).getValue(), book1.getYear());
        
        assertFalse("Books are unequal", book1.equals(book2));
        assertFalse("Hashcodes are unequal", book1.hashCode() == book2.hashCode());
    }
    
    // title.length() = 1, title contains other characters, authors.size = 1,
    // authors contains spaces, year > current year
//    @Test(expected=AssertionError.class)
//    public void testBadRepBookInFutureYear() {
//        Book book1 = new Book(".", Arrays.asList("Hey Man"), Year.now(ZoneId.of("Europe/Amsterdam")).getValue()+2);
//    }
    
    // title.length() >1, title contains spaces, authors.size >1, authors has different order only 
    // authors contains other characters, year = 0, books are unequal by authors only
    @Test
    public void testUnequalBooksByAuthorOrder() {
        Book book1 = new Book("Great book", Arrays.asList("1.Sup", "Hey Man"), 0);
        Book book2 = new Book("Great book", Arrays.asList("Hey Man", "1.Sup"), 0);
        assertEquals("Great book", book1.getTitle());
        assertTrue("Contains Hey Man", book1.getAuthors().contains("Hey Man"));
        assertTrue("Contains 1.Sup", book1.getAuthors().contains("1.Sup"));
        assertEquals(0, book1.getYear());
        assertTrue("String contains title", book1.toString().contains("Great book"));
        assertTrue("String contains 1.Sup", book1.toString().contains("1.Sup"));
        assertTrue("String contains Hey Man", book1.toString().contains("Hey Man"));
        assertTrue("String contains year", book1.toString().contains("0"));
        
        assertFalse("Books are unequal", book1.equals(book2));
        assertFalse("Hashcodes are unequal", book1.hashCode() == book2.hashCode());
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
