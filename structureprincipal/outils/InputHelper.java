package outils;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in, "UTF-8");

    public static String lireChaine(String message) {
        System.out.print(Colors.CYAN + "\n  >> " + message + Colors.RESET + " : ");
        return scanner.nextLine().trim();
    }

    public static int lireEntier(String message) {
        while (true) {
            System.out.print(Colors.CYAN + "\n  >> " + message + Colors.RESET + " : ");
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "  [!!] Entrez un nombre entier valide." + Colors.RESET);
            }
        }
    }

    public static double lireDouble(String message) {
        while (true) {
            System.out.print(Colors.CYAN + "\n  >> " + message + Colors.RESET + " : ");
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "  [!!] Entrez un nombre valide (ex: 15.5)." + Colors.RESET);
            }
        }
    }

    public static void pause() {
        System.out.print(Colors.BLACK_BOLD + "\n  Appuyez sur Entree pour continuer..." + Colors.RESET);
        scanner.nextLine();
    }
}
