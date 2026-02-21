import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Park park = new Park(10);

        park.addRide("Jet", 2, 5);
        park.addRide("Wheel", 3, 10);

        Scanner input = new Scanner(System.in);
        boolean interactive = (System.console() != null);

        if (interactive) {
            System.out.println("\n=== Theme Park Simulator ===");
            System.out.println("DSA Final Project\n");
            System.out.println("Type 'help' for commands, 'exit' to quit");
        }

        while (input.hasNextLine()) {
            if (interactive) {
                System.out.print("\n> ");
                System.out.flush();
            }

            String line = input.nextLine();
            if (line == null) break;

            line = line.trim();
            if (line.isEmpty()) continue;

            if (line.startsWith(">")) {
                line = line.substring(1).trim();
                if (line.isEmpty()) continue;
            }

            if (interactive) {
                if (line.equalsIgnoreCase("exit")) break;
                if (line.equalsIgnoreCase("help")) { printHelp(); continue; }
            }

            // اجرای دستور
            park.processCommand(line);
        }
        if (interactive) {
            System.out.println("\nGoodbye!");
        }
    }

    private static void printHelp() {
        System.out.println("\nCommands:");
        System.out.println("  ADD_VISITOR <id> <name>");
        System.out.println("  MAKE_VIP <id>");
        System.out.println("  JOIN_QUEUE <id> <ride>");
        System.out.println("  LEAVE_QUEUE <id> <ride>");
        System.out.println("  TICK <minutes>");
        System.out.println("  UNDO");
        System.out.println("  STATUS");
        System.out.println("  REPORT");
        System.out.println("  VISITOR_INFO <id>");
        System.out.println("  ADD_RIDE <name> <capacity> <duration>");
        System.out.println("  help - show help");
        System.out.println("  DELETE_VISITOR <id>");
        System.out.println("  exit - quit");
        System.out.println("\nAvailable rides: Jet, Wheel");
    }




}