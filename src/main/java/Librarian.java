public class Librarian extends User{

    private String employeeId;
    private Library library; // Reference to the library

    public Librarian(String name, String email, int age, String employeeId, Library library) {
        super(name, email, age);
        this.employeeId = employeeId;
        this.library = library;
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
     * Remove a book from inventory
     */
    public void removeBook(String isbn) {
        try {
            Book book = library.findBookByISBN(isbn);
            if (book.getAvailableCopies() < book.getTotalCopies()) {
                throw new IllegalStateException("Cannot remove book: Some copies are currently borrowed");
            }
            library.removeBook(isbn);
            System.out.println("Book removed successfully");
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
            book.setTitle(newTitle);
            book.setAuthor(newAuthor);
            book.setTheme(newTheme);
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
            book.setTotalCopies(book.getTotalCopies() + additionalCopies);
            book.setAvailableCopies(book.getAvailableCopies() + additionalCopies);
            System.out.println("Added " + additionalCopies + " copies");
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
        List<Book> allBooks = library.getAllBooks();
        if (allBooks.isEmpty()) {
            System.out.println("No books in the library");
            return;
        }

        System.out.println("\n========== LIBRARY INVENTORY ==========");
        for (Book book : allBooks) {
            System.out.println(book);
            System.out.println("---");
        }
    }

    /**
     * Display only available books
     */
    public void displayAvailableBooks() {
        List<Book> available = library.getAvailableBooks();
        System.out.println("\n========== AVAILABLE BOOKS ==========");
        for (Book book : available) {
            System.out.println(book);
        }
    }

    // ========== MEMBER MANAGEMENT ==========

    /**
     * Register a new member
     */
    public void registerMember(String name, String email, int age, String memberId) {
        try {
            Member newMember = new Member(name, email, age, memberId);
            library.addMember(newMember);
            System.out.println("Member registered successfully: " + name);
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    /**
     * Remove a member from the system
     */
    public void removeMember(String memberId) {
        try {
            Member member = library.findMemberById(memberId);
            if (!member.getBorrowedBooks().isEmpty()) {
                throw new IllegalStateException("Cannot remove member: Has active loans");
            }
            library.removeMember(memberId);
            System.out.println("Member removed successfully");
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
        for (Member member : allMembers) {
            System.out.println(member);
            System.out.println("Active loans: " + member.getBorrowedBooks().size());
            System.out.println("---");
        }
    }

    /**
     * View a specific member's details including borrowed books
     */
    public void viewMemberDetails(String memberId) {
        try {
            Member member = library.findMemberById(memberId);
            System.out.println("\n========== MEMBER DETAILS ==========");
            System.out.println(member);
            System.out.println("\nBorrowed Books:");

            List<Loan> loans = library.getActiveLoansByMember(memberId);
            if (loans.isEmpty()) {
                System.out.println("No active loans");
            } else {
                for (Loan loan : loans) {
                    System.out.println("- " + loan.getBook().getTitle() +
                            " (Due: " + loan.getDueDate() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ========== LOAN MANAGEMENT ==========

    /**
     * Issue a book to a member
     */
    public void issueBook(String memberId, String isbn) {
        try {
            Loan loan = library.issueLoan(memberId, isbn);
            System.out.println("Book issued successfully!");
            System.out.println("Due date: " + loan.getDueDate());
        } catch (Exception e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    /**
     * Process book return
     */
    public void returnBook(String memberId, String isbn) {
        try {
            library.returnBook(memberId, isbn);
            System.out.println("Book returned successfully!");
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
        for (Loan loan : activeLoans) {
            System.out.println(loan);
            System.out.println("---");
        }
    }

    /**
     * View overdue loans
     */
    public void displayOverdueLoans() {
        List<Loan> overdueLoans = library.getOverdueLoans();
        System.out.println("\n========== OVERDUE LOANS ==========");
        if (overdueLoans.isEmpty()) {
            System.out.println("No overdue loans");
        } else {
            for (Loan loan : overdueLoans) {
                System.out.println(loan);
                System.out.println("Days overdue: " + loan.getDaysOverdue());
                System.out.println("---");
            }
        }
    }

    // ========== REPORTS & STATISTICS ==========

    /**
     * Generate inventory report
     */
    public void generateInventoryReport() {
        List<Book> allBooks = library.getAllBooks();
        int totalBooks = allBooks.stream()
                .mapToInt(Book::getTotalCopies)
                .sum();
        int availableBooks = allBooks.stream()
                .mapToInt(Book::getAvailableCopies)
                .sum();
        int borrowedBooks = totalBooks - availableBooks;

        System.out.println("\n========== INVENTORY REPORT ==========");
        System.out.println("Total unique titles: " + allBooks.size());
        System.out.println("Total book copies: " + totalBooks);
        System.out.println("Available copies: " + availableBooks);
        System.out.println("Borrowed copies: " + borrowedBooks);
    }

    /**
     * Generate member statistics
     */
    public void generateMemberReport() {
        List<Member> allMembers = library.getAllMembers();
        int totalMembers = allMembers.size();
        int membersWithLoans = (int) allMembers.stream()
                .filter(m -> !m.getBorrowedBooks().isEmpty())
                .count();

        System.out.println("\n========== MEMBER REPORT ==========");
        System.out.println("Total members: " + totalMembers);
        System.out.println("Members with active loans: " + membersWithLoans);
        System.out.println("Members without loans: " + (totalMembers - membersWithLoans));
    }

    /**
     * Find most borrowed books
     */
    public void displayMostBorrowedBooks() {
        // Implementation depends on if you track historical loan data
        System.out.println("Most Borrowed Books feature coming soon...");
    }

    // Getters
    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
