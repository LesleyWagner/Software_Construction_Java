package library;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test suite for BigLibrary's stronger specs.
 */
public class BigLibraryTest {
    
    /* 
     * NOTE: use this file only for tests of BigLibrary.find()'s stronger spec.
     * Tests of all other Library operations should be in LibraryTest.java 
     */

    /*
     * Testing strategy
     * ==================
     * 
     * Big Library find()
     * 
     * query: empty string, query word(s) not in book, query word matches word in book.title,
     *      query word matches word in book.authors, query word matches word in book but different capitalisation,
     *      query word matches word in multiple books with different availability, different year
     *      query word matches word in multiple books with same availability, different year,
     *      query words match 1 word in one book, 1 word in another book,
     *      query words match 2 words in one book, 1 word in another book,
     *      query contains contiguous keyword with match,
     *      query contains multiple contiguous keywords, one with 2 matches, the other with 1 match,
     *      query contains one normal keyword, one contiguous keyword, where one book matches both keywords,
     *      another book matches only the contiguous keyword
     * 
     * Covers every part
     */
    
    // TestBooks
    Book book1 = new Book("Good book", Arrays.asList("Hamilton", "Bottas", "Alonso"), 2004);
    Book book2 = new Book("Great book", Arrays.asList("Gasly"), 2017);
    Book book3 = new Book("Great book", Arrays.asList("Gasly"), 2010);
    Book book4 = new Book("Good book", Arrays.asList("Kimi Raikonen"), 2004);
    
    // Covers empty string, query word not in book, matches word in book.title, book.authors, 
    //  matches word in book with different capitalisation
    @Test
    public void testFindCapitalisation() {
        Library library = new BigLibrary();
        library.buy(book1);
        library.buy(book2);
        
        assertEquals(Collections.emptyList(), library.find(""));
        assertEquals(Collections.emptyList(), library.find("kubica"));
        assertEquals(Collections.singletonList(book1), library.find("Good"));
        assertEquals(Collections.singletonList(book2), library.find("Gasly"));
        assertEquals(Collections.singletonList(book1), library.find("good"));
                
    }
    
    // Covers query word matches word in multiple books with different availability, different year
    // query word matches word in multiple books with same availability, different year
    @Test
    public void testFindSortByAvailabilityAndYear() {
        Library library = new BigLibrary();
        BookCopy book2Copy = library.buy(book2);
        library.buy(book3);
        
        List<Book> sortedByYear = library.find("book");
        assertEquals(2, sortedByYear.size());
        assertTrue(sortedByYear.indexOf(book2) < sortedByYear.indexOf(book3));
        library.checkout(book2Copy);
        List<Book> sortedByAvailability = library.find("book");
        assertEquals(2, sortedByAvailability.size());
        assertTrue(sortedByAvailability.indexOf(book2) > sortedByAvailability.indexOf(book3));
                
    }
    
    // query words match 1 word in one book, 1 word in another book,
    // query words match 2 words in one book, 1 word in another book,
    @Test
    public void testFindSortByMatch() {
        Library library = new BigLibrary();
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        
        List<Book> booksList1 = library.find("great good");
        assertEquals(3, booksList1.size());
        List<Book> booksList2 = library.find("book gasly");
        assertEquals(3, booksList2.size());
        assertTrue(booksList2.indexOf(book2) < booksList2.indexOf(book1));
        assertTrue(booksList2.indexOf(book3) < booksList2.indexOf(book1));                        
    }
    
    // query contains contiguous keyword with match,
    // query contains multiple contiguous keywords, one with 2 matches, the other with 1 match,
    // query contains one normal keyword, one contiguous keyword, where one book matches both keywords,
    //      another book matches only the contiguous keyword
    @Test
    public void testFindContiguous() {
        Library library = new BigLibrary();
        library.buy(book1);
        library.buy(book2);
        library.buy(book3);
        library.buy(book4);
        
        List<Book> booksList1 = library.find("\"kimi raikonen\"");
        assertEquals(Collections.singletonList(book4), booksList1);
        List<Book> booksList2 = library.find("\"great book\" \"good book\"");
        assertEquals(4, booksList2.size());
        List<Book> booksList3 = library.find("Hamilton \"good book\"");
        assertEquals(2, booksList3.size());
        assertTrue(booksList3.indexOf(book1) < booksList3.indexOf(book4));                      
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
