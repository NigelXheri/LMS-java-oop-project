public class Staff extends user {

    private String position;

    public Staff(String name, String surname, String position) {
        super(name, surname);
        this.position = position;
    }

    public String getPosition() {
        return position;
    }


    public String getRole() {
        return "Staff";
    }

    public void addBook(Book book) {
        System.out.println("Staff added book: " + book.getTitle());
    }

    public void removeBook(Book book) {
        System.out.println("Staff removed book: " + book.getTitle());
    }
}


