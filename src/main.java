public class main {

    public static void main(String[] args){

        System.out.println("Hello World");

        Student alfred = new Student("Alfred", "Smith", 40);

        System.out.println(alfred.toString());

        alfred.setAge(15);

        System.out.println(alfred.toString());
    }

}
