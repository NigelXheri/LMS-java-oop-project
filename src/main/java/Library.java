
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JavaFX GUI for Library Management System
 * Complete interface with Librarian and Member modes
 */
public class LibraryManagementGUI extends Application {

    private Library library;
    private Stage primaryStage;

    // Observable lists for TableViews
    private ObservableList<Book> booksList;
    private ObservableList<Member> membersList;
    private ObservableList<Loan> loansList;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.library = new Library();

        // Initialize observable lists
        booksList = FXCollections.observableArrayList();
        membersList = FXCollections.observableArrayList();
        loansList = FXCollections.observableArrayList();

        // Load existing data
        try {
            library.loadAllData();
            refreshAllLists();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Library data loaded successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.INFORMATION, "New Start", "No existing data found. Starting fresh.");
        }

        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(createLoginScene());
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            handleExit();
        });
        primaryStage.show();
    }

    // ==================== LOGIN SCENE ====================

    private Scene createLoginScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2c3e50;");

        // Title
        Label titleLabel = new Label("Library Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Welcome! Please select your role");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        // Buttons
        Button librarianBtn = createStyledButton("Librarian Login", "#3498db");
        Button memberBtn = createStyledButton("Member Login", "#2ecc71");
        Button exitBtn = createStyledButton("Exit", "#e74c3c");

        librarianBtn.setOnAction(e -> showLibrarianLogin());
        memberBtn.setOnAction(e -> showMemberLogin());
        exitBtn.setOnAction(e -> handleExit());

        root.getChildren().addAll(titleLabel, subtitleLabel, librarianBtn, memberBtn, exitBtn);

        return new Scene(root, 600, 400);
    }

    private void showLibrarianLogin() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Librarian Login");
        dialog.setHeaderText("Enter your Employee ID");

        TextField empIdField = new TextField();
        empIdField.setPromptText("Employee ID");

        dialog.getDialogPane().setContent(new VBox(10, new Label("Employee ID:"), empIdField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return empIdField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(empId -> {
            if (!empId.trim().isEmpty()) {
                Librarian librarian = new Librarian("Admin", "admin@library.com", 30, empId, library);
                primaryStage.setScene(createLibrarianDashboard(librarian));
            }
        });
    }

    private void showMemberLogin() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Member Login");
        dialog.setHeaderText("Enter your Member ID");

        TextField memberIdField = new TextField();
        memberIdField.setPromptText("Member ID");

        dialog.getDialogPane().setContent(new VBox(10, new Label("Member ID:"), memberIdField));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return memberIdField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(memberId -> {
            Member member = library.findMemberById(memberId);
            if (member != null) {
                primaryStage.setScene(createMemberDashboard(member));
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Member not found!");
            }
        });
    }
