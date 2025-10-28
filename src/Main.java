public class Main {

    public static void main(String[] args){

        System.out.println("Hello World");

        Member alfred = new Member("Alfred", "Smith", 40);

        System.out.println(alfred.toString());

        alfred.setAge(15);

        System.out.println(alfred.toString());
    }

}
