import services.*;
import modele.Utilisateur;
import outils.Formatter;
import outils.InputHelper;
import outils.Colors;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class main {

    public static void main(String[] args) throws Exception {
        // ── FORCER UTF-8 EN TOUT PREMIER ─────────────────────────────
        // Bypass le code page Windows (1252) et force la sortie en UTF-8
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, "UTF-8"));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, "UTF-8"));
        Formatter.setupConsole(); // double securite

        // Initialisation des services (injection de dependances)
        StockService       stockService    = new StockService();
        VenteService       venteService    = new VenteService(stockService);
        CommandeService    commandeService = new CommandeService(stockService);
        SAVService         savService      = new SAVService();
        StatistiqueService statService     = new StatistiqueService(venteService, stockService);
        AuthService        authService     = new AuthService();

        authenthification auth    = new authenthification(authService);
        menu              leMenu  = new menu(stockService, venteService, commandeService,
                                             savService, statService, authService);

        boolean quitter = false;

        while (!quitter) {
            // Effacer l'ecran
            System.out.print("\033[H\033[2J");
            System.out.flush();

            // Banner principal
            Formatter.afficherBannerPrincipal();

            // Menu de selection du portail
            System.out.println("\n" + Colors.YELLOW_BOLD +
                "  ==========================================================\n" +
                "  ||           ✨ SELECTIONNEZ VOTRE PORTAIL ✨           ||\n" +
                "  ==========================================================" + Colors.RESET);
            Formatter.menuItem("1", "👤", "Portail Client  (Achats, Commandes, SAV)",  Colors.CYAN_BOLD);
            Formatter.menuItem("2", "💼", "Portail Vendeur (Caisse, Stock)",            Colors.GREEN_BOLD);
            Formatter.menuItem("3", "👑", "Portail Proprietaire (Administration)",      Colors.PURPLE_BOLD);
            Formatter.menuItem("0", "🚪", "Quitter le systeme",                         Colors.RED_BOLD);
            System.out.println(Colors.YELLOW_BOLD +
                "  ==========================================================" + Colors.RESET);

            int portail = InputHelper.lireEntier("Votre choix");
            Utilisateur session = null;

            switch (portail) {
                case 1:
                    session = auth.loginClient();
                    if (session != null) leMenu.afficherMenuClient(session);
                    break;
                case 2:
                    session = auth.loginEmploye("VENDEUR");
                    if (session != null) leMenu.afficherMenuVendeur(session);
                    break;
                case 3:
                    session = auth.loginEmploye("PROPRIETAIRE");
                    if (session != null) leMenu.afficherMenuProprietaire(session);
                    break;
                case 0:
                    quitter = true;
                    System.out.println("\n" + Colors.RED_BOLD +
                        "  ==========================================================\n" +
                        "  ||   👋 Merci d'utiliser Quincaillerie de Sekande !     ||\n" +
                        "  ||   🌟 A tres bientot !                                ||\n" +
                        "  =========================================================="
                        + Colors.RESET + "\n");
                    break;
                default:
                    Formatter.afficherErreur("Choix invalide. Entrez 0, 1, 2 ou 3.");
            }
        }
    }
}