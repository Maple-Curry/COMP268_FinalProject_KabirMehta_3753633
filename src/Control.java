import java.util.Scanner;

public class Control {
    private Scanner scanner = new Scanner(System.in);

    public String getCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim().toLowerCase();
    }

    public void printHelp() {
        System.out.println("Available commands: go [direction], look, take [item], drop [item], inventory, use [item], quit");
    }
}

