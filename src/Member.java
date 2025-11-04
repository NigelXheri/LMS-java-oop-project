import java.util.ArrayList;

public class Member {

    // 1. Class Attributes
    private final int id;
    private final String name;
    private final String surname;
    private int age;
    private ArrayList<Book> borrowedBooks;

    private static int base_id = 100;

    // 2. Constructor
    public Member(String name, String surname, int age){
        this.id  = ++base_id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.borrowedBooks = new ArrayList<>();
    }

    // 3.1 GET Methods
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getSurname(){
        return this.surname;
    }
    public int getAge(){
        return this.age;
    }

    // 3.2 SET Methods
    public void setAge(int age) {
        this.age = age;
    }

    // 4. toString()

    @Override
    public String toString() {
        return "Student: " + this.name + " " + this.surname + ", Age: " + this.age + ", ID: " + this.id;
    }

    public boolean borrowBook(Book book){
        if(book == null){
            System.out.println("Book cannot be null");
            return false;
        }
        if(!book.borrowBook()){
            System.out.println("Book is not available. Could not borrow this book.");
            return false;
        }
        borrowedBooks.add(book);
        return true;
    }

    public void displayBorrowedBooks(){
        if(borrowedBooks.isEmpty()){
            System.out.println("You don't have any borrowed books");
            return;
        }
        System.out.println("======== These are your borrowed books: ========");
        for (Book book : borrowedBooks){
            System.out.println(book.toString());
        }
    }


}
