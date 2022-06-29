package library;

/**
 * BookCopy is a mutable type representing a particular copy of a book that is held in a library's
 * collection.
 */
public class BookCopy {

    private final Book book;
    private Condition condition;
    
    // Rep invariant:
    //  book can be all valid books of Book
    //  condition can be all valid conditions of Condition
    // Abstraction function:
    //  represents particular copy of a book in good or bad condition
    // Safety from rep exposure:
    //  All fields are private and immutable
    
    public static enum Condition {
        GOOD, DAMAGED
    };
    
    /**
     * Make a new BookCopy, initially in good condition.
     * @param book the Book of which this is a copy
     */
    public BookCopy(Book book) {
        this.book = book;
        this.condition = Condition.GOOD;
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        assert this.book != null;
        assert this.condition != null;
    }
    
    /**
     * @return the Book of which this is a copy
     */
    public Book getBook() {
        return this.book;
    }
    
    /**
     * @return the condition of this book copy
     */
    public Condition getCondition() {
        return this.condition;
    }

    /**
     * Set the condition of a book copy.  This typically happens when a book copy is returned and a librarian inspects it.
     * @param condition the latest condition of the book copy
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }
    
    /**
     * @return human-readable representation of this book that includes book.toString()
     *    and the words "good" or "damaged" depending on its condition
     */
    public String toString() {
        return "{ " + book.toString() + ", " + condition.toString().toLowerCase() + " }";     
    }


    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
