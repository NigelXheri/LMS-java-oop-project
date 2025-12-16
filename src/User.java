public abstract class User {

    public enum Role {
        LIBRARIAN,
        MEMBER
    }

    // 1. Attributes
    protected final int id;
    protected final String name;
    protected final String surname;
    protected int age;
    protected String email;
    protected String password;
    protected Role role;


    private static int base_id = 100;

    // 2. Constructor
    public User(String name, String surname, int age, String email, String password, Role role) {
        validateName(name);
        validateSurname(surname);
        validateAge(age);
        validateEmail(email);
        validatePassword(password);
        validateRole(role);

        this.id = base_id++;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String name, String surname, int age, Role role) {
        validateName(name);
        validateSurname(surname);
        validateAge(age);
        validateRole(role);

        this.id = base_id++;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.role = role;
    }


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
        if (age <= 0 || age > 110) {
            throw new IllegalArgumentException("Age must be between 1 and 110");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email address");
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


    // 3.1 Getters
    public int getId(){
        return this.id;
    }
    public String getName(){
        return this.name;
    }
    public String getSurname(){
        return this.surname;
    }
    public int getAge(){
        return this.age;
    }

    // 3.2 SET Methods
    public void setAge(int age) {
        this.age = age;
    }

}
