public abstract class user {

    protected final int id;
    protected String name;
    protected String surname;

    private static int base_id = 100;

    public user(String name, String surname) {
        this.id = base_id++;
        this.name = name;
        this.surname = surname;
    }

    // GET methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    // POLYMORPHISM (abstract method)
    public abstract String getRole();

    // POLYMORPHISM (method override)
    @Override
    public String toString() {
        return getRole() + ": " + name + " " + surname + " (ID: " + id + ")";
    }
}

