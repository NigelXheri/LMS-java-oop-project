import java.util.List;

/**
 * Main class - Demonstration of the Library Management System
 * Shows all features including book management, member management,
 * loan processing, overdue fees, membership plans, authentication, and data persistence.
 */
public class Main {

    public static void main(String[] args) {
        printWelcomeBanner();
        
        // ==================== LIBRARY INITIALIZATION ====================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           LIBRARY MANAGEMENT SYSTEM DEMO");
        System.out.println("=".repeat(60));
        
        // Create a new library
        Library library = new Library("City Public Library");
        System.out.println("\n✓ Created: " + library.getLibraryName());
        
        // ==================== MEMBERSHIP PLANS OVERVIEW ====================
        System.out.println("\n" + "-".repeat(60));
        System.out.println("              MEMBERSHIP PLANS OVERVIEW");
        System.out.println("-".repeat(60));
        
        // Display all available plans
        MembershipPlan.comparePlans();
        
        // ==================== LIBRARIAN SETUP (with credentials) ====================
        System.out.println("-".repeat(60));
        System.out.println("              LIBRARIAN REGISTRATION");
        System.out.println("-".repeat(60));
        
        // Create librarian WITH credentials for login
        Librarian librarian = new Librarian("Sarah", "Johnson", 35, 
                "sarah@library.com", "admin123", "EMP001", library);
        library.addLibrarian(librarian);
        
        System.out.println("\nLibrarian account created:");
        System.out.println("  Email: sarah@library.com");
        System.out.println("  Plan: " + librarian.getMembershipPlan().getPlanName());
        
        // Show librarian's staff privileges
        librarian.displayStaffPrivileges();

        // ==================== BOOK INVENTORY SETUP ====================
        System.out.println("-".repeat(60));
        System.out.println("                  ADDING BOOKS TO INVENTORY");
        System.out.println("-".repeat(60));
        
        // Create books with different themes
        Book book1 = new Book("978-0141439518", "Pride and Prejudice", "Jane Austen", 
                Book.BookTheme.FICTION, 5);
        Book book2 = new Book("978-0262033848", "Introduction to Algorithms", 
                "Thomas H. Cormen", Book.BookTheme.TECHNOLOGY, 3);
        Book book3 = new Book("978-0143127550", "Sapiens: A Brief History of Humankind", 
                "Yuval Noah Harari", Book.BookTheme.HISTORY, 4);
        Book book4 = new Book("978-0307474278", "The Da Vinci Code", "Dan Brown", 
                Book.BookTheme.FICTION, 6);
        Book book5 = new Book("978-0060850524", "Brave New World", "Aldous Huxley", 
                Book.BookTheme.FICTION, 2);
        Book book6 = new Book("978-0452284234", "1984", "George Orwell", 
                Book.BookTheme.FICTION, 4);
        
        // Librarian adds books to the library
        librarian.addBook(book1);
        librarian.addBook(book2);
        librarian.addBook(book3);
        librarian.addBook(book4);
        librarian.addBook(book5);
        librarian.addBook(book6);
        
        // Display current inventory
        librarian.generateInventoryReport();

        // ==================== MEMBER REGISTRATION WITH PLANS ====================
        System.out.println("-".repeat(60));
        System.out.println("         REGISTERING MEMBERS WITH DIFFERENT PLANS");
        System.out.println("-".repeat(60));
        
        // Create members with different plans
        Member member1 = new Member("Alice", "Smith", 28, "alice@email.com", "password123");
        // Alice starts with Basic plan (default)
        System.out.println("\n[Registering Alice with Basic plan]");
        System.out.println("Alice's plan: " + member1.getMembershipPlan().getPlanName());
        System.out.println("  Max loans: " + member1.getMaxLoanLimit());
        System.out.println("  Loan period: " + member1.getLoanPeriodDays() + " days");
        System.out.println("  Overdue fee: $" + String.format("%.2f", member1.getDailyOverdueFee()) + "/day");
        
        // Bob gets Premium plan
        Member member2 = new Member("Bob", "Wilson", 35, "bob@email.com", "bobpass456");
        member2.upgradePlan(MembershipPlan.PlanType.PREMIUM);
        System.out.println("\n[Registering Bob with Premium plan]");
        System.out.println("Bob's plan: " + member2.getMembershipPlan().getPlanName());
        System.out.println("  Max loans: " + member2.getMaxLoanLimit());
        System.out.println("  Loan period: " + member2.getLoanPeriodDays() + " days");
        System.out.println("  Overdue fee: $" + String.format("%.2f", member2.getDailyOverdueFee()) + "/day");
        
        // Charlie gets VIP plan
        Member member3 = new Member("Charlie", "Brown", 22, "charlie@email.com", "securepass");
        member3.upgradePlan(MembershipPlan.PlanType.VIP);
        System.out.println("\n[Registering Charlie with VIP plan]");
        System.out.println("Charlie's plan: " + member3.getMembershipPlan().getPlanName());
        System.out.println("  Max loans: " + member3.getMaxLoanLimit());
        System.out.println("  Loan period: " + member3.getLoanPeriodDays() + " days");
        System.out.println("  Overdue fee: $" + String.format("%.2f", member3.getDailyOverdueFee()) + "/day");
        
        library.addMember(member1);
        library.addMember(member2);
        library.addMember(member3);

        // ==================== AUTHENTICATION DEMO ====================
        System.out.println("\n" + "-".repeat(60));
        System.out.println("              AUTHENTICATION SYSTEM DEMO");
        System.out.println("-".repeat(60));
        
        // Test invalid login
        System.out.println("\n[Attempting login with wrong password]");
        library.authenticateUser("alice@email.com", "wrongpassword");
        
        // Test valid member login
        System.out.println("\n[Alice logging in correctly]");
        Member loggedInAlice = library.loginMember("alice@email.com", "password123");
        if (loggedInAlice != null) {
            System.out.println("Current user: " + library.getCurrentUser().getName());
            System.out.println("Is member logged in: " + library.isMemberLoggedIn());
        }
        
        // Logout Alice
        System.out.println("\n[Alice logging out]");
        library.logout();
        
        // Test librarian login
        System.out.println("\n[Librarian Sarah logging in]");
        Librarian loggedInLibrarian = library.loginLibrarian("sarah@library.com", "admin123");
        if (loggedInLibrarian != null) {
            System.out.println("Is librarian logged in: " + library.isLibrarianLoggedIn());
        }
        library.logout();

        // ==================== BORROWING WITH PLAN LIMITS ====================
        System.out.println("\n" + "-".repeat(60));
        System.out.println("          BORROWING BOOKS (PLAN-BASED LIMITS)");
        System.out.println("-".repeat(60));
        
        // Alice (Basic - max 3 books, 14 days)
        System.out.println("\n[Alice (Basic Plan) borrowing books]");
        member1.borrowBook(book1);
        member1.borrowBook(book2);
        member1.borrowBook(book3);
        // This should fail - reached limit
        System.out.println("\n[Alice trying to exceed her 3-book limit]");
        member1.borrowBook(book4);
        
        // Bob (Premium - max 5 books, 21 days)  
        System.out.println("\n[Bob (Premium Plan) borrowing books]");
        member2.borrowBook(book4);
        member2.borrowBook(book5);
        
        // Charlie (VIP - max 10 books, 30 days)
        System.out.println("\n[Charlie (VIP Plan) borrowing books]");
        member3.borrowBook(book6);
        
        // Display each member's borrowed books with plan info
        member1.displayBorrowedBooks();
        member2.displayBorrowedBooks();
        member3.displayBorrowedBooks();

        // ==================== PLAN UPGRADE DEMO ====================
        System.out.println("-".repeat(60));
        System.out.println("              MEMBERSHIP UPGRADE DEMO");
        System.out.println("-".repeat(60));
        
        System.out.println("\n[Alice upgrading from Basic to Premium]");
        System.out.println("Before: " + member1.getMembershipPlan().getPlanName() + 
                " (Max: " + member1.getMaxLoanLimit() + " books)");
        member1.upgradePlan(MembershipPlan.PlanType.PREMIUM);
        System.out.println("After: " + member1.getMembershipPlan().getPlanName() + 
                " (Max: " + member1.getMaxLoanLimit() + " books)");
        
        // Now Alice can borrow more!
        System.out.println("\n[Alice can now borrow more books with Premium]");
        member1.borrowBook(book4);
        member1.displayBorrowedBooks();

        // ==================== CHECKING AVAILABILITY ====================
        System.out.println("-".repeat(60));
        System.out.println("                  BOOK AVAILABILITY CHECK");
        System.out.println("-".repeat(60));
        
        System.out.println("\nAvailability check by librarian:");
        System.out.println("• Pride and Prejudice: " + 
                (librarian.checkBookAvailability(book1.getIsbn()) ? "Available" : "Not Available"));
        System.out.println("• Introduction to Algorithms: " + 
                (librarian.checkBookAvailability(book2.getIsbn()) ? "Available" : "Not Available"));
        
        librarian.displayAvailableBooks();

        // ==================== ACTIVE LOANS ====================
        System.out.println("-".repeat(60));
        System.out.println("                  ACTIVE LOANS OVERVIEW");
        System.out.println("-".repeat(60));
        
        librarian.displayAllActiveLoans();

        // ==================== RETURNING BOOKS ====================
        System.out.println("-".repeat(60));
        System.out.println("                  RETURNING BOOKS");
        System.out.println("-".repeat(60));
        
        // Alice returns one book
        System.out.println("\n[Alice returning 'Sapiens']");
        member1.returnBook(book3);
        
        // Bob returns a book via librarian
        System.out.println("\n[Librarian processing Bob's return of book4]");
        if (member2.getActiveLoans().size() > 0) {
            Loan bobLoan = member2.getActiveLoans().get(0);
            librarian.returnBook(member2.getId(), bobLoan.getBook().getIsbn());
        }
        
        // Updated borrowed books display
        System.out.println("\n--- Updated Loan Status ---");
        member1.displayBorrowedBooks();
        member2.displayBorrowedBooks();

        // ==================== SEARCH FUNCTIONALITY ====================
        System.out.println("-".repeat(60));
        System.out.println("                  SEARCH FUNCTIONALITY");
        System.out.println("-".repeat(60));
        
        System.out.println("\nSearching for books by title 'the':");
        List<Book> searchResults = librarian.searchBooksByTitle("the");
        for (Book book : searchResults) {
            System.out.println("  Found: " + book.getTitle());
        }
        
        System.out.println("\nSearching for books by author 'George':");
        searchResults = librarian.searchBooksByAuthor("George");
        for (Book book : searchResults) {
            System.out.println("  Found: " + book.getTitle() + " by " + book.getAuthor());
        }
        
        System.out.println("\nSearching for FICTION books:");
        searchResults = librarian.searchBooksByTheme("FICTION");
        for (Book book : searchResults) {
            System.out.println("  Found: " + book.getTitle());
        }
        
        System.out.println("\nSearching for members named 'Smith':");
        List<Member> memberResults = librarian.searchMembersByName("Smith");
        for (Member member : memberResults) {
            System.out.println("  Found: " + member.getName() + " " + member.getSurname());
        }

        // ==================== MEMBER DETAILS ====================
        System.out.println("-".repeat(60));
        System.out.println("                  MEMBER DETAILS VIEW");
        System.out.println("-".repeat(60));
        
        System.out.println("\n[Librarian viewing Alice's details]");
        librarian.viewMemberDetails(member1.getId());
        
        System.out.println("[Librarian viewing Bob's borrowing history]");
        librarian.displayMemberBorrowingHistory(member2.getId());

        // ==================== OVERDUE SIMULATION & FEES ====================
        System.out.println("-".repeat(60));
        System.out.println("           OVERDUE FEES DEMONSTRATION");
        System.out.println("-".repeat(60));
        
        System.out.println("\n[Note: In a real scenario, overdue fees accumulate over time]");
        System.out.println("Fee rates by plan:");
        System.out.println("  Basic: $0.50/day | Premium: $0.25/day | VIP: $0.10/day | Staff: $0/day");
        
        // Show fee calculation methods for each member
        System.out.println("\nAlice's (Premium) current fees: $" + 
                String.format("%.2f", member1.calculateCurrentOverdueFees()));
        System.out.println("Bob's (Premium) current fees: $" + 
                String.format("%.2f", member2.calculateCurrentOverdueFees()));
        System.out.println("Charlie's (VIP) current fees: $" + 
                String.format("%.2f", member3.calculateCurrentOverdueFees()));
        
        // Display fee summary
        member1.displayFeeSummary();
        
        // Check for overdue books
        System.out.println("\nAlice has overdue books: " + member1.hasOverdueBooks());
        System.out.println("Overdue book count: " + member1.getOverdueBookCount());
        
        // Display overdue report
        librarian.displayOverdueLoans();

        // ==================== INVENTORY MANAGEMENT ====================
        System.out.println("-".repeat(60));
        System.out.println("           INVENTORY MANAGEMENT");
        System.out.println("-".repeat(60));
        
        // Add more copies of a popular book
        System.out.println("\n[Adding 5 more copies of '1984']");
        librarian.addCopies(book6.getIsbn(), 5);
        System.out.println("1984 now has " + book6.getTotalCopies() + " total copies");
        
        // Update book details
        System.out.println("\n[Updating book details]");
        librarian.updateBook(book1.getIsbn(), null, "Jane Austen (1775-1817)", null);

        // ==================== FINAL REPORTS ====================
        System.out.println("-".repeat(60));
        System.out.println("                  FINAL REPORTS");
        System.out.println("-".repeat(60));
        
        librarian.generateInventoryReport();
        librarian.generateMemberReport();
        library.displayMemberStatistics();

        // ==================== DATA PERSISTENCE ====================
        System.out.println("-".repeat(60));
        System.out.println("           DATA PERSISTENCE (Binary Files)");
        System.out.println("-".repeat(60));
        
        // Save all library data to binary files
        System.out.println("\n[Saving library data to binary files]");
        library.saveAllData();
        
        // Also save to text format for readability
        System.out.println("\n[Saving books to text file for backup]");
        FileManager.saveBooksToText(library.getAllBooks());
        
        // Export a library report
        System.out.println("\n[Exporting library report]");
        FileManager.exportLibraryReport(library, "library_report.txt");

        // ==================== DEMO COMPLETE ====================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         LIBRARY MANAGEMENT SYSTEM DEMO COMPLETE");
        System.out.println("=".repeat(60));
        System.out.println("\nSummary:");
        System.out.println("• Library: " + library.getLibraryName());
        System.out.println("• Total Books: " + library.getTotalBooks() + " titles");
        System.out.println("• Total Members: " + library.getTotalMembers());
        System.out.println("• Total Librarians: " + library.getTotalLibrarians());
        System.out.println("• Active Loans: " + library.getActiveLoansCount());
        System.out.println("\nMembership Plans in use:");
        System.out.println("• Alice: " + member1.getMembershipPlan().getPlanName());
        System.out.println("• Bob: " + member2.getMembershipPlan().getPlanName());
        System.out.println("• Charlie: " + member3.getMembershipPlan().getPlanName());
        System.out.println("• Sarah (Librarian): " + librarian.getMembershipPlan().getPlanName());
        System.out.println("\nData saved to:");
        System.out.println("• Binary files: data/*.dat");
        System.out.println("• Text backup: books.txt");
        System.out.println("• Report: library_report.txt");
        System.out.println("\nThank you for using the Library Management System!");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Print welcome banner
     */
    private static void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                                                          ║");
        System.out.println("║     ██╗     ███╗   ███╗███████╗                          ║");
        System.out.println("║     ██║     ████╗ ████║██╔════╝                          ║");
        System.out.println("║     ██║     ██╔████╔██║███████╗                          ║");
        System.out.println("║     ██║     ██║╚██╔╝██║╚════██║                          ║");
        System.out.println("║     ███████╗██║ ╚═╝ ██║███████║                          ║");
        System.out.println("║     ╚══════╝╚═╝     ╚═╝╚══════╝                          ║");
        System.out.println("║                                                          ║");
        System.out.println("║          Library Management System v2.0                  ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
