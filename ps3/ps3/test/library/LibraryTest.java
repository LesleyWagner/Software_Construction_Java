package library;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test suite for Library ADT.
 */
@RunWith(Parameterized.class)
public class LibraryTest {

    /*
     * Note: all the tests you write here must be runnable against any
     * Library class that follows the spec.  JUnit will automatically
     * run these tests against both SmallLibrary and BigLibrary.
     */

    /**
     * Implementation classes for the Library ADT.
     * JUnit runs this test suite once for each class name in the returned array.
     * @return array of Java class names, including their full package prefix
     */
    @Parameters(name="{0}")
    public static Object[] allImplementationClassNames() {
        return new Object[] { 
            "library.SmallLibrary", 
            "library.BigLibrary"
        }; 
    }

    /**
     * Implementation class being tested on this run of the test suite.
     * JUnit sets this variable automatically as it iterates through the array returned
     * by allImplementationClassNames.
     */
    @Parameter
    public String implementationClassName;    

    /**
     * @return a fresh instance of a Library, constructed from the implementation class specified
     * by implementationClassName.
     */
    public Library makeLibrary() {
        try {
            Class<?> cls = Class.forName(implementationClassName);
            return (Library) cls.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    /*
     * Testing strategy for each operation in Library
     * ==================
     * 
     * buy
     * book: valid Book
     * 
     * checkout
     * copy: available copy, copy is already checked out
     * 
     * checkin
     * copy: checked-out book copy
     * 
     * isAvailable
     * copy: available copy, checked-out copy, copy of book not in library (not checked-out nor available)
     *
     * allCopies
     * Book: book with only available copies, book with only checked-out copies,
     * book with both available and checked-out copies, book with no copies in library 
     * 
     * availableCopies
     * Book: book with only available copies, book with only checked-out copies,
     * book with both available and checked-out copies, book with no copies in library 
     * 
     * find
     * query: query = book.getTitle() for some book with only available copies in library collection, 
     * book.getTitle() for some book with only checked out copies in library collection,
     * query = author in book.getAuthors() for some book with only available copies in library collection,
     * query = author in book.getAuthors() for some book with only checked out copies in library collection,
     * query = book.getTitle() for multiple books with copies in library with matching authors but different publication years
     * 
     * lose
     * copy: copy is checked out, copy is available
     * 
     * Covers every part
     */
    
    // Covers buy, isAvailable, allCopies, availableCopies (no checked out copies)
    @Test
    public void testLibraryNoCheckouts() {
        Library library = makeLibrary();
        Book book1 = new Book("Good book", Arrays.asList("Hamilton", "Bottas", "Alonso"), 2004);
        Book book2 = new Book("Great book", Arrays.asList("Gasly"), 2017);
        BookCopy book1copy = new BookCopy(book1);
        BookCopy book2copy = new BookCopy(book2);
        assertFalse("No book in library", library.isAvailable(book1copy));
        assertEquals(Collections.emptySet(), library.availableCopies(book1));
        assertEquals(Collections.emptySet(), library.allCopies(book2));
        
        BookCopy book1copyInLibrary = library.buy(book1);
        assertTrue("Copy in library", library.isAvailable(book1copyInLibrary));
        assertFalse("Copy not in library", library.isAvailable(book1copy));
        assertFalse("Copy not in library", library.isAvailable(book2copy));
        assertTrue("Set contains book1copyInLibrary", library.availableCopies(book1).contains(book1copyInLibrary));
        assertEquals(1, library.availableCopies(book1).size());
        assertEquals(Collections.emptySet(), library.availableCopies(book2));
        assertTrue("Set contains book1copyInLibrary", library.allCopies(book1).contains(book1copyInLibrary));
        assertEquals(1, library.allCopies(book1).size());
        assertEquals(Collections.emptySet(), library.allCopies(book2));       
    }
    
    // Covers buy, isAvailable, allCopies, availableCopies (with checked out copies)
    @Test
    public void testLibraryWithCheckouts() {
        Library library = makeLibrary();
        Book book1 = new Book("Good book", Arrays.asList("Hamilton", "Bottas", "Alonso"), 2004);
        Book book2 = new Book("Great book", Arrays.asList("Gasly"), 2017);
        BookCopy book1copy1 = library.buy(book1);
        BookCopy book1copy2 = library.buy(book1);
        BookCopy book1copy3 = library.buy(book1);
        BookCopy book2copy1 = library.buy(book2);
        BookCopy book2copy2 = library.buy(book2);
        BookCopy book2copy3 = library.buy(book2);
        
        library.checkout(book1copy1);
        assertFalse("Copy not in library", library.isAvailable(book1copy1));
        assertTrue("Copy in library", library.isAvailable(book1copy2));
        assertTrue("Set contains book1copy2", library.availableCopies(book1).contains(book1copy2));
        assertTrue("Set contains book1copy3", library.availableCopies(book1).contains(book1copy3));
        assertEquals(2, library.availableCopies(book1).size());
        assertTrue("Set contains book1copy1", library.allCopies(book1).contains(book1copy1));
        assertTrue("Set contains book1copy2", library.allCopies(book1).contains(book1copy2));
        assertTrue("Set contains book1copy3", library.allCopies(book1).contains(book1copy3));
        assertEquals(3, library.allCopies(book1).size());
        
        library.checkout(book1copy2);
        library.checkout(book1copy3);
        library.checkin(book1copy3);        
        assertTrue("Copy in library", library.isAvailable(book1copy3));
        assertFalse("Copy not in library", library.isAvailable(book1copy2));
        assertTrue("Set contains book1copy3", library.availableCopies(book1).contains(book1copy3));
        assertEquals(1, library.availableCopies(book1).size());
        assertTrue("Set contains book1copy1", library.allCopies(book1).contains(book1copy1));
        assertTrue("Set contains book1copy2", library.allCopies(book1).contains(book1copy2));
        assertTrue("Set contains book1copy3", library.allCopies(book1).contains(book1copy3));
        assertEquals(3, library.allCopies(book1).size());
        
        library.checkout(book1copy3);
        assertFalse("Copy not in library", library.isAvailable(book1copy3));
        assertFalse("Copy not in library", library.isAvailable(book1copy1));
        assertEquals(Collections.emptySet(), library.availableCopies(book1));
        assertTrue("Set contains book1copy1", library.allCopies(book1).contains(book1copy1));
        assertTrue("Set contains book1copy2", library.allCopies(book1).contains(book1copy2));
        assertTrue("Set contains book1copy3", library.allCopies(book1).contains(book1copy3));
        assertEquals(3, library.allCopies(book1).size());
        
        assertTrue("Copy in library", library.isAvailable(book2copy2));
        assertTrue("Set contains book2copy1", library.availableCopies(book2).contains(book2copy1));
        assertTrue("Set contains book2copy2", library.availableCopies(book2).contains(book2copy2));
        assertTrue("Set contains book2copy3", library.availableCopies(book2).contains(book2copy3));
        assertEquals(3, library.availableCopies(book2).size());
        assertTrue("Set contains book2copy1", library.allCopies(book2).contains(book2copy1));
        assertTrue("Set contains book2copy2", library.allCopies(book2).contains(book2copy2));
        assertTrue("Set contains book2copy3", library.allCopies(book2).contains(book2copy3));
        assertEquals(3, library.allCopies(book2).size());    
    }
    
    // Covers find
    @Test
    public void testLibraryFind() {
        Library library = makeLibrary();
        Book book1 = new Book("Good book", Arrays.asList("Hamilton", "Bottas", "Alonso"), 2004);
        Book book2 = new Book("Great book", Arrays.asList("Gasly"), 2010);
        Book book3 = new Book("Great book", Arrays.asList("Gasly"), 2017);
        library.buy(book1);
        library.buy(book1);
        library.buy(book1);
        library.buy(book2);
        library.buy(book2);
        library.buy(book2);  
        library.buy(book3);
        
        assertTrue("List contains book1", library.find(book1.getTitle()).contains(book1));
        assertTrue("List contains book1", library.find(book1.getAuthors().get(0)).contains(book1));
        assertTrue("List contains book2", library.find(book2.getTitle()).contains(book2));
        assertTrue("List contains book3", library.find(book2.getTitle()).contains(book3));
        assertTrue("book3 is earlier in the list", 
                library.find(book2.getTitle()).indexOf(book3) < library.find(book2.getTitle()).indexOf(book2));
        assertTrue("List contains book2", library.find(book2.getAuthors().get(0)).contains(book2));
        assertTrue("List contains book3", library.find(book2.getAuthors().get(0)).contains(book3));
        assertTrue("book3 is earlier in the list", 
                library.find(book2.getAuthors().get(0)).indexOf(book3) < library.find(book2.getAuthors().get(0)).indexOf(book2));
    }
    
    // Covers lose
    @Test
    public void testLibraryLose() {
        Library library = makeLibrary();
        Book book1 = new Book("Good book", Arrays.asList("Hamilton", "Bottas", "Alonso"), 2004);
        Book book2 = new Book("Great book", Arrays.asList("Gasly"), 2017);
        BookCopy book1copy1 = library.buy(book1);
        BookCopy book1copy2 = library.buy(book1);
        BookCopy book2copy = library.buy(book2); 
        library.checkout(book1copy1);
        
        library.lose(book1copy1);
        assertFalse("book1copy1 not in library", library.isAvailable(book1copy1));
        assertFalse("Set does not contain book1copy1", library.availableCopies(book1).contains(book1copy1));
        assertTrue("Set does contain book1copy2", library.availableCopies(book1).contains(book1copy2));
        assertEquals(1, library.availableCopies(book1).size());
        assertFalse("Set does not contain book1copy1", library.availableCopies(book1).contains(book1copy1));
        assertTrue("Set does contain book1copy2", library.availableCopies(book1).contains(book1copy2));
        assertEquals(1, library.allCopies(book1).size());
        
        library.lose(book2copy);
        assertFalse("book2copy not in library", library.isAvailable(book1copy1));
        assertFalse("Set does not contain book2copy", library.availableCopies(book2).contains(book2copy));
        assertFalse("Set does not contain book2copy", library.allCopies(book2).contains(book2copy));
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
