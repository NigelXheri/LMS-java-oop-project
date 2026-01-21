import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Loan class represents a book loan transaction.
 * Tracks loan dates, due dates, return status, and calculates overdue fees.
 */
public class Loan implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;

    // Attributes
    private final Member member;
    private final Book book;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;

    // Constructors
    
    /**
     * Constructor with default loan period (14 days)
     */
    public Loan(Member member, Book book) {
        this(member, book, DEFAULT_LOAN_PERIOD_DAYS);
    }
    
    /**
     * Constructor with custom loan period
     */
    public Loan(Member member, Book book, int loanPeriodDays) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (loanPeriodDays < 1) {
            throw new IllegalArgumentException("Loan period must be at least 1 day");
        }
        
        this.member = member;
        this.book = book;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(loanPeriodDays);
        this.returnDate = null;
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
    
    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }
    
    // ==================== OVERDUE CALCULATIONS ====================
    
    /**
     * Check if the loan is overdue
     */
    public boolean isOverdue() {
        if (returned) {
            // If returned, check if it was returned late
            return returnDate != null && returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Get number of days overdue
     * Returns 0 if not overdue
     */
    public long getDaysOverdue() {
        if (returned) {
            if (returnDate != null && returnDate.isAfter(dueDate)) {
                return ChronoUnit.DAYS.between(dueDate, returnDate);
            }
            return 0;
        }
        
        LocalDate today = LocalDate.now();
        if (today.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, today);
        }
        return 0;
    }
    
    /**
     * Get number of days until due date
     * Returns 0 if overdue or already returned
     */
    public long getDaysUntilDue() {
        if (returned) {
            return 0;
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(dueDate) || today.isEqual(dueDate)) {
            return ChronoUnit.DAYS.between(today, dueDate);
        }
        return 0;
    }
    
    /**
     * Calculate overdue fee based on daily rate
     */
    public double calculateOverdueFee(double dailyRate) {
        if (dailyRate < 0) {
            throw new IllegalArgumentException("Daily rate cannot be negative");
        }
        long daysOverdue = getDaysOverdue();
        return daysOverdue * dailyRate;
    }

    // ==================== LOAN OPERATIONS ====================
    
    /**
     * Mark the loan as returned
     */
    public void markReturned() {
        if (!returned) {
            this.returned = true;
            this.returnDate = LocalDate.now();
            book.returnCopy();
            
            if (isOverdue()) {
                System.out.println("⚠ Book returned " + getDaysOverdue() + " days late!");
            } else {
                System.out.println("✓ Book returned on time.");
            }
        } else {
            System.out.println("This loan was already marked as returned.");
        }
    }
    
    /**
     * Extend the due date by specified days
     */
    public Loan extendLoan(int additionalDays) {
        if (returned) {
            throw new IllegalStateException("Cannot extend a returned loan");
        }
        if (isOverdue()) {
            throw new IllegalStateException("Cannot extend an overdue loan. Please return the book first.");
        }
        if (additionalDays < 1) {
            throw new IllegalArgumentException("Extension must be at least 1 day");
        }
        
        // Create a new loan with extended due date
        Loan extendedLoan = new Loan(member, book, 
                (int) ChronoUnit.DAYS.between(loanDate, dueDate.plusDays(additionalDays)));
        System.out.println("Loan extended. New due date: " + extendedLoan.getDueDate());
        return extendedLoan;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Loan{");
        sb.append("member='").append(member.getName()).append(" ").append(member.getSurname()).append("'");
        sb.append(", book='").append(book.getTitle()).append("'");
        sb.append(", loanDate=").append(loanDate);
        sb.append(", dueDate=").append(dueDate);
        
        if (returned) {
            sb.append(", returnDate=").append(returnDate);
            sb.append(", status=RETURNED");
        } else if (isOverdue()) {
            sb.append(", status=OVERDUE (").append(getDaysOverdue()).append(" days)");
        } else {
            sb.append(", status=ACTIVE (").append(getDaysUntilDue()).append(" days left)");
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Loan loan = (Loan) obj;
        return member.getId() == loan.member.getId() && 
               book.getIsbn().equals(loan.book.getIsbn()) &&
               loanDate.equals(loan.loanDate);
    }
    
    @Override
    public int hashCode() {
        int result = member.getId();
        result = 31 * result + book.getIsbn().hashCode();
        result = 31 * result + loanDate.hashCode();
        return result;
    }
}
