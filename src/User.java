public abstract class User {

    // 1. Attributes
    protected final int id;
    protected final String name;
    protected final String surname;
    protected int age;

    private static int base_id = 100;

    // 2. Constructor
    public User(String name, String surname, int age) {
        this.id = base_id++;
        this.name = name;
        this.surname = surname;
        this.age = age;
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
