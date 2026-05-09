package outils;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Formatter {

    // ═══════════════════════════════════════════════════
    // BANNER PRINCIPAL
    // ═══════════════════════════════════════════════════
    public static void afficherBannerPrincipal() {
        System.out.println();
        System.out.println(Colors.RED_BOLD +    "   ___   _   _ ___ _   _  ____    _    ___ _     _     _____ ____  ___ _____ ");
        System.out.println(Colors.YELLOW_BOLD + "  / _ \\ | | | |_ _| \\ | |/ ___|  / \\  |_ _| |   | |   | ____|  _ \\|_ _| ____|");
        System.out.println(Colors.GREEN_BOLD +  " | | | || | | || ||  \\| | |     / _ \\  | || |   | |   |  _| | |_) || ||  _|  ");
        System.out.println(Colors.CYAN_BOLD +   " | |_| || |_| || || |\\  | |___ / ___ \\ | || |___| |___| |___|  _ < | || |___ ");
        System.out.println(Colors.PURPLE_BOLD + "  \\__\\_\\ \\___/|___|_| \\_|\\____/_/   \\_\\___|_____|_____|_____|_| \\_\\___|_____|");
        System.out.println();
        System.out.println("  " + Colors.WHITE_BOLD + Colors.BLUE_BG + 
            "                     DE SEKANDE  -  SYSTEME DE GESTION                     " + Colors.RESET);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════
    // BOX DE SECTION
    // ═══════════════════════════════════════════════════
    public static void afficherTitre(String titre, String couleur, String icone) {
        int largeur = 58;
        String ligne = icone + "  " + titre.toUpperCase();
        int padding = (largeur - ligne.length()) / 2;
        String pad = " ".repeat(Math.max(0, padding));

        System.out.println("\n" + couleur +
            "  ╔══════════════════════════════════════════════════════════╗\n" +
            "  ║" + pad + ligne + " ".repeat(Math.max(0, largeur - pad.length() - ligne.length())) + "║\n" +
            "  ╚══════════════════════════════════════════════════════════╝"
            + Colors.RESET);
    }

    public static void afficherTitre(String titre) {
        afficherTitre(titre, Colors.BLUE_BOLD, "");
    }

    // ═══════════════════════════════════════════════════
    // SEPARATEUR DE MENU
    // ═══════════════════════════════════════════════════
    public static void separateur(String couleur) {
        System.out.println(couleur + "  " + "─".repeat(58) + Colors.RESET);
    }

    public static void separateur() {
        separateur(Colors.WHITE);
    }

    // ═══════════════════════════════════════════════════
    // ITEM DE MENU
    // ═══════════════════════════════════════════════════
    public static void menuItem(String num, String icone, String texte, String couleur) {
        System.out.println(couleur + "  │ " + Colors.YELLOW_BOLD + num + Colors.RESET
                + couleur + "  " + icone + "  " + texte + Colors.RESET);
    }

    public static void menuFooter(String couleur) {
        System.out.println(couleur + "  └─────────────────────────────────────────────────────────┘" + Colors.RESET);
    }

    public static void menuHeader(String couleur) {
        System.out.println(couleur + "  ┌─────────────────────────────────────────────────────────┐" + Colors.RESET);
    }

    // ═══════════════════════════════════════════════════
    // MESSAGES
    // ═══════════════════════════════════════════════════
    public static void afficherSucces(String message) {
        System.out.println(Colors.GREEN_BOLD + "\n  [OK] " + message + Colors.RESET);
    }

    public static void afficherErreur(String message) {
        System.out.println(Colors.RED_BOLD + "\n  [!!] " + message + Colors.RESET);
    }

    public static void afficherInfo(String message) {
        System.out.println(Colors.YELLOW + "\n  [i]  " + message + Colors.RESET);
    }

    public static void afficherAlerte(String message) {
        System.out.println(Colors.RED_BOLD + "\n  [!]  " + message + Colors.RESET);
    }

    // ═══════════════════════════════════════════════════
    // TABLEAU
    // ═══════════════════════════════════════════════════
    public static void tableHeader(String... colonnes) {
        StringBuilder sb = new StringBuilder(Colors.CYAN_BOLD + "  ");
        for (String col : colonnes) {
            sb.append(String.format("%-20s", col));
        }
        System.out.println(sb + Colors.RESET);
        separateur(Colors.CYAN);
    }

    // ═══════════════════════════════════════════════════
    // FORMAT MONTANT
    // ═══════════════════════════════════════════════════
    public static String formatMontant(double montant) {
        return String.format("%,.0f FCFA", montant);
    }

    // ═══════════════════════════════════════════════════
    // FACTURE / TICKET DE CAISSE
    // ═══════════════════════════════════════════════════
    public static void afficherFacture(String type, String ref, String entete, List<String[]> items, double total) {
        System.out.println("\n" + Colors.WHITE_BOLD +
            "  ╔══════════════════════════════════════════════════════════╗\n" +
            "  ║" + centerString("FACTURE / TICKET DE CAISSE", 58) + "║\n" +
            "  ╠══════════════════════════════════════════════════════════╣" + Colors.RESET);
        
        System.out.println(Colors.WHITE_BOLD + "  ║ " + String.format("%-26s", "Type : " + type) + String.format("%30s", "Ref : " + ref) + " ║");
        System.out.println("  ║ " + String.format("%-56s", entete) + " ║");
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║ " + Colors.CYAN_BOLD + String.format("%-30s %-10s %-14s", "DESIGNATION", "QTE", "PRIX TOTAL") + Colors.WHITE_BOLD + " ║");
        System.out.println("  ║ " + "--------------------------------------------------------" + " ║");
        
        for (String[] item : items) {
            String nom = item[0].length() > 28 ? item[0].substring(0, 28) : item[0];
            System.out.println("  ║ " + Colors.YELLOW + String.format("%-30s %-10s %-14s", nom, "x" + item[1], item[2]) + Colors.WHITE_BOLD + " ║");
        }
        
        System.out.println("  ╠══════════════════════════════════════════════════════════╣");
        System.out.println("  ║ " + Colors.GREEN_BOLD + String.format("%-41s %-14s", "TOTAL A PAYER :", formatMontant(total)) + Colors.WHITE_BOLD + " ║");
        System.out.println("  ╚══════════════════════════════════════════════════════════╝" + Colors.RESET + "\n");
    }

    private static String centerString(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        int padSize = width - s.length();
        int padStart = s.length() + padSize / 2;
        s = String.format("%" + padStart + "s", s);
        s = String.format("%-" + width  + "s", s);
        return s;
    }

    // ═══════════════════════════════════════════════════
    // SETUP UTF-8 POUR LES EMOJIS
    // ═══════════════════════════════════════════════════
    /**
     * Force UTF-8 sur la sortie standard Windows.
     * Doit etre appele en TOUT PREMIER dans main().
     */
    public static void setupConsole() {
        try {
            PrintStream utf8Out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8");
            PrintStream utf8Err = new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8");
            System.setOut(utf8Out);
            System.setErr(utf8Err);
        } catch (Exception e) {
            // silence - console restera dans l'encodage par defaut
        }
    }
}
