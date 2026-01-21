import java.io.Serializable;
import java.util.List;

/**
 * Librarian class represents a library staff member with administrative privileges.
 * Can manage books, members, loans, and generate reports.
 * Has Staff membership plan with special privileges (polymorphic implementation).
 */
public class Librarian extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String employeeId;
    private Library library;

    // Constructors
    
    /**
     * Full constructor with library reference
     */
    public Librarian(String name, String surname, int age, String email, String password, 
                     String employeeId, Library library) {
        super(name, surname, age, email, password, Role.LIBRARIAN);
        this.employeeId = employeeId;
        this.library = library;
    }
    
    /**
     * Constructor without credentials
     */
    public Librarian(String name, String surname, int age, String employeeId, Library library) {
        super(name, surname, age, Role.LIBRARIAN);
        this.employeeId = employeeId;
        this.library = library;
    }
    
    // ==================== POLYMORPHIC PLAN METHODS ====================
    
    /**
     * Assign Staff plan for librarians (polymorphic - Members get different plan)
     * Staff plan includes: unlimited loans, no fees, extended periods
     */
    @Override
    protected MembershipPlan assignDefaultPlan() {
        return new MembershipPlan(MembershipPlan.PlanType.STAFF);
    }
    
    /**
     * Check if librarian can borrow more books
     * Staff can always borrow more (up to 20 books)
     */
    @Override
    public boolean canBorrowMore(int currentLoans) {
        // Staff plan allows 20 books
        return currentLoans < getMaxLoanLimit();
    }
    
    /**
     * Polymorphic callback - called after successful login
     * Shows staff-specific information
     */
    @Override
    protected void onLogin() {
        System.out.println("🔑 Staff access granted.");
        System.out.println("📊 Library Status: " + library.getTotalBooks() + " books, " +
                library.getTotalMembers() + " members, " + 
                library.getActiveLoansCount() + " active loans");
        
        // Show overdue warnings
        List<Loan> overdueLoans = library.getOverdueLoans();
        if (!overdueLoans.isEmpty()) {
            System.out.println("⚠ Alert: " + overdueLoans.size() + " overdue loan(s) in the system.");
        }
    }
    
    /**
     * Display staff privileges
     */
    public void displayStaffPrivileges() {
        System.out.println("\n========== STAFF PRIVILEGES ==========");
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Role: " + getRole());
        System.out.println("Plan: " + membershipPlan.getPlanName());
        System.out.println();
        System.out.println("Privileges:");
        System.out.println("• Borrow up to " + getMaxLoanLimit() + " books");
        System.out.println("• " + getLoanPeriodDays() + "-day loan period");
        System.out.println("• No overdue fees");
        System.out.println("• Reserve books: " + (membershipPlan.canReserveBooks() ? "Yes" : "No"));
        System.out.println("• Priority access: " + (membershipPlan.hasPriorityAccess() ? "Yes" : "No"));
        System.out.println("• Administrative access: Yes");
        System.out.println("• Extended library hours: Yes");
        System.out.println("========================================\n");
    }
    
    /**
     * Set the library reference
     */
    public void setLibrary(Library library) {
        this.library = library;
    }
    
    /**
     * Get the library reference
     */
    public Library getLibrary() {
        return library;
    }

    // ========== BOOK MANAGEMENT ==========

    /**
     * Add a new book to the library inventory
     */
    public void addBook(String isbn, String title, String author, String theme, int totalCopies) {
        try {
            Book newBook = new Book(isbn, title, author, theme, totalCopies);
            library.addBook(newBook);
            System.out.println("Book added successfully: " + title);
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }
    
    /**
     * Add an existing Book object to the library
     */
    public void addBook(Book book) {
        try {
            library.addBook(book);
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    /**
     * Remove a book from inventory
     */
    public void removeBook(String isbn) {
        try {
            library.removeBook(isbn);
        } catch (Exception e) {
            System.out.println("Error removing book: " + e.getMessage());
        }
    }

    /**
     * Update book details
     */
    public void updateBook(String isbn, String newTitle, String newAuthor, String newTheme) {
        try {
            Book book = library.findBookByISBN(isbn);
            if (newTitle != null && !newTitle.isEmpty()) {
                book.setTitle(newTitle);
            }
            if (newAuthor != null && !newAuthor.isEmpty()) {
                book.setAuthor(newAuthor);
            }
            if (newTheme != null && !newTheme.isEmpty()) {
                book.setTheme(newTheme);
            }
            System.out.println("Book updated successfully");
        } catch (Exception e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    /**
     * Add more copies of an existing book
     */
    public void addCopies(String isbn, int additionalCopies) {
        try {
            Book book = library.findBookByISBN(isbn);
            book.addCopies(additionalCopies);
        } catch (Exception e) {
            System.out.println("Error adding copies: " + e.getMessage());
        }
    }

    // ========== SEARCH & DISPLAY ==========

    /**
     * Search books by title
     */
    public List<Book> searchBooksByTitle(String title) {
        return library.searchBooksByTitle(title);
    }

    /**
     * Search books by author
     */
    public List<Book> searchBooksByAuthor(String author) {
        return library.searchBooksByAuthor(author);
    }

    /**
     * Search books by theme
     */
    public List<Book> searchBooksByTheme(String theme) {
        return library.searchBooksByTheme(theme);
    }

    /**
     * Display all books in the library
     */
    public void displayAllBooks() {
        library.displayAllBooks();
    }

    /**
     * Display only available books
     */
    public void displayAvailableBooks() {
        library.displayAvailableBooks();
    }
    
    /**
     * Check book availability by ISBN
     */
    public boolean checkBookAvailability(String isbn) {
        try {
            return library.isBookAvailable(isbn);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    // ========== MEMBER MANAGEMENT ==========

    /**
     * Register a new member
     */
    public void registerMember(String name, String surname, int age) {
        try {
            Member newMember = new Member(name, surname, age);
            library.addMember(newMember);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }
    
    /**
     * Register a new member with credentials
     */
    public void registerMember(String name, String surname, int age, String email, String password) {
        try {
            Member newMember = new Member(name, surname, age, email, password);
            library.addMember(newMember);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    /**
     * Remove a member from the system
     */
    public void removeMember(int memberId) {
        try {
            library.removeMember(memberId);
        } catch (Exception e) {
            System.out.println("Error removing member: " + e.getMessage());
        }
    }

    /**
     * Display all members
     */
    public void displayAllMembers() {
        List<Member> allMembers = library.getAllMembers();
        System.out.println("\n========== REGISTERED MEMBERS ==========");
        if (allMembers.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }
        for (Member member : allMembers) {
            System.out.println(member);
        }
        System.out.println("==========================================\n");
    }

    /**
     * View a specific member's details including borrowed books
     */
    public void viewMemberDetails(int memberId) {
        try {
            library.displayMemberDetails(memberId);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Search members by name
     */
    public List<Member> searchMembersByName(String name) {
        return library.searchMembersByName(name);
    }

    // ========== LOAN MANAGEMENT ==========

    /**
     * Issue a book to a member
     */
    public void issueBook(int memberId, String isbn) {
        try {
            library.issueLoan(memberId, isbn);
        } catch (Exception e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    /**
     * Process book return
     */
    public void returnBook(int memberId, String isbn) {
        try {
            library.returnBook(memberId, isbn);
        } catch (Exception e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    /**
     * View all active loans
     */
    public void displayAllActiveLoans() {
        List<Loan> activeLoans = library.getAllActiveLoans();
        System.out.println("\n========== ACTIVE LOANS ==========");
        if (activeLoans.isEmpty()) {
            System.out.println("No active loans.");
            return;
        }
        for (Loan loan : activeLoans) {
            System.out.println(loan);
        }
        System.out.println("===================================\n");
    }

    /**
     * View overdue loans
     */
    public void displayOverdueLoans() {
        library.displayOverdueReport();
    }

    // ========== REPORTS & STATISTICS ==========

    /**
     * Generate inventory report
     */
    public void generateInventoryReport() {
        library.displayInventorySummary();
    }

    /**
     * Generate member statistics
     */
    public void generateMemberReport() {
        library.displayMemberStatistics();
    }

    /**
     * Display detailed report of member borrowing history
     */
    public void displayMemberBorrowingHistory(int memberId) {
        try {
            Member member = library.findMemberById(memberId);
            System.out.println("\n========== BORROWING HISTORY ==========");
            System.out.println("Member: " + member.getName() + " " + member.getSurname() + 
                    " (ID: " + member.getId() + ")");
            
            List<Loan> activeLoans = library.getActiveLoansByMember(memberId);
            System.out.println("\nActive Loans (" + activeLoans.size() + "):");
            if (activeLoans.isEmpty()) {
                System.out.println("  None");
            } else {
                for (Loan loan : activeLoans) {
                    System.out.println("  - " + loan.getBook().getTitle() + 
                            " (Due: " + loan.getDueDate() + ")");
                }
            }
            
            List<Loan> history = library.getLoanHistoryByMember(memberId);
            System.out.println("\nLoan History (" + history.size() + " past loans):");
            if (history.isEmpty()) {
                System.out.println("  None");
            } else {
                for (Loan loan : history) {
                    System.out.println("  - " + loan.getBook().getTitle() + 
                            " (Returned: " + loan.getReturnDate() + ")");
                }
            }
            System.out.println("=========================================\n");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // ========== DATA PERSISTENCE ==========
    
    /**
     * Save all library data
     */
    public void saveAllData() {
        library.saveAllData();
    }
    
    /**
     * Load all library data
     */
    public void loadAllData() {
        library.loadAllData();
    }

    // Getters
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String toString() {
        return "Librarian{employeeId='" + employeeId + "', name='" + getName() + " " + 
                getSurname() + "', plan=" + (membershipPlan != null ? membershipPlan.getPlanName() : "N/A") +
                ", library='" + (library != null ? library.getLibraryName() : "N/A") + "'}";
    }
}
