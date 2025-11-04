import java.util.ArrayList;

public class Member {

    // 1. Class Attributes
    private final int id;
    private final String name;
    private final String surname;
    private int age;
    private ArrayList<Loan> activeLoans;

    private static int base_id = 100;

    // 2. Constructor
    public Member(String name, String surname, int age){
        this.id  = ++base_id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.activeLoans = new ArrayList<>();
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
        return "Member: " + this.name + " " + this.surname + ", Age: " + this.age + ", ID: " + this.id;
    }

    private Loan findActiveLoanForBook(Book book) {
        for (Loan loan : activeLoans) {
            if (loan.getBook().equals(book) && !loan.isReturned()) {
                return loan;
            }
        }
        return null;
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
        Loan loan = new Loan(this, book);
        activeLoans.add(loan);
        System.out.println(this.name + " borrowed: " + book.getTitle() + " (Due: " + loan.getDueDate() + ")");
        return true;
    }

    public boolean returnBook(Book book){
        if (book == null) {
            System.out.println("Book cannot be null");
            return false;
        }
        Loan matchingLoan = findActiveLoanForBook(book);
        if (matchingLoan == null) {
            System.out.println("This book has not been borrowed");
            return false;
        }
        matchingLoan.markReturned();
        activeLoans.remove(matchingLoan);
        System.out.println("Book: " + book.getTitle() + " has been returned successfully!");
        return true;
    }



    public void displayBorrowedBooks(){
        if(activeLoans.isEmpty()){
            System.out.println("======== You don't have any borrowed books ========");
            return;
        }
        System.out.println("\n======== These are your borrowed books: ========");
        for (Loan loan : activeLoans){
            System.out.println(loan.toString());
        }
        System.out.println("======== ============================= ========\n");
    }

}
