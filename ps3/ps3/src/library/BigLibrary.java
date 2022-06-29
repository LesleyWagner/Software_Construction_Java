package library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BigLibrary represents a large collection of books that might be held by a city or
 * university library system -- millions of books.
 * 
 * In particular, every operation needs to run faster than linear time (as a function of the number of books
 * in the library).
 */
public class BigLibrary implements Library {

    // rep
    private final Map<Book, Set<BookCopy>> copiesOfAllBooks;
    private final Map<Book, Set<BookCopy>> copiesOfAvailableBooks;
    private final Map<String, Set<Book>> booksByKeyword; // contains duplicate books
    
    // rep invariant:
    //    copiesOfAvailable books is a submap of copiesOfAllBooks,
    //    The concatenation of all values (sets) of booksByKeywords
    //    is a subset of the set of keys of copiesOfAllBooks
    //
    // abstraction function:
    //    represents the collection of books copiesOfAllBooks,
    //      where if a book copy is in copiesOfAllBooks and in copiesOfAvailableBooks then it is available,
    //      and if a copy is in copiesOfAllBooks but not in copiesOfAvailableBooks then it is checked out.
    //    booksByAuthor and booksByTitle are redundant from the perspective of the abstraction function,
    //    but is implemented for performance reasons.
    // Safety from rep exposure:
    //  All fields are private and final,
    //  uses defensive copying for allCopies and availableCopies,
    //  find returns copies of Books collections,
    //  and BookCopy is immutable
    
    public BigLibrary() {    
        this.copiesOfAllBooks = new HashMap<>();
        this.copiesOfAvailableBooks = new HashMap<>();
        this.booksByKeyword = new HashMap<>();
        checkRep();
    }
    
    // assert the rep invariant
    private void checkRep() {
        // copiesOfAvailable books is a submap of copiesOfAllBooks
        for (Map.Entry<Book, Set<BookCopy>> entry : copiesOfAvailableBooks.entrySet()) {
            Set<BookCopy> allCopiesOfBook = copiesOfAllBooks.get(entry.getKey());
            assert allCopiesOfBook.containsAll(entry.getValue());
        }
        Set<Book> booksByKeywordValues = new HashSet<>();
        for (Set<Book> books : booksByKeyword.values()) {
            booksByKeywordValues.addAll(books);
        }
        assert copiesOfAllBooks.keySet().containsAll(booksByKeywordValues);
    }

    @Override
    public BookCopy buy(Book book) {
        BookCopy bookCopy = new BookCopy(book);
        if (copiesOfAllBooks.containsKey(book)) {
            copiesOfAllBooks.get(book).add(bookCopy);
            copiesOfAvailableBooks.get(book).add(bookCopy);
        }
        else {           
            Set<BookCopy> copiesOfBook = new HashSet<>();
            copiesOfBook.add(bookCopy);
            copiesOfAllBooks.put(book, copiesOfBook);
            copiesOfAvailableBooks.put(book, new HashSet<>(copiesOfBook));
        }
                        
        String authors = "";        
        for (String author : book.getAuthors()) {
            authors += author + " ";
        }
        String[] authorWords = authors.toLowerCase().trim().split(" ");
        String[] titleWords = book.getTitle().toLowerCase().split(" "); 
        List<String> keywords = new ArrayList<>(Arrays.asList(titleWords));
        Collections.addAll(keywords, authorWords);
        for (String keyword : keywords) {
            keyword = keyword.replaceFirst("^\\W+", "").replaceFirst("\\W+$", ""); // remove non-word characters before and after keyword
            if (booksByKeyword.containsKey(keyword)) {
                booksByKeyword.get(keyword).add(book);
            }
            else {
                Set<Book> keywordsSet = new HashSet<>();
                keywordsSet.add(book);  
                booksByKeyword.put(keyword, keywordsSet);
            }
        }
        checkRep();
        return bookCopy;
    }
    
    @Override
    public void checkout(BookCopy copy) {
        Book book = copy.getBook();   
        assert copiesOfAvailableBooks.get(book).contains(copy);       
        copiesOfAvailableBooks.get(book).remove(copy);
        checkRep();
    }
    
    @Override
    public void checkin(BookCopy copy) {
        Book book = copy.getBook();
        assert copiesOfAllBooks.get(book).contains(copy) && !copiesOfAvailableBooks.get(book).contains(copy);   
        copiesOfAvailableBooks.get(book).add(copy);
        checkRep();
    }
    
    @Override
    public Set<BookCopy> allCopies(Book book) {
        Set<BookCopy> books = new HashSet<>();
        if (copiesOfAllBooks.containsKey(book)) {
            books.addAll(copiesOfAllBooks.get(book));
        }
        return books;
    }

    @Override
    public Set<BookCopy> availableCopies(Book book) {
        Set<BookCopy> books = new HashSet<>();
        if (copiesOfAvailableBooks.containsKey(book)) {
            books.addAll(copiesOfAvailableBooks.get(book));
        }
        return books;
    }
    
    @Override
    public boolean isAvailable(BookCopy copy) {
        boolean available = false;
        Book book = copy.getBook();
        if (copiesOfAvailableBooks.containsKey(book) && 
                copiesOfAvailableBooks.get(copy.getBook()).contains(copy)) {
            available = true;
        }
        return available;
    }
    
    /**
     * Search for books in this library's collection.
     * @param query search string
     * @return list of books in this library's collection (both available and checked out) 
     * for which a book's title or author (or part of) match the search string,
     * ordered by decreasing amount of match, then by availability (available first), then by year (newer first).
     * A book should appear at most once on the list, capitalisation is ignored. 
     * Keyword matching and ranking must support: 
     *     - exact matching of title and author: i.e., if a copy of a book is in the library's 
     *           collection, then find(book.getTitle()) and find(book.getAuthors().get(i)) 
     *           must include book among the results.
     *     - exact matching of words and contiguous words in title and author.
     *     - support for quotation marks in, so that "\"David Foster Wallace\" \"Infinite Jest\""
     *           finds books whose title or author contains David Foster Wallace or Infinite Jest
     *           as contiguous words. 
     *     - ranks the resulting list so that books that match multiple keywords or multiple contiguous keywords
     *           appear earlier in the list.
     *     - ranks the resulting list so that checked out books appear lower in the list.    
     *     - date ordering: if two matching books have the same title and author but different
     *           publication dates, then the newer book should appear earlier on the list. 
    */
    @Override
    public List<Book> find(String query) {
        List<Book> queriedBooks = new ArrayList<>();
        Map<Book, Integer> bookRanking = new HashMap<>();
        List<String> queryWords = new ArrayList<>();
        // Split query by spaces, but keep spaces inside double quotes
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(query.toLowerCase());
        while (matcher.find())
            queryWords.add(matcher.group(1).replace("\"", ""));
        
        for (String keyword : queryWords) {
            keyword = keyword.replaceFirst("^\\W+", "").replaceFirst("\\W+$", "");
            
            // Searches matching books for contiguous keywords. Book must contain all words in the contiguous term
            // to be a match.
            if (keyword.contains(" ")) {
                Set<Book> matchingBooks = new HashSet<>();
                String[] contiguousWords = keyword.split(" ");
                String firstWord = contiguousWords[0];
                firstWord = firstWord.replaceFirst("^\\W+", "").replaceFirst("\\W+$", "");
                if (booksByKeyword.containsKey(firstWord)) {
                    matchingBooks.addAll(booksByKeyword.get(firstWord));
                }                
                for (int i = 1; i < contiguousWords.length; i++) {
                    String word = contiguousWords[i];
                    word = word.replaceFirst("^\\W+", "").replaceFirst("\\W+$", "");
                    if (booksByKeyword.containsKey(word)) {
                        for (Book book : matchingBooks) {
                            if (!booksByKeyword.get(word).contains(book)) {
                                matchingBooks.remove(book);
                            }
                        }
                    }
                }               
                for (Book book : matchingBooks) {
                    if (bookRanking.containsKey(book)) {
                        bookRanking.put(book, bookRanking.get(book)+2);
                    }
                    else {
                        bookRanking.put(book, 2);
                    }
                }
            }
            // normal keywords (non-contiguous)
            else {
                if (booksByKeyword.containsKey(keyword)) {
                    for (Book book : booksByKeyword.get(keyword)) {
                        if (bookRanking.containsKey(book)) {
                            bookRanking.put(book, bookRanking.get(book)+1);
                        }
                        else {
                            bookRanking.put(book, 1);
                        }
                    }
                }
            }
        }
        // First sort books by year, then by availability, then by ranking (match) (least to most important)
        queriedBooks = new ArrayList<>(bookRanking.keySet());    
        Comparator<Book> bookComparer = Comparator.comparing(book -> book.getYear());
        queriedBooks.sort(bookComparer.reversed());
        bookComparer = Comparator.comparing(book -> copiesOfAvailableBooks.get(book).size() > 0);
        queriedBooks.sort(bookComparer.reversed());
        bookComparer = Comparator.comparing(book -> bookRanking.get(book));
        queriedBooks.sort(bookComparer.reversed());
        
        return queriedBooks;
    }
    
    @Override
    public void lose(BookCopy copy) {
        Book book = copy.getBook();      
        assert copiesOfAllBooks.get(book).contains(copy);
        copiesOfAllBooks.get(book).remove(copy);
        if (copiesOfAllBooks.get(book).isEmpty()) {
            String authors = "";        
            for (String author : book.getAuthors()) {
                authors += author + " ";
            }
            String[] authorWords = authors.toLowerCase().trim().split(" ");
            String[] titleWords = book.getTitle().toLowerCase().split(" "); 
            List<String> keywords = new ArrayList<>(Arrays.asList(titleWords));
            Collections.addAll(keywords, authorWords);
            for (String keyword : keywords) {
                keyword = keyword.replaceFirst("^\\W+", "").replaceFirst("\\W+$", "");
                booksByKeyword.get(keyword).remove(book);
            }
        }
        if (copiesOfAvailableBooks.get(book).contains(copy)) {
            copiesOfAvailableBooks.get(book).remove(copy);
        }
        checkRep();
    }

    /* Copyright (c) 2016 MIT 6.005 course staff, all rights reserved.
     * Redistribution of original or derived work requires explicit permission.
     * Don't post any of this code on the web or to a public Github repository.
     */

}
