public class Book {

    public enum BookTheme {
        FICTION,
        NON_FICTION,
        SCIENCE,
        RELIGION,
        POLITICS
    }

    private String isbn;
    private String title;
    private String author;
    private BookTheme bookTheme;
    private int numberOfCopies;
    private int copiesAvailable;

    // Constructor of the class
    public Book(String isbn, String title, String author, BookTheme bookTheme, int numberOfCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.bookTheme = bookTheme;
        this.numberOfCopies = numberOfCopies;
        this.copiesAvailable = numberOfCopies;
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public BookTheme getCategory() {
        return bookTheme;
    }
    public String getIsbn() {
        return isbn;
    }
    public int getNumberOfCopies() {
        return numberOfCopies;
    }
    public int getCopiesAvailable() {
        return copiesAvailable;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(BookTheme bookTheme) {
        this.bookTheme = bookTheme;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setNumberOfCopies(int numberOfCopies) {
        this.numberOfCopies = numberOfCopies;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    //String toString
    public String toString() {
        return "Book { " +
                "ISBN='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + bookTheme + '\'' +
                ", numberOfCopies=" + numberOfCopies +
                ", copiesAvailable=" + copiesAvailable +
                '}';
    }


    // Methods

    public void modifyAvailableCopiesBy(int modifierNumber){
        if (-modifierNumber > copiesAvailable) {
            System.out.println("Cannot reduce available copies by " + -modifierNumber + ". " + title + " only has " + copyNumberString(copiesAvailable) + " available.");
            return;
        }
        else if (modifierNumber + copiesAvailable > numberOfCopies){
            System.out.println(title + " can only have up to " + copyNumberString(numberOfCopies) + " available" + ". You can only add up to " + (numberOfCopies - copiesAvailable) + " copies.");
        }
        copiesAvailable += modifierNumber;
        System.out.println(title + " has now " + copyNumberString(copiesAvailable) + " available");
    }

    public void removeOneAvailableCopy(){
        modifyAvailableCopiesBy(-1);
    }


    // 3. Check if book is available
    public boolean isAvailable() {
        return copiesAvailable > 0;
    }

    // 4. Add copies to inventory
    public void addCopies(int copies) {
        if (copies < 1) {
            System.out.println("Please enter a valid number of copies to remove.");
            return;
        }
        numberOfCopies += copies;
        copiesAvailable += copies;
        System.out.println(copyNumberString(copies) +" of book " + title + " has been added successfully.");
    }

    public void removeCopies(int copies){
        if(copies > copiesAvailable || copies < 1){
            System.out.println("Please enter a valid number of copies to remove.");
            return;
        }
        numberOfCopies -= copies;
        copiesAvailable -= copies;
        System.out.println(copyNumberString(copies) +" of book " + title + " has been removed successfully.");
    }

    private String copyNumberString(int copies){
        return copies == 1 ? copies + " copy" : copies + " copies";
    }
}
