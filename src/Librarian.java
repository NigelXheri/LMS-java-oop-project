public class Librarian extends User{

    public Librarian(String name, String surname, int age, String email, String password){
        super(name, surname, age, email, password, Role.LIBRARIAN);
    }
    public Librarian(String name, String surname, int age){
        super(name, surname, age, Role.LIBRARIAN);
    }


}
