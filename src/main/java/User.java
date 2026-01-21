import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Abstract base class for all users in the Library Management System.
 * Provides common attributes, validation, authentication, and polymorphic plan management.
 */
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Role {
        LIBRARIAN,
        MEMBER
    }

    // Attributes
    protected final int id;
    protected final String name;
    protected final String surname;
    protected int age;
    protected String email;
    protected String passwordHash;  // Store hashed password for security
    protected Role role;
    protected MembershipPlan membershipPlan;
    protected boolean isLoggedIn;
    protected java.time.LocalDateTime lastLoginTime;

    private static int baseId = 100;

    // Constructors
    
    /**
     * Full constructor with all fields
     */
    public User(String name, String surname, int age, String email, String password, Role role) {
        validateName(name);
        validateSurname(surname);
        validateAge(age);
        validateEmail(email);
        validatePassword(password);
        validateRole(role);

        this.id = baseId++;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.email = email;
        this.passwordHash = hashPassword(password);
        this.role = role;
        this.isLoggedIn = false;
        this.lastLoginTime = null;
        
        // Assign plan using polymorphic method
        this.membershipPlan = assignDefaultPlan();
    }

    /**
     * Constructor without email and password
     */
    public User(String name, String surname, int age, Role role) {
        validateName(name);
        validateSurname(surname);
        validateAge(age);
        validateRole(role);

        this.id = baseId++;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.email = null;
        this.passwordHash = null;
        this.role = role;
        this.isLoggedIn = false;
        this.lastLoginTime = null;
        
        // Assign plan using polymorphic method
        this.membershipPlan = assignDefaultPlan();
    }

    // Validation Methods
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty");
        }
    }

    private void validateAge(int age) {
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Age must be between 1 and 120");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address format");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    private void validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
    }

    // ==================== PASSWORD HASHING ====================
    
    /**
     * Hash a password using SHA-256
     */
    protected static String hashPassword(String password) {
        if (password == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple encoding if SHA-256 not available
            return password;
        }
    }
    
    /**
     * Verify a password against the stored hash
     */
    protected boolean verifyPassword(String password) {
        if (password == null || passwordHash == null) return false;
        return passwordHash.equals(hashPassword(password));
    }

    // ==================== AUTHENTICATION ====================
    
    /**
     * Login with email and password
     * @return true if login successful
     */
    public boolean login(String email, String password) {
        if (this.email == null || this.passwordHash == null) {
            System.out.println("Error: No credentials set for this user.");
            return false;
        }
        
        if (!this.email.equalsIgnoreCase(email)) {
            System.out.println("Error: Invalid email.");
            return false;
        }
        
        if (!verifyPassword(password)) {
            System.out.println("Error: Invalid password.");
            return false;
        }
        
        this.isLoggedIn = true;
        this.lastLoginTime = java.time.LocalDateTime.now();
        System.out.println("✓ Login successful! Welcome, " + name + " " + surname);
        onLogin(); // Polymorphic callback
        return true;
    }
    
    /**
     * Logout the user
     */
    public void logout() {
        if (isLoggedIn) {
            this.isLoggedIn = false;
            System.out.println("✓ Logged out successfully. Goodbye, " + name + "!");
            onLogout(); // Polymorphic callback
        } else {
            System.out.println("You are not logged in.");
        }
    }
    
    /**
     * Check if user is currently logged in
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * Get last login time
     */
    public java.time.LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    
    /**
     * Polymorphic callback - called after successful login
     * Subclasses can override to add specific behavior
     */
    protected void onLogin() {
        // Default implementation - can be overridden
    }
    
    /**
     * Polymorphic callback - called after logout
     * Subclasses can override to add specific behavior
     */
    protected void onLogout() {
        // Default implementation - can be overridden
    }

    // ==================== POLYMORPHIC PLAN METHODS ====================
    
    /**
     * Abstract method to assign default membership plan.
     * Member and Librarian implement this differently (polymorphism).
     */
    protected abstract MembershipPlan assignDefaultPlan();
    
    /**
     * Get the user's membership plan
     */
    public MembershipPlan getMembershipPlan() {
        return membershipPlan;
    }
    
    /**
     * Check if user can borrow more books (polymorphic - uses plan limits)
     */
    public abstract boolean canBorrowMore(int currentLoans);
    
    /**
     * Get the maximum number of books this user can borrow
     */
    public int getMaxLoanLimit() {
        return membershipPlan != null ? membershipPlan.getMaxLoans() : 3;
    }
    
    /**
     * Get the loan period in days for this user
     */
    public int getLoanPeriodDays() {
        return membershipPlan != null ? membershipPlan.getLoanPeriodDays() : 14;
    }
    
    /**
     * Get the daily overdue fee for this user
     */
    public double getDailyOverdueFee() {
        return membershipPlan != null ? membershipPlan.getDailyOverdueFee() : 0.50;
    }
    
    /**
     * Display membership plan details
     */
    public void displayPlanDetails() {
        if (membershipPlan != null) {
            membershipPlan.displayBenefits();
        } else {
            System.out.println("No membership plan assigned.");
        }
    }

    // Getters
    
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public int getAge() {
        return this.age;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    /**
     * @deprecated Password hash should not be exposed directly
     */
    @Deprecated
    public String getPassword() {
        return this.passwordHash;
    }
    
    public Role getRole() {
        return this.role;
    }

    // Setters
    
    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }
    
    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }
    
    public void setPassword(String password) {
        validatePassword(password);
        this.passwordHash = hashPassword(password);
    }
    
    /**
     * Set credentials (email and password together)
     */
    public void setCredentials(String email, String password) {
        validateEmail(email);
        validatePassword(password);
        this.email = email;
        this.passwordHash = hashPassword(password);
        System.out.println("✓ Credentials updated for " + name + " " + surname);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', surname='" + surname + 
               "', age=" + age + ", role=" + role + 
               ", plan=" + (membershipPlan != null ? membershipPlan.getPlanName() : "None") + "}";
    }
}
