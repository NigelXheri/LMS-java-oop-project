import java.util.List;

public class Main {

    public static void main(String[] args){

        System.out.println(
                "          ┓ ┏  ┓                                 \n" +
                "          ┃┃┃┏┓┃┏┏┓┏┳┓┏┓  ╋┏┓  ┓┏┏┓┓┏┏┓          \n" +
                "          ┗┻┛┗ ┗┗┗┛┛┗┗┗   ┗┗┛  ┗┫┗┛┗┻┛           \n" +
                "┓ •┓           ┳┳┓              ┛     ┏┓         \n" +
                "┃ ┓┣┓┏┓┏┓┏┓┓┏  ┃┃┃┏┓┏┓┏┓┏┓┏┓┏┳┓┏┓┏┓╋  ┗┓┓┏┏╋┏┓┏┳┓\n" +
                "┗┛┗┗┛┛ ┗┻┛ ┗┫  ┛ ┗┗┻┛┗┗┻┗┫┗ ┛┗┗┗ ┛┗┗  ┗┛┗┫┛┗┗ ┛┗┗\n" +
                "            ┛            ┛               ┛       ");


        // Loading books
        List<Book> libraryInventory = FileManager.loadBooks();

        // If list is empty add some dummy data

        // Book class demo
        Book cpb = new Book("0000000000000", "Codex Purpureus Beratinus", null, Book.BookTheme.RELIGION, 1);
        Book b1 = new Book("9780141439600", "Pride and Prejudice", "Jane Austen", Book.BookTheme.FICTION, 10);
        Book b2 = new Book("9780262033848", "Introduction to Algorithms", "Thomas H. Cormen", Book.BookTheme.SCIENCE, 2);
        if(libraryInventory.isEmpty()) {
            libraryInventory.add(new Book("9780141412648", "Dune", "Frank Herbert", Book.BookTheme.FICTION, 5));
            libraryInventory.add(new Book("9788462018372", "Sapiens", "Yuval Noah Harari", Book.BookTheme.NON_FICTION, 3));
            libraryInventory.add(cpb);
            libraryInventory.add(b1);
            libraryInventory.add(b2);
        }
        for(Book book : libraryInventory){
            System.out.println(book);
        }

        // Member Class demo
        Member alfred = new Member("Alfred", "Smith", 40);

        System.out.println(alfred.toString());

        alfred.setAge(15);

        System.out.println(alfred.toString());

        alfred.borrowBook(b1);
        alfred.borrowBook(b2);
        System.out.println(b1);
        alfred.displayBorrowedBooks();

        b1.removeCopies(1);
        b1.modifyAvailableCopiesBy(-50);

        alfred.returnBook(b2);
        alfred.displayBorrowedBooks();
        alfred.returnBook(b1);
        alfred.displayBorrowedBooks();

        // Saving books
        FileManager.saveBooks(libraryInventory);


    }

}
