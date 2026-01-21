import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * LibraryApp - Main JavaFX Application
 * Entry point for the Library Management System GUI
 * Features glass-morphism design with rounded edges
 */
public class LibraryApp extends Application {
    
    private Stage primaryStage;
    private Library library;
    private User currentUser;
    
    // Screen dimensions
    private static final double WINDOW_WIDTH = 1200;
    private static final double WINDOW_HEIGHT = 800;
    
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        
        // Initialize library and load data
        initializeLibrary();
        
        // Configure primary stage
        primaryStage.setTitle("Library Management System");
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        
        // Show login screen
        showLoginScreen();
        
        primaryStage.show();
    }
    
    /**
     * Initialize library with sample data if none exists
     */
    private void initializeLibrary() {
        library = new Library("City Public Library");
        library.loadAllData();
        
        // If no data exists, create sample data
        if (library.getTotalMembers() == 0) {
            createSampleData();
        }
    }
    
    /**
     * Create sample data for demonstration
     */
    private void createSampleData() {
        // Create sample librarian
        Librarian librarian = new Librarian("Fatbardh", "Troci", 35, 
                "fatbardh@librarian.com", "admin123", "EMP001", library);
        library.addLibrarian(librarian);
        
        // Create sample members
        Member member1 = new Member("Alice", "Smith", 28, "alice@email.com", "password123");
        Member member2 = new Member("Bob", "Wilson", 35, "bob@email.com", "password123");
        member2.upgradePlan(MembershipPlan.PlanType.PREMIUM);
        Member member3 = new Member("Charlie", "Brown", 22, "charlie@email.com", "password123");
        member3.upgradePlan(MembershipPlan.PlanType.VIP);
        
        library.addMember(member1);
        library.addMember(member2);
        library.addMember(member3);
        
        // Create sample books
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
        
        library.addBook(book1);
        library.addBook(book2);
        library.addBook(book3);
        library.addBook(book4);
        library.addBook(book5);
        library.addBook(book6);
        
        // Save initial data
        library.saveAllData();
    }
    
    /**
     * Display the login screen
     */
    public void showLoginScreen() {
        LoginController loginController = new LoginController(this, library);
        Scene loginScene = new Scene(loginController.getView(), WINDOW_WIDTH, WINDOW_HEIGHT);
        loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(loginScene);
    }
    
    /**
     * Navigate to Member Dashboard
     */
    public void showMemberDashboard(Member member) {
        this.currentUser = member;
        MemberDashboard dashboard = new MemberDashboard(this, library, member);
        Scene dashboardScene = new Scene(dashboard.getView(), WINDOW_WIDTH, WINDOW_HEIGHT);
        dashboardScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    
    /**
     * Navigate to Librarian Dashboard
     */
    public void showLibrarianDashboard(Librarian librarian) {
        this.currentUser = librarian;
        LibrarianDashboard dashboard = new LibrarianDashboard(this, library, librarian);
        Scene dashboardScene = new Scene(dashboard.getView(), WINDOW_WIDTH, WINDOW_HEIGHT);
        dashboardScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(dashboardScene);
    }
    
    /**
     * Logout and return to login screen
     */
    public void logout() {
        if (currentUser != null) {
            currentUser.logout();
            currentUser = null;
        }
        library.saveAllData();
        showLoginScreen();
    }
    
    /**
     * Get the primary stage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Get the library instance
     */
    public Library getLibrary() {
        return library;
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        launch(args);
    }
}
