import java.util.ArrayList;

public class Member extends User{

    // 1. Class Attributes
    private ArrayList<Loan> activeLoans;

    // 2. Constructor
    public Member(String name, String surname, int age, String email, String password, Role role){
        super(name,surname,age, email, password,role);
        this.activeLoans = new ArrayList<>();
    }
    public Member(String name, String surname, int age, Role role){
        super(name,surname,age, role);
        this.activeLoans = new ArrayList<>();
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
        if(!book.isAvailable()){
            System.out.println("Book is not available. Could not borrow this book.");
            return false;
        }
        book.removeOneAvailableCopy();
        System.out.println("Book " + book.getTitle() + " has been successfully borrowed!");
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
