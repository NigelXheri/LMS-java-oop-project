import java.time.LocalDate;

public class Loan {

    private final Member member;
    private final Book book;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private boolean returned;

    // Constructor
    public Loan(Member member, Book book) {
        this.member = member;
        this.book = book;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(14); // Books need to be returned within 14 days.
        this.returned = false;
    }

    // Getters
    public Member getMember() {
        return member;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    @Override
    public String toString() {
        return "Loan { " +
                "Member=" + member.getName() + " " + member.getSurname() +
                ", Book='" + book.getTitle() + '\'' +
                ", LoanDate=" + loanDate +
                ", DueDate=" + dueDate +
                ", Returned=" + returned +
                " }";
    }

    // Other Methods
    public void markReturned() {
        if (!returned) {
            if (isOverdue()) System.out.println("Loan was overdue!");
            returned = true;
            book.modifyAvailableCopiesBy(1);
            System.out.println("Loan completed: " + book.getTitle() + " returned by " + member.getName());
        } else {
            System.out.println("This loan was already marked as returned.");
        }
    }

    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

}
