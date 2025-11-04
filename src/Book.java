public class Book {

    public enum BookTheme {
        FICTION,
        NON_FICTION,
        SCIENCE,
        RELIGION,
        POLITICS
    }

    private String isbn;
    private String title;
    private String author;
    private BookTheme bookTheme;
    private int numberOfCopies;
    private int copiesAvailable;

    // Constructor of the class
    public Book(String isbn, String title, String author, BookTheme bookTheme, int numberOfCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookTheme = bookTheme;
        this.numberOfCopies = numberOfCopies;
        this.copiesAvailable = numberOfCopies;
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public BookTheme getCategory() {
        return bookTheme;
    }
    public String getIsbn() {
        return isbn;
    }
    public int getNumberOfCopies() {
        return numberOfCopies;
    }
    public int getCopiesAvailable() {
        return copiesAvailable;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(BookTheme bookTheme) {
        this.bookTheme = bookTheme;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    //String toString
    public String toString() {
        return "Book { " +
                "ISBN='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + bookTheme + '\'' +
                ", numberOfCopies=" + numberOfCopies +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }


    // Methods

    // 1. Borrow a book
    public boolean borrowBook() {
        if (isAvailable()) {
            copiesAvailable--;
            System.out.println("Book " + this.title + " has been successfully borrowed!"); // IMPLEMENT BOOK BORROWING
            return true;
        }
        return false;
    }

    // 2. Return a book (increase available copies)
    public void returnBook() {
        if (copiesAvailable < numberOfCopies) {
            copiesAvailable++;
        }
    }

    // 3. Check if book is available
    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    // 4. Add copies to inventory
    public void addCopies(int amount) {
        if (amount > 0) {
            numberOfCopies += amount;
            copiesAvailable += amount;
        }
    }
}
