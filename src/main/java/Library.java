import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Library class - Central management system for the Library Management System.
 * Handles book inventory, member management, loan tracking, authentication, and data persistence.
 * Uses binary files for storing data.
 */
public class Library implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // File paths for binary storage
    private static final String DATA_DIRECTORY = "data";
    private static final String BOOKS_FILE = DATA_DIRECTORY + "/books.dat";
    private static final String MEMBERS_FILE = DATA_DIRECTORY + "/members.dat";
    private static final String LOANS_FILE = DATA_DIRECTORY + "/loans.dat";
    private static final String LOAN_HISTORY_FILE = DATA_DIRECTORY + "/loan_history.dat";
    private static final String LIBRARIANS_FILE = DATA_DIRECTORY + "/librarians.dat";
    
    // Library data collections
    private Map<String, Book> bookInventory;          // ISBN -> Book
    private Map<Integer, Member> members;              // MemberID -> Member
    private Map<Integer, Librarian> librarians;        // LibrarianID -> Librarian
    private List<Loan> activeLoans;
    private List<Loan> loanHistory;                   // For tracking all past loans
    
    // Currently logged in user
    private transient User currentUser;
    
    // Library configuration
    private String libraryName;
    private static final double DAILY_OVERDUE_FEE = 0.50;  // Default fee
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final int MAX_BOOKS_PER_MEMBER = 5;
    
    // ==================== CONSTRUCTORS ====================
    
    public Library(String libraryName) {
        this.libraryName = libraryName;
        this.bookInventory = new HashMap<>();
        this.members = new HashMap<>();
        this.librarians = new HashMap<>();
        this.activeLoans = new ArrayList<>();
        this.loanHistory = new ArrayList<>();
        this.currentUser = null;
        
        // Ensure data directory exists
        createDataDirectory();
    }
    
    public Library() {
        this("Community Library");
    }
    
    // ==================== AUTHENTICATION ====================
    
    /**
     * Authenticate a user (member or librarian) by email and password
     * @return The authenticated user, or null if authentication fails
     */
    public User authenticateUser(String email, String password) {
        if (email == null || password == null) {
            System.out.println("Error: Email and password are required.");
            return null;
        }
        
        // Check librarians first
        for (Librarian librarian : librarians.values()) {
            if (librarian.getEmail() != null && 
                librarian.getEmail().equalsIgnoreCase(email)) {
                if (librarian.login(email, password)) {
                    currentUser = librarian;
                    return librarian;
                }
                return null;
            }
        }
        
        // Check members
        for (Member member : members.values()) {
            if (member.getEmail() != null && 
                member.getEmail().equalsIgnoreCase(email)) {
                if (member.login(email, password)) {
                    currentUser = member;
                    return member;
                }
                return null;
            }
        }
        
        System.out.println("Error: No user found with email: " + email);
        return null;
    }
    
    /**
     * Login a member by email and password
     */
    public Member loginMember(String email, String password) {
        User user = authenticateUser(email, password);
        if (user instanceof Member) {
            return (Member) user;
        }
        if (user != null) {
            System.out.println("Error: This is not a member account.");
            user.logout();
        }
        return null;
    }
    
    /**
     * Login a librarian by email and password
     */
    public Librarian loginLibrarian(String email, String password) {
        User user = authenticateUser(email, password);
        if (user instanceof Librarian) {
            return (Librarian) user;
        }
        if (user != null) {
            System.out.println("Error: This is not a librarian account.");
            user.logout();
        }
        return null;
    }
    
    /**
     * Logout the currently logged in user
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
    
    /**
     * Get the currently logged in user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     */
    public boolean isUserLoggedIn() {
        return currentUser != null && currentUser.isLoggedIn();
    }
    
    /**
     * Check if the current user is a librarian
     */
    public boolean isLibrarianLoggedIn() {
        return currentUser instanceof Librarian && currentUser.isLoggedIn();
    }
    
    /**
     * Check if the current user is a member
     */
    public boolean isMemberLoggedIn() {
        return currentUser instanceof Member && currentUser.isLoggedIn();
    }
    
    /**
     * Find user by email
     */
    public User findUserByEmail(String email) {
        if (email == null) return null;
        
        for (Librarian librarian : librarians.values()) {
            if (email.equalsIgnoreCase(librarian.getEmail())) {
                return librarian;
            }
        }
        
        for (Member member : members.values()) {
            if (email.equalsIgnoreCase(member.getEmail())) {
                return member;
            }
        }
        
        return null;
    }
    
    // ==================== LIBRARIAN MANAGEMENT ====================
    
    /**
     * Register a new librarian
     */
    public void addLibrarian(Librarian librarian) {
        if (librarian == null) {
            throw new IllegalArgumentException("Librarian cannot be null");
        }
        if (librarians.containsKey(librarian.getId())) {
            throw new IllegalArgumentException("A librarian with ID " + librarian.getId() + " already exists");
        }
        librarians.put(librarian.getId(), librarian);
        System.out.println("Librarian registered: " + librarian.getName() + " " + librarian.getSurname() + 
                " (Employee ID: " + librarian.getEmployeeId() + ")");
    }
    
    /**
     * Remove a librarian
     */
    public void removeLibrarian(int librarianId) {
        if (!librarians.containsKey(librarianId)) {
            throw new NoSuchElementException("No librarian found with ID: " + librarianId);
        }
        Librarian librarian = librarians.remove(librarianId);
        System.out.println("Librarian removed: " + librarian.getName() + " " + librarian.getSurname());
    }
    
    /**
     * Find librarian by ID
     */
    public Librarian findLibrarianById(int librarianId) {
        Librarian librarian = librarians.get(librarianId);
        if (librarian == null) {
            throw new NoSuchElementException("No librarian found with ID: " + librarianId);
        }
        return librarian;
    }
    
    /**
     * Get all librarians
     */
    public List<Librarian> getAllLibrarians() {
        return new ArrayList<>(librarians.values());
    }
    
    /**
     * Save librarians to binary file
     */
    public void saveLibrariansToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LIBRARIANS_FILE))) {
            oos.writeObject(new ArrayList<>(librarians.values()));
            System.out.println("Librarians saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving librarians: " + e.getMessage());
        }
    }
    
    /**
     * Load librarians from binary file
     */
    @SuppressWarnings("unchecked")
    public void loadLibrariansFromFile() {
        File file = new File(LIBRARIANS_FILE);
        if (!file.exists()) {
            System.out.println("No existing librarian data found.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(LIBRARIANS_FILE))) {
            List<Librarian> loadedLibrarians = (List<Librarian>) ois.readObject();
            librarians.clear();
            for (Librarian librarian : loadedLibrarians) {
                librarian.setLibrary(this); // Re-link library reference
                librarians.put(librarian.getId(), librarian);
            }
            System.out.println("Loaded " + loadedLibrarians.size() + " librarians.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading librarians: " + e.getMessage());
        }
    }

    // ==================== DATA PERSISTENCE (Binary Files) ====================
    
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    /**
     * Save all books to binary file
     */
    public void saveBooksToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BOOKS_FILE))) {
            oos.writeObject(new ArrayList<>(bookInventory.values()));
            System.out.println("Books saved successfully to binary file.");
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }
    
    /**
     * Load books from binary file
     */
    @SuppressWarnings("unchecked")
    public void loadBooksFromFile() {
        File file = new File(BOOKS_FILE);
        if (!file.exists()) {
            System.out.println("No existing book data found. Starting with empty inventory.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(BOOKS_FILE))) {
            List<Book> loadedBooks = (List<Book>) ois.readObject();
            bookInventory.clear();
            for (Book book : loadedBooks) {
                bookInventory.put(book.getIsbn(), book);
            }
            System.out.println("Loaded " + loadedBooks.size() + " books from binary file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading books: " + e.getMessage());
        }
    }
    
    /**
     * Save all members to binary file
     */
    public void saveMembersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(MEMBERS_FILE))) {
            oos.writeObject(new ArrayList<>(members.values()));
            System.out.println("Members saved successfully to binary file.");
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }
    
    /**
     * Load members from binary file
     */
    @SuppressWarnings("unchecked")
    public void loadMembersFromFile() {
        File file = new File(MEMBERS_FILE);
        if (!file.exists()) {
            System.out.println("No existing member data found.");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(MEMBERS_FILE))) {
            List<Member> loadedMembers = (List<Member>) ois.readObject();
            members.clear();
            for (Member member : loadedMembers) {
                members.put(member.getId(), member);
            }
            System.out.println("Loaded " + loadedMembers.size() + " members from binary file.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading members: " + e.getMessage());
        }
    }
    
    /**
     * Save all loans to binary file
     */
    public void saveLoansToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LOANS_FILE))) {
            oos.writeObject(activeLoans);
            System.out.println("Active loans saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving loans: " + e.getMessage());
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LOAN_HISTORY_FILE))) {
            oos.writeObject(loanHistory);
            System.out.println("Loan history saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving loan history: " + e.getMessage());
        }
    }
    
    /**
     * Load loans from binary files
     */
    @SuppressWarnings("unchecked")
    public void loadLoansFromFile() {
        File activeFile = new File(LOANS_FILE);
        if (activeFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(LOANS_FILE))) {
                activeLoans = (List<Loan>) ois.readObject();
                System.out.println("Loaded " + activeLoans.size() + " active loans.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading active loans: " + e.getMessage());
                activeLoans = new ArrayList<>();
            }
        }
        
        File historyFile = new File(LOAN_HISTORY_FILE);
        if (historyFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(LOAN_HISTORY_FILE))) {
                loanHistory = (List<Loan>) ois.readObject();
                System.out.println("Loaded " + loanHistory.size() + " historical loans.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading loan history: " + e.getMessage());
                loanHistory = new ArrayList<>();
            }
        }
    }
    
    /**
     * Save all library data to binary files
     */
    public void saveAllData() {
        System.out.println("\n========== SAVING LIBRARY DATA ==========");
        saveBooksToFile();
        saveMembersToFile();
        saveLibrariansToFile();
        saveLoansToFile();
        System.out.println("All library data saved successfully!");
    }
    
    /**
     * Load all library data from binary files
     */
    public void loadAllData() {
        System.out.println("\n========== LOADING LIBRARY DATA ==========");
        loadBooksFromFile();
        loadMembersFromFile();
        loadLibrariansFromFile();
        loadLoansFromFile();
        System.out.println("All library data loaded successfully!");
    }
    
    // ==================== BOOK MANAGEMENT ====================
    
    /**
     * Add a new book to the inventory
     */
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (bookInventory.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException("A book with ISBN " + book.getIsbn() + " already exists");
        }
        bookInventory.put(book.getIsbn(), book);
        System.out.println("Book added: " + book.getTitle());
    }
    
    /**
     * Remove a book from inventory
     */
    public void removeBook(String isbn) {
        Book book = findBookByISBN(isbn);
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new IllegalStateException("Cannot remove book: Some copies are currently borrowed");
        }
        bookInventory.remove(isbn);
        System.out.println("Book removed: " + book.getTitle());
    }
    
    /**
     * Find a book by ISBN
     */
    public Book findBookByISBN(String isbn) {
        Book book = bookInventory.get(isbn);
        if (book == null) {
            throw new NoSuchElementException("No book found with ISBN: " + isbn);
        }
        return book;
    }
    
    /**
     * Search books by title (partial match, case-insensitive)
     */
    public List<Book> searchBooksByTitle(String title) {
        return bookInventory.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Search books by author (partial match, case-insensitive)
     */
    public List<Book> searchBooksByAuthor(String author) {
        return bookInventory.values().stream()
                .filter(b -> b.getAuthor() != null && 
                        b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Search books by theme
     */
    public List<Book> searchBooksByTheme(String theme) {
        try {
            Book.BookTheme bookTheme = Book.BookTheme.valueOf(theme.toUpperCase());
            return bookInventory.values().stream()
                    .filter(b -> b.getTheme() == bookTheme)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Get all books in the library
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookInventory.values());
    }
    
    /**
     * Get all available books
     */
    public List<Book> getAvailableBooks() {
        return bookInventory.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if a book is available for borrowing
     */
    public boolean isBookAvailable(String isbn) {
        Book book = findBookByISBN(isbn);
        return book.isAvailable();
    }
    
    // ==================== MEMBER MANAGEMENT ====================
    
    /**
     * Register a new member
     */
    public void addMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        if (members.containsKey(member.getId())) {
            throw new IllegalArgumentException("A member with ID " + member.getId() + " already exists");
        }
        members.put(member.getId(), member);
        System.out.println("Member registered: " + member.getName() + " " + member.getSurname() + 
                " (ID: " + member.getId() + ")");
    }
    
    /**
     * Remove a member
     */
    public void removeMember(int memberId) {
        Member member = findMemberById(memberId);
        if (!member.getActiveLoans().isEmpty()) {
            throw new IllegalStateException("Cannot remove member: Has active loans");
        }
        members.remove(memberId);
        System.out.println("Member removed: " + member.getName() + " " + member.getSurname());
    }
    
    /**
     * Find member by ID
     */
    public Member findMemberById(int memberId) {
        Member member = members.get(memberId);
        if (member == null) {
            throw new NoSuchElementException("No member found with ID: " + memberId);
        }
        return member;
    }
    
    /**
     * Search members by name
     */
    public List<Member> searchMembersByName(String name) {
        return members.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()) ||
                        m.getSurname().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all members
     */
    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }
    
    /**
     * Get member's borrowing details
     */
    public void displayMemberDetails(int memberId) {
        Member member = findMemberById(memberId);
        System.out.println("\n========== MEMBER DETAILS ==========");
        System.out.println("ID: " + member.getId());
        System.out.println("Name: " + member.getName() + " " + member.getSurname());
        System.out.println("Age: " + member.getAge());
        if (member.getEmail() != null) {
            System.out.println("Email: " + member.getEmail());
        }
        
        System.out.println("\n--- Active Loans ---");
        List<Loan> memberLoans = getActiveLoansByMember(memberId);
        if (memberLoans.isEmpty()) {
            System.out.println("No active loans");
        } else {
            for (Loan loan : memberLoans) {
                System.out.println("  - " + loan.getBook().getTitle() + 
                        " (Due: " + loan.getDueDate() + 
                        (loan.isOverdue() ? " - OVERDUE by " + loan.getDaysOverdue() + " days!" : "") + ")");
            }
        }
        
        System.out.println("\n--- Overdue Fees ---");
        double totalFees = member.calculateTotalOverdueFees();
        System.out.println("Total overdue fees: $" + String.format("%.2f", totalFees));
    }
    
    // ==================== LOAN MANAGEMENT ====================
    
    /**
     * Issue a loan - Member borrows a book
     */
    public Loan issueLoan(int memberId, String isbn) {
        Member member = findMemberById(memberId);
        Book book = findBookByISBN(isbn);
        
        // Validate loan conditions
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for borrowing");
        }
        
        if (member.getActiveLoans().size() >= MAX_BOOKS_PER_MEMBER) {
            throw new IllegalStateException("Member has reached maximum loan limit (" + 
                    MAX_BOOKS_PER_MEMBER + " books)");
        }
        
        // Check if member has overdue books
        if (member.hasOverdueBooks()) {
            throw new IllegalStateException("Member has overdue books. Please return them first.");
        }
        
        // Check if member already has this book
        for (Loan loan : member.getActiveLoans()) {
            if (loan.getBook().getIsbn().equals(isbn)) {
                throw new IllegalStateException("Member already has this book on loan");
            }
        }
        
        // Create and record the loan
        Loan newLoan = new Loan(member, book, DEFAULT_LOAN_PERIOD_DAYS);
        activeLoans.add(newLoan);
        member.addLoan(newLoan);
        book.borrowCopy();
        
        System.out.println("Loan issued: " + book.getTitle() + " to " + 
                member.getName() + " " + member.getSurname());
        System.out.println("Due date: " + newLoan.getDueDate());
        
        return newLoan;
    }
    
    /**
     * Return a book
     */
    public void returnBook(int memberId, String isbn) {
        Member member = findMemberById(memberId);
        Book book = findBookByISBN(isbn);
        
        // Find the loan
        Loan loanToReturn = null;
        for (Loan loan : activeLoans) {
            if (loan.getMember().getId() == memberId && 
                loan.getBook().getIsbn().equals(isbn) && 
                !loan.isReturned()) {
                loanToReturn = loan;
                break;
            }
        }
        
        if (loanToReturn == null) {
            throw new IllegalStateException("No active loan found for this book and member");
        }
        
        // Calculate and apply overdue fees if any
        double overdueFee = loanToReturn.calculateOverdueFee(DAILY_OVERDUE_FEE);
        
        // Process return
        loanToReturn.markReturned();
        activeLoans.remove(loanToReturn);
        loanHistory.add(loanToReturn);
        member.removeLoan(loanToReturn);
        book.returnCopy();
        
        System.out.println("Book returned: " + book.getTitle());
        if (overdueFee > 0) {
            System.out.println("Overdue fee: $" + String.format("%.2f", overdueFee));
        }
    }
    
    /**
     * Get all active loans
     */
    public List<Loan> getAllActiveLoans() {
        return new ArrayList<>(activeLoans);
    }
    
    /**
     * Get active loans by member
     */
    public List<Loan> getActiveLoansByMember(int memberId) {
        return activeLoans.stream()
                .filter(l -> l.getMember().getId() == memberId && !l.isReturned())
                .collect(Collectors.toList());
    }
    
    /**
     * Get all overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return activeLoans.stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }
    
    /**
     * Get loan history for a member
     */
    public List<Loan> getLoanHistoryByMember(int memberId) {
        return loanHistory.stream()
                .filter(l -> l.getMember().getId() == memberId)
                .collect(Collectors.toList());
    }
    
    // ==================== FEE MANAGEMENT ====================
    
    /**
     * Calculate total overdue fees for a member
     */
    public double calculateMemberOverdueFees(int memberId) {
        Member member = findMemberById(memberId);
        return member.calculateTotalOverdueFees();
    }
    
    /**
     * Get daily overdue fee rate
     */
    public static double getDailyOverdueFee() {
        return DAILY_OVERDUE_FEE;
    }
    
    // ==================== REPORTS & STATISTICS ====================
    
    /**
     * Display library inventory summary
     */
    public void displayInventorySummary() {
        System.out.println("\n========== " + libraryName.toUpperCase() + " INVENTORY ==========");
        int totalTitles = bookInventory.size();
        int totalCopies = bookInventory.values().stream()
                .mapToInt(Book::getTotalCopies).sum();
        int availableCopies = bookInventory.values().stream()
                .mapToInt(Book::getAvailableCopies).sum();
        int borrowedCopies = totalCopies - availableCopies;
        
        System.out.println("Total unique titles: " + totalTitles);
        System.out.println("Total book copies: " + totalCopies);
        System.out.println("Available copies: " + availableCopies);
        System.out.println("Borrowed copies: " + borrowedCopies);
        System.out.println("========================================\n");
    }
    
    /**
     * Display all books in inventory
     */
    public void displayAllBooks() {
        System.out.println("\n========== ALL BOOKS ==========");
        if (bookInventory.isEmpty()) {
            System.out.println("No books in inventory.");
            return;
        }
        for (Book book : bookInventory.values()) {
            System.out.println(book);
        }
        System.out.println("================================\n");
    }
    
    /**
     * Display available books
     */
    public void displayAvailableBooks() {
        System.out.println("\n========== AVAILABLE BOOKS ==========");
        List<Book> available = getAvailableBooks();
        if (available.isEmpty()) {
            System.out.println("No books currently available.");
            return;
        }
        for (Book book : available) {
            System.out.println(book.getTitle() + " by " + book.getAuthor() + 
                    " (" + book.getAvailableCopies() + " available)");
        }
        System.out.println("======================================\n");
    }
    
    /**
     * Display overdue loans report
     */
    public void displayOverdueReport() {
        System.out.println("\n========== OVERDUE LOANS REPORT ==========");
        List<Loan> overdue = getOverdueLoans();
        if (overdue.isEmpty()) {
            System.out.println("No overdue loans. Great job!");
            return;
        }
        
        double totalFees = 0;
        for (Loan loan : overdue) {
            double fee = loan.calculateOverdueFee(DAILY_OVERDUE_FEE);
            totalFees += fee;
            System.out.println("Member: " + loan.getMember().getName() + " " + 
                    loan.getMember().getSurname());
            System.out.println("  Book: " + loan.getBook().getTitle());
            System.out.println("  Due: " + loan.getDueDate() + 
                    " (" + loan.getDaysOverdue() + " days overdue)");
            System.out.println("  Fee: $" + String.format("%.2f", fee));
            System.out.println("---");
        }
        System.out.println("Total overdue fees: $" + String.format("%.2f", totalFees));
        System.out.println("==========================================\n");
    }
    
    /**
     * Display member statistics
     */
    public void displayMemberStatistics() {
        System.out.println("\n========== MEMBER STATISTICS ==========");
        int totalMembers = members.size();
        long membersWithLoans = members.values().stream()
                .filter(m -> !m.getActiveLoans().isEmpty())
                .count();
        long membersWithOverdue = members.values().stream()
                .filter(Member::hasOverdueBooks)
                .count();
        
        System.out.println("Total registered members: " + totalMembers);
        System.out.println("Members with active loans: " + membersWithLoans);
        System.out.println("Members with overdue books: " + membersWithOverdue);
        System.out.println("========================================\n");
    }
    
    // ==================== GETTERS ====================
    
    public String getLibraryName() {
        return libraryName;
    }
    
    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }
    
    public int getTotalBooks() {
        return bookInventory.size();
    }
    
    public int getTotalMembers() {
        return members.size();
    }
    
    public int getTotalLibrarians() {
        return librarians.size();
    }
    
    public int getActiveLoansCount() {
        return activeLoans.size();
    }

    @Override
    public String toString() {
        return "Library{" +
                "name='" + libraryName + '\'' +
                ", books=" + bookInventory.size() +
                ", members=" + members.size() +
                ", activeLoans=" + activeLoans.size() +
                '}';
    }
}
