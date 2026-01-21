import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * LibrarianDashboard - Dashboard for library staff
 * Features: Manage books, manage members, process loans/returns, view reports, manage inventory
 */
public class LibrarianDashboard {
    
    private LibraryApp app;
    private Library library;
    private Librarian librarian;
    private BorderPane rootPane;
    private StackPane contentArea;
    
    // Sidebar buttons for active state management
    private Button activeButton;
    private Button btnDashboard, btnBooks, btnMembers, btnLoans, btnReports, btnLogout;
    
    public LibrarianDashboard(LibraryApp app, Library library, Librarian librarian) {
        this.app = app;
        this.library = library;
        this.librarian = librarian;
        createView();
    }
    
    /**
     * Create the main dashboard view
     */
    private void createView() {
        rootPane = new BorderPane();
        rootPane.getStyleClass().add("gradient-bg-librarian");
        
        // Add background decoration
        addBackgroundDecoration();
        
        // Create sidebar
        VBox sidebar = createSidebar();
        rootPane.setLeft(sidebar);
        
        // Create content area
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30));
        rootPane.setCenter(contentArea);
        
        // Show dashboard by default
        showDashboard();
        setActiveButton(btnDashboard);
    }
    
    /**
     * Add decorative background elements
     */
    private void addBackgroundDecoration() {
        Circle circle1 = new Circle(180, Color.rgb(13, 148, 136, 0.15));
        circle1.setEffect(new GaussianBlur(70));
        
        Circle circle2 = new Circle(150, Color.rgb(16, 185, 129, 0.1));
        circle2.setEffect(new GaussianBlur(60));
    }
    
    /**
     * Create the sidebar navigation
     */
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);
        sidebar.setPadding(new Insets(30, 15, 30, 15));
        
        // User info section
        VBox userInfo = createUserInfoSection();
        
        // Navigation buttons
        btnDashboard = createSidebarButton("🏠  Dashboard", () -> { showDashboard(); setActiveButton(btnDashboard); });
        btnBooks = createSidebarButton("📚  Manage Books", () -> { showManageBooks(); setActiveButton(btnBooks); });
        btnMembers = createSidebarButton("👥  Manage Members", () -> { showManageMembers(); setActiveButton(btnMembers); });
        btnLoans = createSidebarButton("📖  Loans & Returns", () -> { showLoansReturns(); setActiveButton(btnLoans); });
        btnReports = createSidebarButton("📊  Reports", () -> { showReports(); setActiveButton(btnReports); });
        
        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Logout button
        btnLogout = createSidebarButton("🚪  Logout", this::handleLogout);
        btnLogout.setStyle("-fx-text-fill: #fca5a5;");
        
        sidebar.getChildren().addAll(
            userInfo,
            new Region() {{ setPrefHeight(30); }},
            btnDashboard,
            btnBooks,
            btnMembers,
            btnLoans,
            btnReports,
            spacer,
            btnLogout
        );
        
        return sidebar;
    }
    
    /**
     * Create user info section in sidebar
     */
    private VBox createUserInfoSection() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15;");
        
        // Avatar
        StackPane avatar = new StackPane();
        Circle circle = new Circle(35, Color.web("#0d9488"));
        Text initials = new Text(librarian.getName().substring(0, 1).toUpperCase());
        initials.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");
        avatar.getChildren().addAll(circle, initials);
        
        // Name
        Label name = new Label(librarian.getName() + " " + librarian.getSurname());
        name.getStyleClass().add("section-label");
        
        // Role badge
        Label roleBadge = new Label("📋 Staff - " + librarian.getEmployeeId());
        roleBadge.getStyleClass().addAll("badge", "badge-success");
        
        box.getChildren().addAll(avatar, name, roleBadge);
        return box;
    }
    
    /**
     * Create a sidebar navigation button
     */
    private Button createSidebarButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("sidebar-button");
        button.setOnAction(e -> action.run());
        return button;
    }
    
    /**
     * Set active button styling
     */
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("sidebar-button-active");
            activeButton.getStyleClass().add("sidebar-button");
        }
        button.getStyleClass().remove("sidebar-button");
        button.getStyleClass().add("sidebar-button-active");
        activeButton = button;
    }
    
    /**
     * Show dashboard overview
     */
    private void showDashboard() {
        VBox content = new VBox(25);
        content.setAlignment(Pos.TOP_LEFT);
        
        // Welcome header
        Text welcome = new Text("Staff Dashboard");
        welcome.getStyleClass().add("title-label");
        
        Label subtitle = new Label("Welcome back, " + librarian.getName() + "! Here's today's overview.");
        subtitle.getStyleClass().add("subtitle-label");
        
        // Stats cards row 1
        HBox statsRow1 = new HBox(20);
        statsRow1.getChildren().addAll(
            createStatCard("📚", "Total Books", String.valueOf(library.getTotalBooks())),
            createStatCard("👥", "Total Members", String.valueOf(library.getTotalMembers())),
            createStatCard("📖", "Active Loans", String.valueOf(library.getActiveLoansCount())),
            createStatCard("⚠️", "Overdue Loans", String.valueOf(countOverdueLoans()))
        );
        
        // Stats cards row 2
        HBox statsRow2 = new HBox(20);
        statsRow2.getChildren().addAll(
            createStatCard("📦", "Total Copies", String.valueOf(countTotalCopies())),
            createStatCard("✅", "Available", String.valueOf(countAvailableCopies())),
            createStatCard("💰", "Total Fees Due", String.format("$%.2f", calculateTotalFees())),
            createStatCard("🏆", "VIP Members", String.valueOf(countVIPMembers()))
        );
        
        // Quick actions
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.getStyleClass().add("section-label");
        
        HBox quickActions = new HBox(15);
        Button addBookBtn = new Button("Add New Book");
        addBookBtn.getStyleClass().add("primary-button");
        addBookBtn.setOnAction(e -> showAddBookDialog());
        
        Button addMemberBtn = new Button("Register Member");
        addMemberBtn.getStyleClass().add("success-button");
        addMemberBtn.setOnAction(e -> showAddMemberDialog());
        
        Button issueBookBtn = new Button("Issue Book");
        issueBookBtn.getStyleClass().add("glass-button");
        issueBookBtn.setOnAction(e -> { showLoansReturns(); setActiveButton(btnLoans); });
        
        Button overdueBtn = new Button("View Overdue");
        overdueBtn.getStyleClass().add("warning-button");
        overdueBtn.setOnAction(e -> { showReports(); setActiveButton(btnReports); });
        
        quickActions.getChildren().addAll(addBookBtn, addMemberBtn, issueBookBtn, overdueBtn);
        
        // Recent activity
        Label recentLabel = new Label("Recent Loans");
        recentLabel.getStyleClass().add("section-label");
        
        VBox recentActivity = createRecentLoansPreview();
        
        content.getChildren().addAll(welcome, subtitle, statsRow1, statsRow2, 
                quickActionsLabel, quickActions, recentLabel, recentActivity);
        
        setContent(content);
    }
    
    /**
     * Count overdue loans
     */
    private int countOverdueLoans() {
        return (int) library.getAllMembers().stream()
            .flatMap(m -> m.getActiveLoans().stream())
            .filter(Loan::isOverdue)
            .count();
    }
    
    /**
     * Count total copies
     */
    private int countTotalCopies() {
        return library.getAllBooks().stream()
            .mapToInt(Book::getTotalCopies)
            .sum();
    }
    
    /**
     * Count available copies
     */
    private int countAvailableCopies() {
        return library.getAllBooks().stream()
            .mapToInt(Book::getAvailableCopies)
            .sum();
    }
    
    /**
     * Calculate total fees due
     */
    private double calculateTotalFees() {
        return library.getAllMembers().stream()
            .mapToDouble(Member::calculateCurrentOverdueFees)
            .sum();
    }
    
    /**
     * Count VIP members
     */
    private int countVIPMembers() {
        return (int) library.getAllMembers().stream()
            .filter(m -> m.getMembershipPlan().getPlanType() == MembershipPlan.PlanType.VIP)
            .count();
    }
    
    /**
     * Create a stat card
     */
    private VBox createStatCard(String icon, String title, String value) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 28px;");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        
        card.getChildren().addAll(iconLabel, valueLabel, titleLabel);
        return card;
    }
    
    /**
     * Create recent loans preview
     */
    private VBox createRecentLoansPreview() {
        VBox box = new VBox(10);
        box.getStyleClass().add("glass-card");
        
        List<Loan> allLoans = library.getAllMembers().stream()
            .flatMap(m -> m.getActiveLoans().stream())
            .sorted((a, b) -> b.getLoanDate().compareTo(a.getLoanDate()))
            .limit(5)
            .toList();
        
        if (allLoans.isEmpty()) {
            Label noLoans = new Label("No active loans in the system.");
            noLoans.getStyleClass().add("body-label");
            box.getChildren().add(noLoans);
        } else {
            for (Loan loan : allLoans) {
                HBox loanRow = createLoanPreviewRow(loan);
                box.getChildren().add(loanRow);
            }
        }
        
        return box;
    }
    
    /**
     * Create loan preview row
     */
    private HBox createLoanPreviewRow(Loan loan) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10));
        row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10;");
        
        Label bookIcon = new Label("📖");
        bookIcon.setStyle("-fx-font-size: 20px;");
        
        VBox bookInfo = new VBox(3);
        Label title = new Label(loan.getBook().getTitle());
        title.getStyleClass().add("body-label");
        title.setStyle("-fx-font-weight: bold;");
        
        Label borrower = new Label("Borrowed by: " + loan.getMember().getName() + " " + loan.getMember().getSurname());
        borrower.getStyleClass().add("muted-label");
        
        bookInfo.getChildren().addAll(title, borrower);
        HBox.setHgrow(bookInfo, Priority.ALWAYS);
        
        // Status
        Label status = new Label(loan.isOverdue() ? "OVERDUE" : "Active");
        status.getStyleClass().addAll("badge", loan.isOverdue() ? "badge-danger" : "badge-success");
        
        row.getChildren().addAll(bookIcon, bookInfo, status);
        return row;
    }
    
    /**
     * Show manage books view
     */
    private void showManageBooks() {
        VBox content = new VBox(20);
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Manage Books");
        title.getStyleClass().add("title-label");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search books...");
        searchField.getStyleClass().add("glass-text-field");
        searchField.setPrefWidth(250);
        
        Button addBtn = new Button("+ Add Book");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showAddBookDialog());
        
        header.getChildren().addAll(title, searchField, addBtn);
        
        // Books table
        TableView<Book> booksTable = createBooksManagementTable();
        VBox.setVgrow(booksTable, Priority.ALWAYS);
        
        ObservableList<Book> books = FXCollections.observableArrayList(library.getAllBooks());
        booksTable.setItems(books);
        
        // Search functionality
        searchField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.isEmpty()) {
                booksTable.setItems(FXCollections.observableArrayList(library.getAllBooks()));
            } else {
                List<Book> filtered = library.getAllBooks().stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                                b.getAuthor().toLowerCase().contains(newValue.toLowerCase()) ||
                                b.getIsbn().contains(newValue))
                    .toList();
                booksTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        
        // Action buttons
        HBox actions = new HBox(15);
        Button editBtn = new Button("Edit Selected");
        editBtn.getStyleClass().add("glass-button");
        editBtn.setDisable(true);
        editBtn.setOnAction(e -> showEditBookDialog(booksTable.getSelectionModel().getSelectedItem()));
        
        Button addCopiesBtn = new Button("Add Copies");
        addCopiesBtn.getStyleClass().add("success-button");
        addCopiesBtn.setDisable(true);
        addCopiesBtn.setOnAction(e -> showAddCopiesDialog(booksTable.getSelectionModel().getSelectedItem()));
        
        Button deleteBtn = new Button("Remove Book");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> handleDeleteBook(booksTable.getSelectionModel().getSelectedItem()));
        
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            boolean hasSelection = selected != null;
            editBtn.setDisable(!hasSelection);
            addCopiesBtn.setDisable(!hasSelection);
            deleteBtn.setDisable(!hasSelection);
        });
        
        actions.getChildren().addAll(editBtn, addCopiesBtn, deleteBtn);
        
        content.getChildren().addAll(header, booksTable, actions);
        setContent(content);
    }
    
    /**
     * Create books management table
     */
    @SuppressWarnings("unchecked")
    private TableView<Book> createBooksManagementTable() {
        TableView<Book> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(130);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(150);
        
        TableColumn<Book, String> themeCol = new TableColumn<>("Category");
        themeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTheme().toString()));
        themeCol.setPrefWidth(100);
        
        TableColumn<Book, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTotalCopies())));
        totalCol.setPrefWidth(60);
        
        TableColumn<Book, String> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAvailableCopies())));
        availableCol.setPrefWidth(80);
        
        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isAvailable() ? "✓ In Stock" : "✗ Out"));
        statusCol.setPrefWidth(90);
        
        table.getColumns().addAll(isbnCol, titleCol, authorCol, themeCol, totalCol, availableCol, statusCol);
        
        return table;
    }
    
    /**
     * Show add book dialog
     */
    private void showAddBookDialog() {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");
        
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        TextField isbnField = new TextField();
        isbnField.setPromptText("978-0000000000");
        TextField titleField = new TextField();
        titleField.setPromptText("Book title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author name");
        ComboBox<Book.BookTheme> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll(Book.BookTheme.values());
        themeCombo.setValue(Book.BookTheme.FICTION);
        Spinner<Integer> copiesSpinner = new Spinner<>(1, 100, 1);
        
        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(isbnField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(themeCombo, 1, 3);
        grid.add(new Label("Copies:"), 0, 4);
        grid.add(copiesSpinner, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                return new Book(isbnField.getText(), titleField.getText(), 
                    authorField.getText(), themeCombo.getValue(), copiesSpinner.getValue());
            }
            return null;
        });
        
        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            librarian.addBook(book);
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully!");
            showManageBooks();
        });
    }
    
    /**
     * Show edit book dialog
     */
    private void showEditBookDialog(Book book) {
        if (book == null) return;
        
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Book");
        dialog.setHeaderText("Edit book details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        
        grid.add(new Label("ISBN:"), 0, 0);
        grid.add(new Label(book.getIsbn()), 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(buttonType -> buttonType == saveButtonType);
        
        Optional<Boolean> result = dialog.showAndWait();
        if (result.isPresent() && result.get()) {
            librarian.updateBook(book.getIsbn(), titleField.getText(), authorField.getText(), null);
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully!");
            showManageBooks();
        }
    }
    
    /**
     * Show add copies dialog
     */
    private void showAddCopiesDialog(Book book) {
        if (book == null) return;
        
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add Copies");
        dialog.setHeaderText("Add copies to: " + book.getTitle());
        dialog.setContentText("Number of copies to add:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(copies -> {
            try {
                int count = Integer.parseInt(copies);
                if (count > 0) {
                    librarian.addCopies(book.getIsbn(), count);
                    library.saveAllData();
                    showAlert(Alert.AlertType.INFORMATION, "Success", 
                        count + " copies added. Total: " + book.getTotalCopies());
                    showManageBooks();
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid number.");
            }
        });
    }
    
    /**
     * Handle delete book
     */
    private void handleDeleteBook(Book book) {
        if (book == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Book");
        confirm.setHeaderText("Remove \"" + book.getTitle() + "\"?");
        confirm.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            librarian.removeBook(book.getIsbn());
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book removed successfully!");
            showManageBooks();
        }
    }
    
    /**
     * Show manage members view
     */
    private void showManageMembers() {
        VBox content = new VBox(20);
        
        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Manage Members");
        title.getStyleClass().add("title-label");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search members...");
        searchField.getStyleClass().add("glass-text-field");
        searchField.setPrefWidth(250);
        
        Button addBtn = new Button("+ Register Member");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showAddMemberDialog());
        
        header.getChildren().addAll(title, searchField, addBtn);
        
        // Members table
        TableView<Member> membersTable = createMembersTable();
        VBox.setVgrow(membersTable, Priority.ALWAYS);
        
        ObservableList<Member> members = FXCollections.observableArrayList(library.getAllMembers());
        membersTable.setItems(members);
        
        // Search functionality
        searchField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.isEmpty()) {
                membersTable.setItems(FXCollections.observableArrayList(library.getAllMembers()));
            } else {
                List<Member> filtered = library.getAllMembers().stream()
                    .filter(m -> m.getName().toLowerCase().contains(newValue.toLowerCase()) ||
                                m.getSurname().toLowerCase().contains(newValue.toLowerCase()) ||
                                (m.getEmail() != null && m.getEmail().toLowerCase().contains(newValue.toLowerCase())))
                    .toList();
                membersTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        
        // Action buttons
        HBox actions = new HBox(15);
        Button viewBtn = new Button("View Details");
        viewBtn.getStyleClass().add("glass-button");
        viewBtn.setDisable(true);
        viewBtn.setOnAction(e -> showMemberDetails(membersTable.getSelectionModel().getSelectedItem()));
        
        Button deleteBtn = new Button("Remove Member");
        deleteBtn.getStyleClass().add("danger-button");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> handleDeleteMember(membersTable.getSelectionModel().getSelectedItem()));
        
        membersTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            boolean hasSelection = selected != null;
            viewBtn.setDisable(!hasSelection);
            deleteBtn.setDisable(!hasSelection);
        });
        
        actions.getChildren().addAll(viewBtn, deleteBtn);
        
        content.getChildren().addAll(header, membersTable, actions);
        setContent(content);
    }
    
    /**
     * Create members table
     */
    @SuppressWarnings("unchecked")
    private TableView<Member> createMembersTable() {
        TableView<Member> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Member, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        
        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getName() + " " + data.getValue().getSurname()));
        nameCol.setPrefWidth(180);
        
        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getEmail() != null ? data.getValue().getEmail() : "N/A"));
        emailCol.setPrefWidth(180);
        
        TableColumn<Member, String> planCol = new TableColumn<>("Plan");
        planCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getMembershipPlan().getPlanType().name()));
        planCol.setPrefWidth(100);
        
        TableColumn<Member, String> loansCol = new TableColumn<>("Active Loans");
        loansCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getActiveLoans().size() + "/" + data.getValue().getMaxLoanLimit()));
        loansCol.setPrefWidth(100);
        
        TableColumn<Member, String> feesCol = new TableColumn<>("Fees Due");
        feesCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("$%.2f", data.getValue().calculateCurrentOverdueFees())));
        feesCol.setPrefWidth(100);
        
        table.getColumns().addAll(idCol, nameCol, emailCol, planCol, loansCol, feesCol);
        
        return table;
    }
    
    /**
     * Show add member dialog
     */
    private void showAddMemberDialog() {
        Dialog<Member> dialog = new Dialog<>();
        dialog.setTitle("Register New Member");
        dialog.setHeaderText("Enter member details");
        
        ButtonType addButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last name");
        Spinner<Integer> ageSpinner = new Spinner<>(5, 120, 25);
        TextField emailField = new TextField();
        emailField.setPromptText("email@example.com");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<MembershipPlan.PlanType> planCombo = new ComboBox<>();
        planCombo.getItems().addAll(MembershipPlan.PlanType.BASIC, 
            MembershipPlan.PlanType.PREMIUM, MembershipPlan.PlanType.VIP);
        planCombo.setValue(MembershipPlan.PlanType.BASIC);
        
        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageSpinner, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Password:"), 0, 4);
        grid.add(passwordField, 1, 4);
        grid.add(new Label("Plan:"), 0, 5);
        grid.add(planCombo, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == addButtonType) {
                Member member = new Member(firstNameField.getText(), lastNameField.getText(), 
                    ageSpinner.getValue(), emailField.getText(), passwordField.getText());
                if (planCombo.getValue() != MembershipPlan.PlanType.BASIC) {
                    member.upgradePlan(planCombo.getValue());
                }
                return member;
            }
            return null;
        });
        
        Optional<Member> result = dialog.showAndWait();
        result.ifPresent(member -> {
            library.addMember(member);
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Member registered successfully!\nMember ID: " + member.getId());
            showManageMembers();
        });
    }
    
    /**
     * Show member details dialog
     */
    private void showMemberDetails(Member member) {
        if (member == null) return;
        
        Alert details = new Alert(Alert.AlertType.INFORMATION);
        details.setTitle("Member Details");
        details.setHeaderText(member.getName() + " " + member.getSurname());
        
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(member.getId()).append("\n");
        sb.append("Email: ").append(member.getEmail() != null ? member.getEmail() : "N/A").append("\n");
        sb.append("Age: ").append(member.getAge()).append("\n");
        sb.append("\n--- Membership ---\n");
        sb.append("Plan: ").append(member.getMembershipPlan().getPlanName()).append("\n");
        sb.append("Max Books: ").append(member.getMaxLoanLimit()).append("\n");
        sb.append("Loan Period: ").append(member.getLoanPeriodDays()).append(" days\n");
        sb.append("\n--- Current Status ---\n");
        sb.append("Active Loans: ").append(member.getActiveLoans().size()).append("\n");
        sb.append("Overdue Books: ").append(member.getOverdueBookCount()).append("\n");
        sb.append("Current Fees: $").append(String.format("%.2f", member.calculateCurrentOverdueFees())).append("\n");
        sb.append("Total Fees: $").append(String.format("%.2f", member.calculateTotalOverdueFees()));
        
        details.setContentText(sb.toString());
        details.showAndWait();
    }
    
    /**
     * Handle delete member
     */
    private void handleDeleteMember(Member member) {
        if (member == null) return;
        
        if (!member.getActiveLoans().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cannot Remove", 
                "This member has active loans. Please process returns first.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Member");
        confirm.setHeaderText("Remove " + member.getName() + " " + member.getSurname() + "?");
        confirm.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            library.removeMember(member.getId());
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member removed successfully!");
            showManageMembers();
        }
    }
    
    /**
     * Show loans and returns view
     */
    private void showLoansReturns() {
        VBox content = new VBox(20);
        
        Text title = new Text("Loans & Returns");
        title.getStyleClass().add("title-label");
        
        // Issue book section
        VBox issueSection = createIssueBookSection();
        
        // All active loans table
        Label loansLabel = new Label("All Active Loans");
        loansLabel.getStyleClass().add("section-label");
        
        TableView<LoanWithMember> loansTable = createAllLoansTable();
        VBox.setVgrow(loansTable, Priority.ALWAYS);
        
        // Populate with all loans
        ObservableList<LoanWithMember> allLoans = FXCollections.observableArrayList();
        for (Member m : library.getAllMembers()) {
            for (Loan loan : m.getActiveLoans()) {
                allLoans.add(new LoanWithMember(loan, m));
            }
        }
        loansTable.setItems(allLoans);
        
        // Return button
        Button returnBtn = new Button("Process Return");
        returnBtn.getStyleClass().add("danger-button");
        returnBtn.setDisable(true);
        returnBtn.setOnAction(e -> {
            LoanWithMember selected = loansTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                handleProcessReturn(selected);
                showLoansReturns(); // Refresh
            }
        });
        
        loansTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            returnBtn.setDisable(selected == null);
        });
        
        content.getChildren().addAll(title, issueSection, loansLabel, loansTable, returnBtn);
        setContent(content);
    }
    
    /**
     * Create issue book section
     */
    private VBox createIssueBookSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("glass-card");
        
        Label header = new Label("Issue Book to Member");
        header.getStyleClass().add("section-label");
        
        HBox form = new HBox(15);
        form.setAlignment(Pos.CENTER_LEFT);
        
        ComboBox<Member> memberCombo = new ComboBox<>();
        memberCombo.setPromptText("Select Member");
        memberCombo.getItems().addAll(library.getAllMembers());
        memberCombo.setCellFactory(lv -> new ListCell<Member>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName() + " " + item.getSurname());
            }
        });
        memberCombo.setButtonCell(new ListCell<Member>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select Member" : item.getName() + " " + item.getSurname());
            }
        });
        
        ComboBox<Book> bookCombo = new ComboBox<>();
        bookCombo.setPromptText("Select Book");
        bookCombo.getItems().addAll(library.getAllBooks().stream().filter(Book::isAvailable).toList());
        bookCombo.setCellFactory(lv -> new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle() + " (" + item.getAvailableCopies() + " available)");
            }
        });
        bookCombo.setButtonCell(new ListCell<Book>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select Book" : item.getTitle());
            }
        });
        
        Button issueBtn = new Button("Issue Book");
        issueBtn.getStyleClass().add("primary-button");
        issueBtn.setOnAction(e -> {
            Member selectedMember = memberCombo.getValue();
            Book selectedBook = bookCombo.getValue();
            if (selectedMember != null && selectedBook != null) {
                handleIssueBook(selectedMember, selectedBook);
                showLoansReturns(); // Refresh
            } else {
                showAlert(Alert.AlertType.WARNING, "Selection Required", 
                    "Please select both a member and a book.");
            }
        });
        
        form.getChildren().addAll(memberCombo, bookCombo, issueBtn);
        section.getChildren().addAll(header, form);
        return section;
    }
    
    /**
     * Handle issue book
     */
    private void handleIssueBook(Member member, Book book) {
        if (!member.canBorrowMore(member.getActiveLoans().size())) {
            showAlert(Alert.AlertType.WARNING, "Limit Reached", 
                member.getName() + " has reached their loan limit of " + member.getMaxLoanLimit() + " books.");
            return;
        }
        
        librarian.issueBook(member.getId(), book.getIsbn());
        library.saveAllData();
        showAlert(Alert.AlertType.INFORMATION, "Success", 
            "Book issued successfully!\n\n" +
            "Book: " + book.getTitle() + "\n" +
            "Member: " + member.getName() + " " + member.getSurname() + "\n" +
            "Due: " + LocalDate.now().plusDays(member.getLoanPeriodDays()));
    }
    
    /**
     * Create all loans table
     */
    @SuppressWarnings("unchecked")
    private TableView<LoanWithMember> createAllLoansTable() {
        TableView<LoanWithMember> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<LoanWithMember, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().loan.getBook().getTitle()));
        bookCol.setPrefWidth(200);
        
        TableColumn<LoanWithMember, String> memberCol = new TableColumn<>("Member");
        memberCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().member.getName() + " " + data.getValue().member.getSurname()));
        memberCol.setPrefWidth(150);
        
        TableColumn<LoanWithMember, String> borrowCol = new TableColumn<>("Borrowed");
        borrowCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().loan.getLoanDate().format(DateTimeFormatter.ofPattern("MMM dd"))));
        borrowCol.setPrefWidth(80);
        
        TableColumn<LoanWithMember, String> dueCol = new TableColumn<>("Due Date");
        dueCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().loan.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        dueCol.setPrefWidth(100);
        
        TableColumn<LoanWithMember, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            Loan loan = data.getValue().loan;
            if (loan.isOverdue()) {
                return new SimpleStringProperty("⚠️ OVERDUE (" + loan.getDaysOverdue() + "d)");
            }
            long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate());
            return new SimpleStringProperty("✓ " + days + " days left");
        });
        statusCol.setPrefWidth(120);
        
        TableColumn<LoanWithMember, String> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("$%.2f", data.getValue().loan.calculateOverdueFee(data.getValue().member.getDailyOverdueFee()))));
        feeCol.setPrefWidth(70);
        
        table.getColumns().addAll(bookCol, memberCol, borrowCol, dueCol, statusCol, feeCol);
        
        return table;
    }
    
    /**
     * Handle process return
     */
    private void handleProcessReturn(LoanWithMember loanWithMember) {
        Loan loan = loanWithMember.loan;
        Member member = loanWithMember.member;
        double fee = loan.calculateOverdueFee(member.getDailyOverdueFee());
        
        String message = "Process return for:\n\n" +
            "Book: " + loan.getBook().getTitle() + "\n" +
            "Member: " + member.getName() + " " + member.getSurname();
        
        if (fee > 0) {
            message += "\n\n⚠️ Overdue fee: $" + String.format("%.2f", fee);
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Process Return");
        confirm.setHeaderText("Confirm Return");
        confirm.setContentText(message);
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            librarian.returnBook(member.getId(), loan.getBook().getIsbn());
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Book returned successfully!" + (fee > 0 ? "\nFee collected: $" + String.format("%.2f", fee) : ""));
        }
    }
    
    /**
     * Show reports view
     */
    private void showReports() {
        VBox content = new VBox(20);
        
        Text title = new Text("Library Reports");
        title.getStyleClass().add("title-label");
        
        // Overdue loans section
        VBox overdueSection = createOverdueSection();
        
        // Statistics section
        VBox statsSection = createStatisticsSection();
        
        // Export buttons
        HBox exportButtons = new HBox(15);
        Button exportInventory = new Button("Export Inventory");
        exportInventory.getStyleClass().add("glass-button");
        exportInventory.setOnAction(e -> {
            FileManager.saveBooksToText(library.getAllBooks());
            showAlert(Alert.AlertType.INFORMATION, "Export Complete", "Inventory exported to books.txt");
        });
        
        Button exportReport = new Button("Export Full Report");
        exportReport.getStyleClass().add("primary-button");
        exportReport.setOnAction(e -> {
            FileManager.exportLibraryReport(library, "library_report.txt");
            showAlert(Alert.AlertType.INFORMATION, "Export Complete", "Report exported to library_report.txt");
        });
        
        exportButtons.getChildren().addAll(exportInventory, exportReport);
        
        content.getChildren().addAll(title, overdueSection, statsSection, exportButtons);
        setContent(content);
    }
    
    /**
     * Create overdue section
     */
    private VBox createOverdueSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("glass-card");
        
        Label header = new Label("⚠️ Overdue Loans");
        header.getStyleClass().add("section-label");
        
        List<LoanWithMember> overdueLoans = library.getAllMembers().stream()
            .flatMap(m -> m.getActiveLoans().stream()
                .filter(Loan::isOverdue)
                .map(loan -> new LoanWithMember(loan, m)))
            .toList();
        
        if (overdueLoans.isEmpty()) {
            Label noOverdue = new Label("✓ No overdue loans! All books are returned on time.");
            noOverdue.getStyleClass().add("success-label");
            section.getChildren().addAll(header, noOverdue);
        } else {
            VBox list = new VBox(8);
            for (LoanWithMember lm : overdueLoans) {
                HBox row = new HBox(15);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(10));
                row.setStyle("-fx-background-color: rgba(239,68,68,0.1); -fx-background-radius: 10;");
                
                Label icon = new Label("📕");
                icon.setStyle("-fx-font-size: 18px;");
                
                VBox info = new VBox(3);
                Label bookTitle = new Label(lm.loan.getBook().getTitle());
                bookTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                Label details = new Label(lm.member.getName() + " | " + lm.loan.getDaysOverdue() + " days overdue | $" + 
                    String.format("%.2f", lm.loan.calculateOverdueFee(lm.member.getDailyOverdueFee())));
                details.getStyleClass().add("muted-label");
                info.getChildren().addAll(bookTitle, details);
                HBox.setHgrow(info, Priority.ALWAYS);
                
                row.getChildren().addAll(icon, info);
                list.getChildren().add(row);
            }
            section.getChildren().addAll(header, list);
        }
        
        return section;
    }
    
    /**
     * Create statistics section
     */
    private VBox createStatisticsSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("glass-card");
        
        Label header = new Label("📊 Library Statistics");
        header.getStyleClass().add("section-label");
        
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(12);
        
        // Calculate statistics
        int totalBooks = library.getTotalBooks();
        int totalCopies = countTotalCopies();
        int availableCopies = countAvailableCopies();
        int totalMembers = library.getTotalMembers();
        int activeLoans = library.getActiveLoansCount();
        int overdueCount = countOverdueLoans();
        double totalFees = calculateTotalFees();
        
        int basicMembers = (int) library.getAllMembers().stream()
            .filter(m -> m.getMembershipPlan().getPlanType() == MembershipPlan.PlanType.BASIC).count();
        int premiumMembers = (int) library.getAllMembers().stream()
            .filter(m -> m.getMembershipPlan().getPlanType() == MembershipPlan.PlanType.PREMIUM).count();
        int vipMembers = countVIPMembers();
        
        addStatRow(grid, 0, "Total Book Titles:", String.valueOf(totalBooks));
        addStatRow(grid, 1, "Total Copies:", String.valueOf(totalCopies));
        addStatRow(grid, 2, "Available Copies:", String.valueOf(availableCopies));
        addStatRow(grid, 3, "Utilization Rate:", String.format("%.1f%%", 
            totalCopies > 0 ? ((totalCopies - availableCopies) * 100.0 / totalCopies) : 0));
        
        grid.add(new Label(""), 0, 4); // Spacer
        
        addStatRow(grid, 5, "Total Members:", String.valueOf(totalMembers));
        addStatRow(grid, 6, "Basic Members:", String.valueOf(basicMembers));
        addStatRow(grid, 7, "Premium Members:", String.valueOf(premiumMembers));
        addStatRow(grid, 8, "VIP Members:", String.valueOf(vipMembers));
        
        grid.add(new Label(""), 0, 9); // Spacer
        
        addStatRow(grid, 10, "Active Loans:", String.valueOf(activeLoans));
        addStatRow(grid, 11, "Overdue Loans:", String.valueOf(overdueCount));
        addStatRow(grid, 12, "Total Fees Due:", "$" + String.format("%.2f", totalFees));
        
        section.getChildren().addAll(header, grid);
        return section;
    }
    
    /**
     * Add stat row to grid
     */
    private void addStatRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("body-label");
        Label valueNode = new Label(value);
        valueNode.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    /**
     * Handle logout
     */
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            app.logout();
        }
    }
    
    /**
     * Set content in the main area with animation
     */
    private void setContent(VBox content) {
        content.setOpacity(0);
        
        contentArea.getChildren().clear();
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("glass-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        contentArea.getChildren().add(scrollPane);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    /**
     * Show alert dialog
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Get the root view
     */
    public BorderPane getView() {
        return rootPane;
    }
    
    /**
     * Helper class to associate Loan with Member
     */
    private static class LoanWithMember {
        Loan loan;
        Member member;
        
        LoanWithMember(Loan loan, Member member) {
            this.loan = loan;
            this.member = member;
        }
    }
}
