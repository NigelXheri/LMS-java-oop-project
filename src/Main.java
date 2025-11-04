public class Main {

    public static void main(String[] args){



        // Book class demo
        Book b1 = new Book("9780141439600", "Pride and Prejudice", "Jane Austen", Book.BookTheme.FICTION, 10);
        Book b2 = new Book("9780262033848", "Introduction to Algorithms", "Thomas H. Cormen", Book.BookTheme.SCIENCE, 2);
        System.out.println(b1);


        // Member Class demo
        Member alfred = new Member("Alfred", "Smith", 40);

        System.out.println(alfred.toString());

        alfred.setAge(15);

        System.out.println(alfred.toString());

        alfred.borrowBook(b1);
        alfred.borrowBook(b2);
        System.out.println(b1);
        System.out.println(alfred);
        alfred.displayBorrowedBooks();

    }

}
