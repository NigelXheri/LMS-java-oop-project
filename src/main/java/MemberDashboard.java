import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
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
 * MemberDashboard - Dashboard for library members
 * Features: Browse books, borrow/return, view loans, manage account, upgrade plan
 */
public class MemberDashboard {
    
    private LibraryApp app;
    private Library library;
    private Member member;
    private BorderPane rootPane;
    private StackPane contentArea;
    
    // Sidebar buttons for active state management
    private Button activeButton;
    private Button btnDashboard, btnBrowse, btnMyLoans, btnAccount, btnLogout;
    
    public MemberDashboard(LibraryApp app, Library library, Member member) {
        this.app = app;
        this.library = library;
        this.member = member;
        createView();
    }
    
    /**
     * Create the main dashboard view
     */
    private void createView() {
        rootPane = new BorderPane();
        rootPane.getStyleClass().add("gradient-bg-member");
        
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
        Circle circle1 = new Circle(150, Color.rgb(59, 130, 246, 0.15));
        circle1.setEffect(new GaussianBlur(60));
        StackPane.setAlignment(circle1, Pos.TOP_RIGHT);
        StackPane.setMargin(circle1, new Insets(-50, -50, 0, 0));
        
        Circle circle2 = new Circle(200, Color.rgb(99, 102, 241, 0.1));
        circle2.setEffect(new GaussianBlur(80));
        StackPane.setAlignment(circle2, Pos.BOTTOM_LEFT);
        StackPane.setMargin(circle2, new Insets(0, 0, -70, -70));
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
        btnBrowse = createSidebarButton("📚  Browse Books", () -> { showBrowseBooks(); setActiveButton(btnBrowse); });
        btnMyLoans = createSidebarButton("📖  My Loans", () -> { showMyLoans(); setActiveButton(btnMyLoans); });
        btnAccount = createSidebarButton("👤  My Account", () -> { showAccount(); setActiveButton(btnAccount); });
        
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
            btnBrowse,
            btnMyLoans,
            btnAccount,
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
        Circle circle = new Circle(35, Color.web("#3b82f6"));
        Text initials = new Text(member.getName().substring(0, 1).toUpperCase());
        initials.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: white;");
        avatar.getChildren().addAll(circle, initials);
        
        // Name
        Label name = new Label(member.getName() + " " + member.getSurname());
        name.getStyleClass().add("section-label");
        
        // Plan badge
        Label planBadge = new Label(member.getMembershipPlan().getPlanName());
        planBadge.getStyleClass().addAll("badge", getPlanBadgeClass());
        
        box.getChildren().addAll(avatar, name, planBadge);
        return box;
    }
    
    /**
     * Get CSS class for plan badge
     */
    private String getPlanBadgeClass() {
        return switch (member.getMembershipPlan().getPlanType()) {
            case VIP -> "badge-warning";
            case PREMIUM -> "badge-primary";
            case STAFF -> "badge-success";
            default -> "badge-primary";
        };
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
        Text welcome = new Text("Welcome back, " + member.getName() + "!");
        welcome.getStyleClass().add("title-label");
        
        // Stats cards
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createStatCard("📚", "Books Borrowed", String.valueOf(member.getActiveLoans().size())),
            createStatCard("⏰", "Days Until Due", getNextDueDate()),
            createStatCard("💰", "Outstanding Fees", String.format("$%.2f", member.calculateCurrentOverdueFees())),
            createStatCard("📖", "Loan Limit", member.getActiveLoans().size() + "/" + member.getMaxLoanLimit())
        );
        
        // Quick actions
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.getStyleClass().add("section-label");
        
        HBox quickActions = new HBox(15);
        Button browseBtn = new Button("Browse Books");
        browseBtn.getStyleClass().add("primary-button");
        browseBtn.setOnAction(e -> { showBrowseBooks(); setActiveButton(btnBrowse); });
        
        Button returnBtn = new Button("Return a Book");
        returnBtn.getStyleClass().add("glass-button");
        returnBtn.setOnAction(e -> { showMyLoans(); setActiveButton(btnMyLoans); });
        
        Button upgradeBtn = new Button("Upgrade Plan");
        upgradeBtn.getStyleClass().add("success-button");
        upgradeBtn.setOnAction(e -> { showAccount(); setActiveButton(btnAccount); });
        
        quickActions.getChildren().addAll(browseBtn, returnBtn, upgradeBtn);
        
        // Current loans preview
        Label loansLabel = new Label("Current Loans");
        loansLabel.getStyleClass().add("section-label");
        
        VBox loansPreview = createLoansPreview();
        
        content.getChildren().addAll(welcome, statsRow, quickActionsLabel, quickActions, loansLabel, loansPreview);
        
        setContent(content);
    }
    
    /**
     * Get next due date string
     */
    private String getNextDueDate() {
        List<Loan> loans = member.getActiveLoans();
        if (loans.isEmpty()) return "N/A";
        
        LocalDate earliest = loans.stream()
            .map(Loan::getDueDate)
            .min(LocalDate::compareTo)
            .orElse(null);
        
        if (earliest == null) return "N/A";
        
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), earliest);
        if (days < 0) return "OVERDUE!";
        if (days == 0) return "Today";
        if (days == 1) return "Tomorrow";
        return days + " days";
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
     * Create loans preview for dashboard
     */
    private VBox createLoansPreview() {
        VBox box = new VBox(10);
        box.getStyleClass().add("glass-card");
        
        List<Loan> loans = member.getActiveLoans();
        
        if (loans.isEmpty()) {
            Label noLoans = new Label("You don't have any active loans. Start browsing books!");
            noLoans.getStyleClass().add("body-label");
            box.getChildren().add(noLoans);
        } else {
            for (int i = 0; i < Math.min(3, loans.size()); i++) {
                Loan loan = loans.get(i);
                HBox loanRow = createLoanRow(loan);
                box.getChildren().add(loanRow);
            }
            
            if (loans.size() > 3) {
                Label moreLabel = new Label("+" + (loans.size() - 3) + " more loans...");
                moreLabel.getStyleClass().add("muted-label");
                box.getChildren().add(moreLabel);
            }
        }
        
        return box;
    }
    
    /**
     * Create a loan row for preview
     */
    private HBox createLoanRow(Loan loan) {
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
        
        Label dueDate = new Label("Due: " + loan.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dueDate.getStyleClass().add("muted-label");
        
        bookInfo.getChildren().addAll(title, dueDate);
        HBox.setHgrow(bookInfo, Priority.ALWAYS);
        
        // Status badge
        Label status = new Label(loan.isOverdue() ? "OVERDUE" : "Active");
        status.getStyleClass().addAll("badge", loan.isOverdue() ? "badge-danger" : "badge-success");
        
        row.getChildren().addAll(bookIcon, bookInfo, status);
        return row;
    }
    
    /**
     * Show browse books view
     */
    private void showBrowseBooks() {
        VBox content = new VBox(20);
        
        // Header with search
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Browse Books");
        title.getStyleClass().add("title-label");
        HBox.setHgrow(title, Priority.ALWAYS);
        
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Search books...");
        searchField.getStyleClass().add("glass-text-field");
        searchField.setPrefWidth(300);
        
        header.getChildren().addAll(title, searchField);
        
        // Books table
        TableView<Book> booksTable = createBooksTable();
        VBox.setVgrow(booksTable, Priority.ALWAYS);
        
        // Load all available books
        ObservableList<Book> books = FXCollections.observableArrayList(library.getAllBooks());
        booksTable.setItems(books);
        
        // Search functionality
        searchField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.isEmpty()) {
                booksTable.setItems(FXCollections.observableArrayList(library.getAllBooks()));
            } else {
                List<Book> filtered = library.getAllBooks().stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                                b.getAuthor().toLowerCase().contains(newValue.toLowerCase()))
                    .toList();
                booksTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
        
        // Borrow button
        Button borrowBtn = new Button("Borrow Selected Book");
        borrowBtn.getStyleClass().add("primary-button");
        borrowBtn.setDisable(true);
        borrowBtn.setOnAction(e -> handleBorrowBook(booksTable.getSelectionModel().getSelectedItem()));
        
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            borrowBtn.setDisable(selected == null);
        });
        
        content.getChildren().addAll(header, booksTable, borrowBtn);
        setContent(content);
    }
    
    /**
     * Create books table
     */
    @SuppressWarnings("unchecked")
    private TableView<Book> createBooksTable() {
        TableView<Book> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(180);
        
        TableColumn<Book, String> themeCol = new TableColumn<>("Category");
        themeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTheme().toString()));
        themeCol.setPrefWidth(120);
        
        TableColumn<Book, String> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getAvailableCopies() + "/" + data.getValue().getTotalCopies()));
        availableCol.setPrefWidth(100);
        
        TableColumn<Book, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().isAvailable() ? "✓ Available" : "✗ Unavailable"));
        statusCol.setPrefWidth(120);
        
        table.getColumns().addAll(titleCol, authorCol, themeCol, availableCol, statusCol);
        
        return table;
    }
    
    /**
     * Handle borrowing a book
     */
    private void handleBorrowBook(Book book) {
        if (book == null) return;
        
        if (!book.isAvailable()) {
            showAlert(Alert.AlertType.WARNING, "Book Unavailable", 
                "This book is currently not available for borrowing.");
            return;
        }
        
        if (!member.canBorrowMore(member.getActiveLoans().size())) {
            showAlert(Alert.AlertType.WARNING, "Loan Limit Reached", 
                "You have reached your loan limit of " + member.getMaxLoanLimit() + " books.\n" +
                "Return a book or upgrade your plan to borrow more.");
            return;
        }
        
        // Confirm borrow
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Borrow");
        confirm.setHeaderText("Borrow \"" + book.getTitle() + "\"?");
        confirm.setContentText("Loan period: " + member.getLoanPeriodDays() + " days\n" +
                              "Due date: " + LocalDate.now().plusDays(member.getLoanPeriodDays()));
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            member.borrowBook(book);
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "You have successfully borrowed \"" + book.getTitle() + "\"!");
            showBrowseBooks(); // Refresh
        }
    }
    
    /**
     * Show my loans view
     */
    private void showMyLoans() {
        VBox content = new VBox(20);
        
        Text title = new Text("My Loans");
        title.getStyleClass().add("title-label");
        
        // Loans table
        TableView<Loan> loansTable = createLoansTable();
        VBox.setVgrow(loansTable, Priority.ALWAYS);
        
        ObservableList<Loan> loans = FXCollections.observableArrayList(member.getActiveLoans());
        loansTable.setItems(loans);
        
        // Return button
        Button returnBtn = new Button("Return Selected Book");
        returnBtn.getStyleClass().add("danger-button");
        returnBtn.setDisable(true);
        returnBtn.setOnAction(e -> handleReturnBook(loansTable.getSelectionModel().getSelectedItem()));
        
        loansTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            returnBtn.setDisable(selected == null);
        });
        
        // Fees summary
        HBox feesSummary = createFeesSummary();
        
        content.getChildren().addAll(title, loansTable, returnBtn, feesSummary);
        setContent(content);
    }
    
    /**
     * Create loans table
     */
    @SuppressWarnings("unchecked")
    private TableView<Loan> createLoansTable() {
        TableView<Loan> table = new TableView<>();
        table.getStyleClass().add("glass-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Loan, String> titleCol = new TableColumn<>("Book Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getBook().getTitle()));
        titleCol.setPrefWidth(250);
        
        TableColumn<Loan, String> borrowDateCol = new TableColumn<>("Borrowed On");
        borrowDateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getLoanDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        borrowDateCol.setPrefWidth(120);
        
        TableColumn<Loan, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        dueDateCol.setPrefWidth(120);
        
        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            Loan loan = data.getValue();
            if (loan.isOverdue()) {
                return new SimpleStringProperty("⚠️ OVERDUE (" + loan.getDaysOverdue() + " days)");
            } else {
                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), loan.getDueDate());
                return new SimpleStringProperty("✓ " + daysLeft + " days left");
            }
        });
        statusCol.setPrefWidth(150);
        
        TableColumn<Loan, String> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("$%.2f", data.getValue().calculateOverdueFee(member.getDailyOverdueFee()))));
        feeCol.setPrefWidth(80);
        
        table.getColumns().addAll(titleCol, borrowDateCol, dueDateCol, statusCol, feeCol);
        
        return table;
    }
    
    /**
     * Handle returning a book
     */
    private void handleReturnBook(Loan loan) {
        if (loan == null) return;
        
        double fee = loan.calculateOverdueFee(member.getDailyOverdueFee());
        String message = "Return \"" + loan.getBook().getTitle() + "\"?";
        if (fee > 0) {
            message += "\n\nOverdue fee: $" + String.format("%.2f", fee);
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Return");
        confirm.setHeaderText("Return Book");
        confirm.setContentText(message);
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            member.returnBook(loan.getBook());
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Book returned successfully!" + (fee > 0 ? "\nFee charged: $" + String.format("%.2f", fee) : ""));
            showMyLoans(); // Refresh
        }
    }
    
    /**
     * Create fees summary panel
     */
    private HBox createFeesSummary() {
        HBox box = new HBox(30);
        box.getStyleClass().add("glass-card");
        box.setAlignment(Pos.CENTER_LEFT);
        
        VBox current = new VBox(5);
        Label currentLabel = new Label("Current Overdue Fees");
        currentLabel.getStyleClass().add("muted-label");
        Label currentValue = new Label(String.format("$%.2f", member.calculateCurrentOverdueFees()));
        currentValue.getStyleClass().add("heading-label");
        current.getChildren().addAll(currentLabel, currentValue);
        
        VBox total = new VBox(5);
        Label totalLabel = new Label("Total Accumulated Fees");
        totalLabel.getStyleClass().add("muted-label");
        Label totalValue = new Label(String.format("$%.2f", member.calculateTotalOverdueFees()));
        totalValue.getStyleClass().add("heading-label");
        total.getChildren().addAll(totalLabel, totalValue);
        
        VBox rate = new VBox(5);
        Label rateLabel = new Label("Your Daily Rate");
        rateLabel.getStyleClass().add("muted-label");
        Label rateValue = new Label(String.format("$%.2f/day", member.getDailyOverdueFee()));
        rateValue.getStyleClass().add("heading-label");
        rate.getChildren().addAll(rateLabel, rateValue);
        
        box.getChildren().addAll(current, total, rate);
        return box;
    }
    
    /**
     * Show account management view
     */
    private void showAccount() {
        VBox content = new VBox(25);
        
        Text title = new Text("My Account");
        title.getStyleClass().add("title-label");
        
        // Account info card
        VBox accountCard = createAccountInfoCard();
        
        // Membership plan card
        VBox planCard = createPlanCard();
        
        // Plan upgrade section
        VBox upgradeSection = createUpgradeSection();
        
        content.getChildren().addAll(title, accountCard, planCard, upgradeSection);
        setContent(content);
    }
    
    /**
     * Create account info card
     */
    private VBox createAccountInfoCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("glass-card");
        
        Label header = new Label("Account Information");
        header.getStyleClass().add("section-label");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        
        addInfoRow(grid, 0, "Name:", member.getName() + " " + member.getSurname());
        addInfoRow(grid, 1, "Email:", member.getEmail() != null ? member.getEmail() : "Not set");
        addInfoRow(grid, 2, "Member ID:", String.valueOf(member.getId()));
        addInfoRow(grid, 3, "Age:", String.valueOf(member.getAge()));
        
        card.getChildren().addAll(header, grid);
        return card;
    }
    
    /**
     * Add info row to grid
     */
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("muted-label");
        Label valueNode = new Label(value);
        valueNode.getStyleClass().add("body-label");
        valueNode.setStyle("-fx-font-weight: bold;");
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    /**
     * Create membership plan card
     */
    private VBox createPlanCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("glass-card");
        
        Label header = new Label("Current Membership Plan");
        header.getStyleClass().add("section-label");
        
        MembershipPlan plan = member.getMembershipPlan();
        
        HBox planInfo = new HBox(30);
        planInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Plan name and badge
        VBox nameBox = new VBox(5);
        Label planName = new Label(plan.getPlanName());
        planName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label planBadge = new Label(plan.getPlanType().name());
        planBadge.getStyleClass().addAll("badge", getPlanBadgeClass());
        nameBox.getChildren().addAll(planName, planBadge);
        
        // Plan benefits
        VBox benefits = new VBox(8);
        benefits.getChildren().addAll(
            createBenefitRow("📚", "Max Books: " + plan.getMaxLoans()),
            createBenefitRow("📅", "Loan Period: " + plan.getLoanPeriodDays() + " days"),
            createBenefitRow("💰", "Daily Fee: $" + String.format("%.2f", plan.getDailyOverdueFee())),
            createBenefitRow("💳", "Monthly: $" + String.format("%.2f", plan.getMonthlyFee()))
        );
        
        planInfo.getChildren().addAll(nameBox, benefits);
        card.getChildren().addAll(header, planInfo);
        return card;
    }
    
    /**
     * Create benefit row
     */
    private HBox createBenefitRow(String icon, String text) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLabel = new Label(icon);
        Label textLabel = new Label(text);
        textLabel.getStyleClass().add("body-label");
        row.getChildren().addAll(iconLabel, textLabel);
        return row;
    }
    
    /**
     * Create upgrade section
     */
    private VBox createUpgradeSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("glass-card");
        
        Label header = new Label("Upgrade Your Plan");
        header.getStyleClass().add("section-label");
        
        HBox plansRow = new HBox(20);
        
        // Only show plans better than current
        MembershipPlan.PlanType currentType = member.getMembershipPlan().getPlanType();
        
        if (currentType != MembershipPlan.PlanType.PREMIUM && currentType != MembershipPlan.PlanType.VIP) {
            plansRow.getChildren().add(createPlanUpgradeCard(MembershipPlan.PlanType.PREMIUM));
        }
        if (currentType != MembershipPlan.PlanType.VIP) {
            plansRow.getChildren().add(createPlanUpgradeCard(MembershipPlan.PlanType.VIP));
        }
        
        if (plansRow.getChildren().isEmpty()) {
            Label maxPlan = new Label("You already have the best plan available! 🎉");
            maxPlan.getStyleClass().add("body-label");
            section.getChildren().addAll(header, maxPlan);
        } else {
            section.getChildren().addAll(header, plansRow);
        }
        
        return section;
    }
    
    /**
     * Create plan upgrade card
     */
    private VBox createPlanUpgradeCard(MembershipPlan.PlanType planType) {
        MembershipPlan plan = new MembershipPlan(planType);
        
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: rgba(255,255,255,0.2); -fx-border-radius: 15;");
        
        Label name = new Label(plan.getPlanName());
        name.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label price = new Label("$" + String.format("%.2f", plan.getMonthlyFee()) + "/mo");
        price.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #6366f1;");
        
        VBox benefits = new VBox(5);
        benefits.setAlignment(Pos.CENTER);
        benefits.getChildren().addAll(
            new Label("📚 " + plan.getMaxLoans() + " books") {{ getStyleClass().add("muted-label"); }},
            new Label("📅 " + plan.getLoanPeriodDays() + " days") {{ getStyleClass().add("muted-label"); }},
            new Label("💰 $" + String.format("%.2f", plan.getDailyOverdueFee()) + "/day") {{ getStyleClass().add("muted-label"); }}
        );
        
        Button upgradeBtn = new Button("Upgrade");
        upgradeBtn.getStyleClass().add("primary-button");
        upgradeBtn.setOnAction(e -> handleUpgrade(planType));
        
        card.getChildren().addAll(name, price, benefits, upgradeBtn);
        return card;
    }
    
    /**
     * Handle plan upgrade
     */
    private void handleUpgrade(MembershipPlan.PlanType planType) {
        MembershipPlan newPlan = new MembershipPlan(planType);
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Upgrade");
        confirm.setHeaderText("Upgrade to " + newPlan.getPlanName() + "?");
        confirm.setContentText("Monthly fee: $" + String.format("%.2f", newPlan.getMonthlyFee()) + 
                              "\n\nBenefits:\n" +
                              "• Max books: " + newPlan.getMaxLoans() + "\n" +
                              "• Loan period: " + newPlan.getLoanPeriodDays() + " days\n" +
                              "• Daily overdue fee: $" + String.format("%.2f", newPlan.getDailyOverdueFee()));
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            member.upgradePlan(planType);
            library.saveAllData();
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "You have successfully upgraded to " + newPlan.getPlanName() + "!");
            showAccount(); // Refresh
            
            // Update sidebar badge
            createView();
            app.showMemberDashboard(member);
        }
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
}
