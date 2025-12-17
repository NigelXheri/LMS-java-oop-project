import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final String BOOKS_FILE = "books.txt";

    // --- SAVE BOOKS ---
    public static void saveBooks(List<Book> books) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book book : books) {
                // ISBN,Title,Author,THEME,Total,Available
                String line = String.format("%s|%s|%s|%s|%d|%d",
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory().name(),
                        book.getNumberOfCopies(),
                        book.getCopiesAvailable()
                );

                writer.write(line);
                writer.newLine();
            }
            System.out.println("Books saved successfully to " + BOOKS_FILE);
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    // --- LOAD BOOKS ---
    public static List<Book> loadBooks() {
        List<Book> loadedBooks = new ArrayList<>();
        File file = new File(BOOKS_FILE);

        if (!file.exists()) {
            return loadedBooks;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("|");

                // Validation
                if (parts.length == 6) {
                    String isbn = parts[0];
                    String title = parts[1];
                    String author = parts[2];
                    Book.BookTheme theme = Book.BookTheme.valueOf(parts[3]);
                    int totalCopies = Integer.parseInt(parts[4]);
                    int availableCopies = Integer.parseInt(parts[5]);

                    Book book = new Book(isbn, title, author, theme, totalCopies, availableCopies);
                    loadedBooks.add(book);
                }
            }
            System.out.println("Books loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from file. Data might be corrupted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing Book Theme (Enum). Data might be corrupted.");
        }

        return loadedBooks;
    }
}