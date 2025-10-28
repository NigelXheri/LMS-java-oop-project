public class Student {

    // 1. Class Attributes
    private int id;
    private String name;
    private String surname;
    private int age;

    private static int base_id = 100;

    // 2. Constructor
    public Student(String name, String surname,int age){
        this.id  = ++base_id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }


    // 3.1 GET Methods
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

    // 4. toString()

    @Override
    public String toString() {
        return "Student: " + this.name + " " + this.surname + ", Age: " + this.age + ", ID: " + this.id;
    }
}
