import java.io.Serializable;

/**
 * Book class represents a book in the library inventory.
 * Tracks availability, copies, and book metadata.
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum BookTheme {
        FICTION,
        NON_FICTION,
        SCIENCE,
        RELIGION,
        POLITICS,
        HISTORY,
        BIOGRAPHY,
        TECHNOLOGY,
        CHILDREN,
        OTHER
    }

    // Attributes
    private String isbn;
    private String title;
    private String author;
    private BookTheme theme;
    private int totalCopies;
    private int availableCopies;

    // Constructors
    
    /**
     * Full constructor
     */
    public Book(String isbn, String title, String author, BookTheme theme, int totalCopies, int availableCopies) {
        validateIsbn(isbn);
        validateTitle(title);
        validateCopies(totalCopies, availableCopies);
        
        this.isbn = isbn;
        this.title = title;
        this.author = author != null ? author : "Unknown";
        this.theme = theme != null ? theme : BookTheme.OTHER;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }
    
    /**
     * Constructor with available copies equal to total copies
     */
    public Book(String isbn, String title, String author, BookTheme theme, int totalCopies) {
        this(isbn, title, author, theme, totalCopies, totalCopies);
    }
    
    /**
     * Constructor with theme as String
     */
    public Book(String isbn, String title, String author, String theme, int totalCopies) {
        this(isbn, title, author, parseTheme(theme), totalCopies, totalCopies);
    }

    // Validation Methods
    
    private void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be empty");
        }
    }
    
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
    }
    
    private void validateCopies(int total, int available) {
        if (total < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        if (available < 0 || available > total) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies");
        }
    }
    
    private static BookTheme parseTheme(String theme) {
        if (theme == null || theme.trim().isEmpty()) {
            return BookTheme.OTHER;
        }
        try {
            return BookTheme.valueOf(theme.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return BookTheme.OTHER;
        }
    }

    // Getters
    
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookTheme getTheme() {
        return theme;
    }
    
    /**
     * @deprecated Use getTheme() instead
     */
    @Deprecated
    public BookTheme getCategory() {
        return theme;
    }

    public int getTotalCopies() {
        return totalCopies;
    }
    
    /**
     * @deprecated Use getTotalCopies() instead
     */
    @Deprecated
    public int getNumberOfCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }
    
    /**
     * @deprecated Use getAvailableCopies() instead
     */
    @Deprecated
    public int getCopiesAvailable() {
        return availableCopies;
    }

    // Setters
    
    public void setIsbn(String isbn) {
        validateIsbn(isbn);
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author : "Unknown";
    }

    public void setTheme(BookTheme theme) {
        this.theme = theme != null ? theme : BookTheme.OTHER;
    }
    
    public void setTheme(String theme) {
        this.theme = parseTheme(theme);
    }
    
    /**
     * @deprecated Use setTheme() instead
     */
    @Deprecated
    public void setCategory(BookTheme theme) {
        this.theme = theme;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        this.totalCopies = totalCopies;
    }
    
    /**
     * @deprecated Use setTotalCopies() instead
     */
    @Deprecated
    public void setNumberOfCopies(int copies) {
        setTotalCopies(copies);
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies");
        }
        this.availableCopies = availableCopies;
    }
    
    /**
     * @deprecated Use setAvailableCopies() instead
     */
    @Deprecated
    public void setCopiesAvailable(int copies) {
        setAvailableCopies(copies);
    }

    // Book Availability Methods
    
    /**
     * Check if book is available for borrowing
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }
    
    /**
     * Borrow one copy (decreases available copies)
     */
    public void borrowCopy() {
        if (availableCopies <= 0) {
            throw new IllegalStateException("No copies available to borrow");
        }
        availableCopies--;
    }
    
    /**
     * Return one copy (increases available copies)
     */
    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException("All copies already returned");
        }
        availableCopies++;
    }
    
    /**
     * @deprecated Use borrowCopy() instead
     */
    @Deprecated
    public void removeOneAvailableCopy() {
        borrowCopy();
    }
    
    /**
     * Modify available copies by a number (positive or negative)
     */
    public void modifyAvailableCopiesBy(int modifier) {
        int newAvailable = availableCopies + modifier;
        if (newAvailable < 0) {
            System.out.println("Cannot reduce available copies below 0. " + 
                    title + " only has " + availableCopies + " available.");
            return;
        }
        if (newAvailable > totalCopies) {
            System.out.println("Cannot exceed total copies. Maximum available: " + totalCopies);
            return;
        }
        availableCopies = newAvailable;
        System.out.println(title + " now has " + availableCopies + " copies available.");
    }

    /**
     * Add copies to inventory
     */
    public void addCopies(int copies) {
        if (copies < 1) {
            throw new IllegalArgumentException("Number of copies to add must be positive");
        }
        totalCopies += copies;
        availableCopies += copies;
        System.out.println("Added " + copies + " " + (copies == 1 ? "copy" : "copies") + 
                " of '" + title + "'. Total: " + totalCopies);
    }

    /**
     * Remove copies from inventory
     */
    public void removeCopies(int copies) {
        if (copies < 1 || copies > availableCopies) {
            throw new IllegalArgumentException("Invalid number of copies to remove. " +
                    "Available: " + availableCopies);
        }
        totalCopies -= copies;
        availableCopies -= copies;
        System.out.println("Removed " + copies + " " + (copies == 1 ? "copy" : "copies") + 
                " of '" + title + "'. Remaining: " + totalCopies);
    }

    @Override
    public String toString() {
        return "Book{ISBN='" + isbn + "', title='" + title + "', author='" + author + 
               "', theme=" + theme + ", copies=" + availableCopies + "/" + totalCopies + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }
    
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
}
