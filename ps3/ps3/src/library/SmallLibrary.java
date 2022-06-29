package library;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 
 * SmallLibrary represents a small collection of books, like a single person's home collection.
 */
public class SmallLibrary implements Library {

    // This rep is required! 
    // Do not change the types of inLibrary or checkedOut, 
    // and don't add or remove any other fields.
    // (BigLibrary is where you can create your own rep for
    // a Library implementation.)

    // rep
    private final Set<BookCopy> inLibrary;
    private final Set<BookCopy> checkedOut;
    
    // rep invariant:
    //    the intersection of inLibrary and checkedOut is the empty set
    //
    // abstraction function:
    //    represents the collection of books inLibrary union checkedOut,
    //      where if a book copy is in inLibrary then it is available,
    //      and if a copy is in checkedOut then it is checked out
    // Safety from rep exposure:
    //  All fields are private and final;
    //  inLibrary and checkedOut are mutable, but BookCopy is immutable
    //  and no method in SmallLibrary sets or returns any field.
    
    public SmallLibrary() {
        this.inLibrary = new HashSet<>();
        this.checkedOut = new HashSet<>();
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        boolean badRep = false;
        for (BookCopy copy : inLibrary) {
            if (checkedOut.contains(copy)) {
                badRep = true;
            }
        }
        for (BookCopy copy : checkedOut) {
            if (inLibrary.contains(copy)) {
                badRep = true;
            }
        }
        assert !badRep;
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy bookCopy = new BookCopy(book);
        inLibrary.add(bookCopy);
        checkRep();
        return bookCopy;
    }
    
    @Override
    public void checkout(BookCopy copy) {
        assert inLibrary.contains(copy);
        inLibrary.remove(copy);
        checkedOut.add(copy);
        checkRep();
    }
    
    @Override
    public void checkin(BookCopy copy) {
        assert checkedOut.contains(copy);
        checkedOut.remove(copy);
        inLibrary.add(copy);
        checkRep();
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        return inLibrary.contains(copy);
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> bookCopies = new HashSet<>(); // All copies of book in library collection
        Set<BookCopy> allLibraryCopies = new HashSet<>(inLibrary); // All copies of all books in library collection
        allLibraryCopies.addAll(checkedOut); 
        for (BookCopy copy : allLibraryCopies) {
            if (copy.getBook().equals(book)) {
                bookCopies.add(copy);
            }
        }
        checkRep();
        return bookCopies;
    }
    
    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> bookCopies = new HashSet<>();
        for (BookCopy copy : inLibrary) {
            if (copy.getBook().equals(book)) {
                bookCopies.add(copy);
            }
        }
        checkRep();
        return bookCopies;
    }

    @Override
    public List<Book> find(String query) {
        List<Book> queriedBooks = new ArrayList<Book>();
        Set<BookCopy> allLibraryCopies = new HashSet<>(inLibrary);
        allLibraryCopies.addAll(checkedOut); 
        for (BookCopy copy : allLibraryCopies) {
            Book book = copy.getBook();
            if (query.equals(book.getTitle())) {
                if (!queriedBooks.contains(book)) {
                    queriedBooks.add(book);
                }
            }
            else {
                for (String author : book.getAuthors()) {
                    if (query.equals(author)) {
                        if (!queriedBooks.contains(book)) {
                            queriedBooks.add(book);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < queriedBooks.size(); i++) {           
            for (int j = i; j < queriedBooks.size(); j++) {
                if (queriedBooks.get(i).getTitle().equals(queriedBooks.get(j).getTitle()) &&
                        queriedBooks.get(i).getAuthors().equals(queriedBooks.get(j).getAuthors())) {
                    if (queriedBooks.get(i).getYear() < queriedBooks.get(j).getYear()) {
                        Collections.swap(queriedBooks, i, j);                       
                    }
                }
            }
        }
        checkRep();
        return queriedBooks;
    }
    
    @Override
    public void lose(BookCopy copy) {
        if (inLibrary.contains(copy)) {
            inLibrary.remove(copy);
        }
        else if (checkedOut.contains(copy)) {
            checkedOut.remove(copy);
        }
        checkRep();
    }
    

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */
}
