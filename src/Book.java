public class Book {

    // Class atributes
    private int idBook;
    private int quantity;
    private String title;
    private String author;
    private String type;
    private String Isbn;
    private int numberOfCopies;
    private int copiesAvailable;

    // Constructor of the class
    public Book(int idBook, int quantity, String title, String author, String type, String Isbn, int nr0fCopies, int copiesAvailable) {
        this.idBook = idBook;
        this.quantity = quantity;
        this.title = title;
        this.author = author;
        this.type = type;
        this.Isbn = Isbn;
        this.numberOfCopies = numberOfCopies;
        this.copiesAvailable = copiesAvailable;
    }
    // Getters and Setters for all the atributes

    public int getIdBook() {
        return idBook;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getType() {
        return type;
    }
    public String getIsbn() {
        return Isbn;
    }
    public int getNumberOfCopies() {
        return numberOfCopies;
    }
    public int getCopiesAvailable() {
        return copiesAvailable;
    }


    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsbn(String isbn) {
        this.Isbn = Isbn;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    //String toString

    public String toString() {
        return "Book {" +
                "idBook=" + idBook +
                ", quantity=" + quantity +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", type='" + type + '\'' +
                ", Isbn='" + Isbn + '\'' +
                ", numberOfCopies=" + numberOfCopies +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }

    // Methods

    // 1. Borrow a book
    public boolean borrowBook() {
        if (copiesAvailable > 0) {
            copiesAvailable--;
            return true;
        } else {
            return false;
        }
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
