public class Book {

    // Class atributes
    private int id_Book;
    private int quantity;
    private String title;
    private String author;
    private String type;
    private String isbn;
    private int nr_of_copies;
    private int copies_available;

    // Constructor of the class
    public Book(int id_Book, int quantity, String title, String author, String type, String isbn, int nr_of_copies, int copies_available) {
        this.id_Book = id_Book;
        this.quantity = quantity;
        this.title = title;
        this.author = author;
        this.type = type;
        this.isbn = isbn;
        this.nr_of_copies = nr_of_copies;
        this.copies_available = copies_available;
    }
}
