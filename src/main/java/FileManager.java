import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileManager class handles data persistence for the Library Management System.
 * Supports both text-based and binary file operations.
 */
public class FileManager {

    // File paths
    private static final String DATA_DIRECTORY = "data";
    private static final String BOOKS_TEXT_FILE = "books.txt";
    private static final String BOOKS_BINARY_FILE = DATA_DIRECTORY + "/books.dat";
    private static final String MEMBERS_BINARY_FILE = DATA_DIRECTORY + "/members.dat";
    private static final String LOANS_BINARY_FILE = DATA_DIRECTORY + "/loans.dat";

    // ==================== INITIALIZATION ====================
    
    /**
     * Create data directory if it doesn't exist
     */
    public static void initializeDataDirectory() {
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("Data directory created: " + DATA_DIRECTORY);
            }
        }
    }

    // ==================== TEXT FILE OPERATIONS ====================
    
    /**
     * Save books to text file (CSV format)
     */
    public static void saveBooksToText(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_TEXT_FILE))) {
            for (Book book : books) {
                String line = String.format("%s|%s|%s|%s|%d|%d",
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getTheme().name(),
                        book.getTotalCopies(),
                        book.getAvailableCopies()
                );
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Books saved to text file: " + BOOKS_TEXT_FILE);
        } catch (IOException e) {
            System.err.println("Error saving books to text file: " + e.getMessage());
        }
    }

    /**
     * Load books from text file
     */
    public static List<Book> loadBooksFromText() {
        List<Book> loadedBooks = new ArrayList<>();
        File file = new File(BOOKS_TEXT_FILE);

        if (!file.exists()) {
            System.out.println("No text file found: " + BOOKS_TEXT_FILE);
            return loadedBooks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split("\\|");
                    if (parts.length == 6) {
                        String isbn = parts[0].trim();
                        String title = parts[1].trim();
                        String author = parts[2].trim();
                        Book.BookTheme theme = Book.BookTheme.valueOf(parts[3].trim());
                        int totalCopies = Integer.parseInt(parts[4].trim());
                        int availableCopies = Integer.parseInt(parts[5].trim());

                        Book book = new Book(isbn, title, author, theme, totalCopies, availableCopies);
                        loadedBooks.add(book);
                    } else {
                        System.err.println("Warning: Invalid format at line " + lineNumber);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
            System.out.println("Loaded " + loadedBooks.size() + " books from text file.");
        } catch (IOException e) {
            System.err.println("Error reading text file: " + e.getMessage());
        }

        return loadedBooks;
    }
    
    /**
     * @deprecated Use saveBooksToText() for clarity
     */
    @Deprecated
    public static void saveBooks(List<Book> books) {
        saveBooksToText(books);
    }
    
    /**
     * @deprecated Use loadBooksFromText() for clarity
     */
    @Deprecated
    public static List<Book> loadBooks() {
        return loadBooksFromText();
    }

    // ==================== BINARY FILE OPERATIONS ====================
    
    /**
     * Save books to binary file
     */
    public static void saveBooksToBinary(List<Book> books) {
        initializeDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BOOKS_BINARY_FILE))) {
            oos.writeObject(new ArrayList<>(books));
            System.out.println("Books saved to binary file: " + BOOKS_BINARY_FILE);
        } catch (IOException e) {
            System.err.println("Error saving books to binary: " + e.getMessage());
        }
    }

    /**
     * Load books from binary file
     */
    @SuppressWarnings("unchecked")
    public static List<Book> loadBooksFromBinary() {
        File file = new File(BOOKS_BINARY_FILE);
        if (!file.exists()) {
            System.out.println("No binary file found: " + BOOKS_BINARY_FILE);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(BOOKS_BINARY_FILE))) {
            List<Book> books = (List<Book>) ois.readObject();
            System.out.println("Loaded " + books.size() + " books from binary file.");
            return books;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading books from binary: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Save members to binary file
     */
    public static void saveMembersToBinary(List<Member> members) {
        initializeDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(MEMBERS_BINARY_FILE))) {
            oos.writeObject(new ArrayList<>(members));
            System.out.println("Members saved to binary file: " + MEMBERS_BINARY_FILE);
        } catch (IOException e) {
            System.err.println("Error saving members to binary: " + e.getMessage());
        }
    }

    /**
     * Load members from binary file
     */
    @SuppressWarnings("unchecked")
    public static List<Member> loadMembersFromBinary() {
        File file = new File(MEMBERS_BINARY_FILE);
        if (!file.exists()) {
            System.out.println("No binary file found: " + MEMBERS_BINARY_FILE);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(MEMBERS_BINARY_FILE))) {
            List<Member> members = (List<Member>) ois.readObject();
            System.out.println("Loaded " + members.size() + " members from binary file.");
            return members;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading members from binary: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Save loans to binary file
     */
    public static void saveLoansToBinary(List<Loan> loans) {
        initializeDataDirectory();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(LOANS_BINARY_FILE))) {
            oos.writeObject(new ArrayList<>(loans));
            System.out.println("Loans saved to binary file: " + LOANS_BINARY_FILE);
        } catch (IOException e) {
            System.err.println("Error saving loans to binary: " + e.getMessage());
        }
    }

    /**
     * Load loans from binary file
     */
    @SuppressWarnings("unchecked")
    public static List<Loan> loadLoansFromBinary() {
        File file = new File(LOANS_BINARY_FILE);
        if (!file.exists()) {
            System.out.println("No binary file found: " + LOANS_BINARY_FILE);
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(LOANS_BINARY_FILE))) {
            List<Loan> loans = (List<Loan>) ois.readObject();
            System.out.println("Loaded " + loans.size() + " loans from binary file.");
            return loans;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading loans from binary: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ==================== UTILITY METHODS ====================
    
    /**
     * Check if binary data files exist
     */
    public static boolean binaryDataExists() {
        return new File(BOOKS_BINARY_FILE).exists() || 
               new File(MEMBERS_BINARY_FILE).exists() ||
               new File(LOANS_BINARY_FILE).exists();
    }
    
    /**
     * Check if text data file exists
     */
    public static boolean textDataExists() {
        return new File(BOOKS_TEXT_FILE).exists();
    }
    
    /**
     * Delete all data files (use with caution)
     */
    public static void deleteAllData() {
        deleteFile(BOOKS_TEXT_FILE);
        deleteFile(BOOKS_BINARY_FILE);
        deleteFile(MEMBERS_BINARY_FILE);
        deleteFile(LOANS_BINARY_FILE);
        System.out.println("All data files deleted.");
    }
    
    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Deleted: " + filePath);
            }
        }
    }
    
    /**
     * Export library data to a text report
     */
    public static void exportLibraryReport(Library library, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("===========================================");
            writer.println("       LIBRARY MANAGEMENT SYSTEM REPORT    ");
            writer.println("===========================================");
            writer.println("Library: " + library.getLibraryName());
            writer.println("Generated: " + java.time.LocalDateTime.now());
            writer.println();
            
            writer.println("--- INVENTORY SUMMARY ---");
            writer.println("Total Books: " + library.getTotalBooks());
            writer.println("Total Members: " + library.getTotalMembers());
            writer.println("Active Loans: " + library.getActiveLoansCount());
            writer.println();
            
            writer.println("--- ALL BOOKS ---");
            for (Book book : library.getAllBooks()) {
                writer.println(book);
            }
            writer.println();
            
            writer.println("--- ALL MEMBERS ---");
            for (Member member : library.getAllMembers()) {
                writer.println(member);
            }
            writer.println();
            
            writer.println("--- ACTIVE LOANS ---");
            for (Loan loan : library.getAllActiveLoans()) {
                writer.println(loan);
            }
            
            writer.println("\n===========================================");
            writer.println("              END OF REPORT                ");
            writer.println("===========================================");
            
            System.out.println("Report exported to: " + filename);
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
}