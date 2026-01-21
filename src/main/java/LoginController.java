import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * LoginController - Handles the login screen UI and authentication
 * Features glass-morphism design with animated elements
 */
public class LoginController {
    
    private LibraryApp app;
    private Library library;
    private StackPane rootPane;
    
    // UI Components
    private TextField emailField;
    private PasswordField passwordField;
    private Label errorLabel;
    private Button loginButton;
    
    public LoginController(LibraryApp app, Library library) {
        this.app = app;
        this.library = library;
        createView();
    }
    
    /**
     * Create the login view
     */
    private void createView() {
        rootPane = new StackPane();
        rootPane.getStyleClass().add("gradient-bg");
        
        // Add animated background circles
        addAnimatedBackground();
        
        // Create the login card
        VBox loginCard = createLoginCard();
        
        rootPane.getChildren().add(loginCard);
        StackPane.setAlignment(loginCard, Pos.CENTER);
    }
    
    /**
     * Add animated decorative circles to the background
     */
    private void addAnimatedBackground() {
        // Create decorative circles with blur effect
        Circle circle1 = createDecorativeCircle(200, Color.rgb(99, 102, 241, 0.3));
        Circle circle2 = createDecorativeCircle(150, Color.rgb(139, 92, 246, 0.25));
        Circle circle3 = createDecorativeCircle(180, Color.rgb(236, 72, 153, 0.2));
        Circle circle4 = createDecorativeCircle(120, Color.rgb(6, 182, 212, 0.25));
        
        // Position circles
        StackPane.setAlignment(circle1, Pos.TOP_LEFT);
        StackPane.setMargin(circle1, new Insets(-50, 0, 0, -50));
        
        StackPane.setAlignment(circle2, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(circle2, new Insets(0, -30, -30, 0));
        
        StackPane.setAlignment(circle3, Pos.TOP_RIGHT);
        StackPane.setMargin(circle3, new Insets(-60, -60, 0, 0));
        
        StackPane.setAlignment(circle4, Pos.BOTTOM_LEFT);
        StackPane.setMargin(circle4, new Insets(0, 0, -40, -40));
        
        rootPane.getChildren().addAll(circle1, circle2, circle3, circle4);
        
        // Animate circles
        animateCircle(circle1, -20, 20, 8);
        animateCircle(circle2, 15, -25, 10);
        animateCircle(circle3, 25, 15, 12);
        animateCircle(circle4, -15, -20, 9);
    }
    
    /**
     * Create a decorative circle with blur
     */
    private Circle createDecorativeCircle(double radius, Color color) {
        Circle circle = new Circle(radius);
        circle.setFill(color);
        circle.setEffect(new GaussianBlur(50));
        return circle;
    }
    
    /**
     * Animate a circle with floating effect
     */
    private void animateCircle(Circle circle, double deltaX, double deltaY, double duration) {
        TranslateTransition translate = new TranslateTransition(Duration.seconds(duration), circle);
        translate.setByX(deltaX);
        translate.setByY(deltaY);
        translate.setCycleCount(Animation.INDEFINITE);
        translate.setAutoReverse(true);
        translate.setInterpolator(Interpolator.EASE_BOTH);
        translate.play();
    }
    
    /**
     * Create the main login card
     */
    private VBox createLoginCard() {
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(50, 60, 50, 60));
        card.setMaxWidth(420);
        card.setMaxHeight(580);
        card.getStyleClass().add("glass-panel");
        
        // Logo/Icon
        StackPane logoContainer = createLogo();
        
        // Title
        Text title = new Text("Welcome Back");
        title.getStyleClass().add("title-label");
        
        // Subtitle
        Text subtitle = new Text("Sign in to access the Library Management System");
        subtitle.getStyleClass().add("subtitle-label");
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setWrappingWidth(300);
        
        // Email field
        VBox emailBox = createInputField("Email Address", "Enter your email");
        emailField = (TextField) emailBox.getChildren().get(1);
        
        // Password field
        VBox passwordBox = createPasswordField("Password", "Enter your password");
        passwordField = (PasswordField) passwordBox.getChildren().get(1);
        
        // Error label
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        // Login button
        loginButton = new Button("Sign In");
        loginButton.getStyleClass().add("primary-button");
        loginButton.setPrefWidth(300);
        loginButton.setPrefHeight(50);
        loginButton.setOnAction(e -> handleLogin());
        
        // Enter key support
        passwordField.setOnAction(e -> handleLogin());
        
        // Demo credentials hint
        VBox credentialsHint = createCredentialsHint();
        
        // Add all components
        card.getChildren().addAll(
            logoContainer,
            title,
            subtitle,
            new Region() {{ setPrefHeight(10); }},
            emailBox,
            passwordBox,
            errorLabel,
            loginButton,
            new Region() {{ setPrefHeight(5); }},
            credentialsHint
        );
        
        // Add entrance animation
        addEntranceAnimation(card);
        
        return card;
    }
    
    /**
     * Create the logo/icon
     */
    private StackPane createLogo() {
        StackPane container = new StackPane();
        container.setPrefSize(80, 80);
        
        Circle background = new Circle(40);
        background.getStyleClass().add("icon-circle");
        background.setFill(Color.web("#6366f1"));
        background.setEffect(new DropShadow(20, Color.rgb(99, 102, 241, 0.5)));
        
        Text icon = new Text("📚");
        icon.setStyle("-fx-font-size: 36px;");
        
        container.getChildren().addAll(background, icon);
        return container;
    }
    
    /**
     * Create an input field with label
     */
    private VBox createInputField(String labelText, String placeholder) {
        VBox box = new VBox(8);
        
        Label label = new Label(labelText);
        label.getStyleClass().add("body-label");
        
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.getStyleClass().add("glass-text-field");
        field.setPrefWidth(300);
        field.setPrefHeight(50);
        
        box.getChildren().addAll(label, field);
        return box;
    }
    
    /**
     * Create a password field with label
     */
    private VBox createPasswordField(String labelText, String placeholder) {
        VBox box = new VBox(8);
        
        Label label = new Label(labelText);
        label.getStyleClass().add("body-label");
        
        PasswordField field = new PasswordField();
        field.setPromptText(placeholder);
        field.getStyleClass().add("glass-password-field");
        field.setPrefWidth(300);
        field.setPrefHeight(50);
        
        box.getChildren().addAll(label, field);
        return box;
    }
    
    /**
     * Create demo credentials hint
     */
    private VBox createCredentialsHint() {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 10;");
        
        Label hintTitle = new Label("Demo Credentials");
        hintTitle.getStyleClass().add("muted-label");
        hintTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: rgba(255,255,255,0.7);");
        
        Label librarianHint = new Label("Librarian: fatbardh@librarian.com / admin123");
        librarianHint.getStyleClass().add("muted-label");
        
        Label memberHint = new Label("Member: alice@email.com / password123");
        memberHint.getStyleClass().add("muted-label");
        
        box.getChildren().addAll(hintTitle, librarianHint, memberHint);
        return box;
    }
    
    /**
     * Add entrance animation to the login card
     */
    private void addEntranceAnimation(VBox card) {
        card.setOpacity(0);
        card.setTranslateY(30);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), card);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(600), card);
        slideUp.setFromY(30);
        slideUp.setToY(0);
        slideUp.setInterpolator(Interpolator.EASE_OUT);
        
        ParallelTransition entrance = new ParallelTransition(fadeIn, slideUp);
        entrance.setDelay(Duration.millis(200));
        entrance.play();
    }
    
    /**
     * Handle login attempt
     */
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            shakeLoginCard();
            return;
        }
        
        // Disable button during authentication
        loginButton.setDisable(true);
        loginButton.setText("Signing in...");
        
        // Attempt authentication
        User user = library.authenticateUser(email, password);
        
        if (user != null) {
            // Successful login
            hideError();
            
            // Add success animation before navigation
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> navigateToDashboard(user));
            fadeOut.play();
            
        } else {
            // Failed login
            showError("Invalid email or password. Please try again.");
            shakeLoginCard();
            loginButton.setDisable(false);
            loginButton.setText("Sign In");
        }
    }
    
    /**
     * Navigate to appropriate dashboard based on user type
     */
    private void navigateToDashboard(User user) {
        if (user instanceof Librarian) {
            app.showLibrarianDashboard((Librarian) user);
        } else if (user instanceof Member) {
            app.showMemberDashboard((Member) user);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
        
        // Fade in error
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), errorLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    /**
     * Hide error message
     */
    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
    
    /**
     * Shake animation for invalid login
     */
    private void shakeLoginCard() {
        VBox card = (VBox) rootPane.getChildren().stream()
                .filter(node -> node instanceof VBox)
                .findFirst()
                .orElse(null);
        
        if (card == null) return;
        
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), card);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> card.setTranslateX(0));
        shake.play();
    }
    
    /**
     * Get the root view
     */
    public StackPane getView() {
        return rootPane;
    }
}
