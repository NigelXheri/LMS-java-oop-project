import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Member class represents a library member who can borrow books.
 * Tracks active loans, borrowing history, and overdue fees.
 * Implements polymorphic plan assignment for member-specific plans.
 */
public class Member extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    // Attributes
    private List<Loan> activeLoans;
    private List<Loan> loanHistory;
    private double accumulatedFees;

    // Constructors
    
    /**
     * Full constructor with email and password
     */
    public Member(String name, String surname, int age, String email, String password) {
        super(name, surname, age, email, password, Role.MEMBER);
        this.activeLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.accumulatedFees = 0.0;
    }

    /**
     * Basic constructor without credentials
     */
    public Member(String name, String surname, int age) {
        super(name, surname, age, Role.MEMBER);
        this.activeLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.accumulatedFees = 0.0;
    }

    // ==================== POLYMORPHIC PLAN METHODS ====================
    
    /**
     * Assign default membership plan for members (Basic plan)
     * This is a polymorphic method - Librarians get a different plan
     */
    @Override
    protected MembershipPlan assignDefaultPlan() {
        return new MembershipPlan(MembershipPlan.PlanType.BASIC);
    }
    
    /**
     * Check if member can borrow more books based on their plan
     */
    @Override
    public boolean canBorrowMore(int currentLoans) {
        if (membershipPlan == null) return currentLoans < 3;
        return currentLoans < membershipPlan.getMaxLoans();
    }
    
    /**
     * Upgrade membership plan
     */
    public void upgradePlan(MembershipPlan.PlanType newPlanType) {
        if (newPlanType == MembershipPlan.PlanType.STAFF) {
            System.out.println("Error: Members cannot upgrade to Staff plan.");
            return;
        }
        if (membershipPlan != null) {
            membershipPlan.upgradePlan(newPlanType);
        } else {
            this.membershipPlan = new MembershipPlan(newPlanType);
        }
        System.out.println(name + " " + surname + " upgraded to " + newPlanType.getDisplayName());
    }
    
    /**
     * Change membership plan
     */
    public void changePlan(MembershipPlan.PlanType newPlanType) {
        if (newPlanType == MembershipPlan.PlanType.STAFF) {
            System.out.println("Error: Members cannot have Staff plan.");
            return;
        }
        if (membershipPlan != null) {
            membershipPlan.changePlan(newPlanType);
        } else {
            this.membershipPlan = new MembershipPlan(newPlanType);
        }
    }
    
    /**
     * Polymorphic callback - called after successful login
     */
    @Override
    protected void onLogin() {
        System.out.println("📚 You have " + activeLoans.size() + " active loan(s).");
        if (hasOverdueBooks()) {
            System.out.println("⚠ Warning: You have overdue books! Please return them soon.");
        }
        if (membershipPlan != null && membershipPlan.isExpiringSoon()) {
            System.out.println("⚠ Your membership expires in " + 
                    membershipPlan.getDaysUntilExpiry() + " days. Consider renewing!");
        }
    }

    // ==================== LOAN MANAGEMENT ====================
    
    /**
     * Get all active loans
     */
    public List<Loan> getActiveLoans() {
        return new ArrayList<>(activeLoans);
    }
    
    /**
     * @deprecated Use getActiveLoans() instead
     */
    @Deprecated
    public List<Loan> getBorrowedBooks() {
        return getActiveLoans();
    }
    
    /**
     * Get loan history
     */
    public List<Loan> getLoanHistory() {
        return new ArrayList<>(loanHistory);
    }
    
    /**
     * Add a loan (called by Library when book is issued)
     */
    public void addLoan(Loan loan) {
        if (loan != null && !activeLoans.contains(loan)) {
            activeLoans.add(loan);
        }
    }
    
    /**
     * Remove a loan (called by Library when book is returned)
     */
    public void removeLoan(Loan loan) {
        if (loan != null) {
            activeLoans.remove(loan);
            loanHistory.add(loan);
        }
    }
    
    /**
     * Find an active loan for a specific book
     */
    private Loan findActiveLoanForBook(Book book) {
        for (Loan loan : activeLoans) {
            if (loan.getBook().equals(book) && !loan.isReturned()) {
                return loan;
            }
        }
        return null;
    }

    /**
     * Borrow a book directly (simplified method for demos)
     */
    public boolean borrowBook(Book book) {
        if (book == null) {
            System.out.println("Error: Book cannot be null");
            return false;
        }
        if (!book.isAvailable()) {
            System.out.println("Error: '" + book.getTitle() + "' is not available");
            return false;
        }
        
        // Check loan limit based on membership plan
        if (!canBorrowMore(activeLoans.size())) {
            System.out.println("Error: You have reached your loan limit (" + 
                    getMaxLoanLimit() + " books for " + membershipPlan.getPlanName() + ")");
            return false;
        }
        
        // Check if already borrowed this book
        if (findActiveLoanForBook(book) != null) {
            System.out.println("Error: You already have this book on loan");
            return false;
        }
        
        // Create loan with plan-specific loan period
        book.borrowCopy();
        Loan loan = new Loan(this, book, getLoanPeriodDays());
        activeLoans.add(loan);
        
        System.out.println("Success: Borrowed '" + book.getTitle() + "'");
        System.out.println("Due date: " + loan.getDueDate() + " (" + getLoanPeriodDays() + " day loan period)");
        return true;
    }

    /**
     * Return a book
     */
    public boolean returnBook(Book book) {
        if (book == null) {
            System.out.println("Error: Book cannot be null");
            return false;
        }
        
        Loan matchingLoan = findActiveLoanForBook(book);
        if (matchingLoan == null) {
            System.out.println("Error: You don't have this book on loan");
            return false;
        }
        
        // Calculate and add any overdue fees using plan's fee rate
        double overdueFee = matchingLoan.calculateOverdueFee(getDailyOverdueFee());
        if (overdueFee > 0) {
            accumulatedFees += overdueFee;
            System.out.println("Overdue fee added: $" + String.format("%.2f", overdueFee) + 
                    " (Rate: $" + String.format("%.2f", getDailyOverdueFee()) + "/day)");
        }
        
        // Process return
        matchingLoan.markReturned();
        activeLoans.remove(matchingLoan);
        loanHistory.add(matchingLoan);
        
        System.out.println("Success: Returned '" + book.getTitle() + "'");
        return true;
    }

    // ==================== FEE CALCULATION ====================
    
    /**
     * Calculate total current overdue fees from active loans
     * Uses the member's plan-specific daily fee rate
     */
    public double calculateTotalOverdueFees() {
        double totalFees = accumulatedFees;
        double feeRate = getDailyOverdueFee();
        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                totalFees += loan.calculateOverdueFee(feeRate);
            }
        }
        return totalFees;
    }
    
    /**
     * Calculate overdue fees from active loans only (not accumulated)
     * Uses the member's plan-specific daily fee rate
     */
    public double calculateCurrentOverdueFees() {
        double currentFees = 0.0;
        double feeRate = getDailyOverdueFee();
        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                currentFees += loan.calculateOverdueFee(feeRate);
            }
        }
        return currentFees;
    }
    
    /**
     * Get accumulated fees (from returned books)
     */
    public double getAccumulatedFees() {
        return accumulatedFees;
    }
    
    /**
     * Pay fees
     */
    public void payFees(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Payment amount must be positive");
            return;
        }
        if (amount > accumulatedFees) {
            System.out.println("Payment of $" + String.format("%.2f", accumulatedFees) + 
                    " accepted. Change: $" + String.format("%.2f", amount - accumulatedFees));
            accumulatedFees = 0;
        } else {
            accumulatedFees -= amount;
            System.out.println("Payment of $" + String.format("%.2f", amount) + " accepted.");
            System.out.println("Remaining fees: $" + String.format("%.2f", accumulatedFees));
        }
    }
    
    /**
     * Check if member has any overdue books
     */
    public boolean hasOverdueBooks() {
        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get count of overdue books
     */
    public int getOverdueBookCount() {
        int count = 0;
        for (Loan loan : activeLoans) {
            if (loan.isOverdue()) {
                count++;
            }
        }
        return count;
    }

    // ==================== DISPLAY METHODS ====================
    
    /**
     * Display all borrowed books with due dates
     */
    public void displayBorrowedBooks() {
        if (activeLoans.isEmpty()) {
            System.out.println("\n========== NO ACTIVE LOANS ==========");
            System.out.println("You have no books currently on loan.");
            return;
        }
        
        System.out.println("\n========== YOUR BORROWED BOOKS ==========");
        System.out.println("Membership: " + membershipPlan.getPlanName() + 
                " (Max: " + getMaxLoanLimit() + " books)");
        System.out.println("-".repeat(42));
        
        for (Loan loan : activeLoans) {
            System.out.println("• " + loan.getBook().getTitle());
            System.out.println("  Borrowed: " + loan.getLoanDate());
            System.out.println("  Due: " + loan.getDueDate());
            if (loan.isOverdue()) {
                System.out.println("  ⚠ OVERDUE by " + loan.getDaysOverdue() + " days!");
                System.out.println("  Fee: $" + String.format("%.2f", 
                        loan.calculateOverdueFee(getDailyOverdueFee())));
            }
            System.out.println();
        }
        System.out.println("Total active loans: " + activeLoans.size() + "/" + getMaxLoanLimit());
        
        double totalFees = calculateTotalOverdueFees();
        if (totalFees > 0) {
            System.out.println("Total fees owed: $" + String.format("%.2f", totalFees));
        }
        System.out.println("==========================================\n");
    }
    
    /**
     * Display fee summary
     */
    public void displayFeeSummary() {
        System.out.println("\n========== FEE SUMMARY ==========");
        System.out.println("Member: " + getName() + " " + getSurname() + " (ID: " + getId() + ")");
        System.out.println("Plan: " + membershipPlan.getPlanName() + 
                " (Fee rate: $" + String.format("%.2f", getDailyOverdueFee()) + "/day)");
        System.out.println("Accumulated fees (past returns): $" + 
                String.format("%.2f", accumulatedFees));
        System.out.println("Current overdue fees: $" + 
                String.format("%.2f", calculateCurrentOverdueFees()));
        System.out.println("Total fees owed: $" + 
                String.format("%.2f", calculateTotalOverdueFees()));
        System.out.println("==================================\n");
    }

    @Override
    public String toString() {
        return "Member{id=" + getId() + ", name='" + getName() + " " + getSurname() + 
               "', plan=" + (membershipPlan != null ? membershipPlan.getPlanName() : "None") +
               ", activeLoans=" + activeLoans.size() + "/" + getMaxLoanLimit() + 
               ", fees=$" + String.format("%.2f", calculateTotalOverdueFees()) + "}";
    }
}
