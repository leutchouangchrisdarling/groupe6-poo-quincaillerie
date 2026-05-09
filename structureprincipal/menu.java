import modele.Utilisateur;
import services.*;
import outils.Formatter;
import outils.InputHelper;
import outils.Colors;

public class menu {
    private StockService       stockService;
    private VenteService       venteService;
    private CommandeService    commandeService;
    private SAVService         savService;
    private StatistiqueService statService;
    private AuthService        authService;

    public menu(StockService ss, VenteService vs, CommandeService cs,
                SAVService sav, StatistiqueService sts, AuthService as) {
        this.stockService    = ss;
        this.venteService    = vs;
        this.commandeService = cs;
        this.savService      = sav;
        this.statService     = sts;
        this.authService     = as;
    }

    // ═══════════════════════════════════════════════════
    // PORTAIL CLIENT
    // ═══════════════════════════════════════════════════
    public void afficherMenuClient(Utilisateur client) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n" + Colors.CYAN_BOLD +
                "  ==========================================================\n" +
                "  ||   PORTAIL CLIENT  -  " + String.format("%-34s", client.getNom()) + "||\n" +
                "  ==========================================================" + Colors.RESET);
            Formatter.menuItem("1", "📦", "Consulter tous les produits",          Colors.CYAN);
            Formatter.menuItem("2", "🛒", "Passer une commande",                  Colors.CYAN);
            Formatter.menuItem("3", "📜", "Mes commandes passees",                Colors.CYAN);
            Formatter.menuItem("4", "✅", "Officialiser un achat",                Colors.CYAN);
            Formatter.menuItem("5", "❌", "Annuler une commande",                 Colors.RED);
            Formatter.menuItem("6", "🎧", "Service Apres-Vente",                  Colors.CYAN);
            Formatter.menuItem("7", "🧾", "Voir/Editer une facture",              Colors.CYAN);
            Formatter.menuItem("0", "🚪", "Deconnexion",                          Colors.RED_BOLD);
            System.out.println(Colors.CYAN_BOLD +
                "  ==========================================================" + Colors.RESET);

            switch (InputHelper.lireEntier("Votre choix")) {
                case 1: stockService.afficherCatalogue();              InputHelper.pause(); break;
                case 2: commandeService.passerCommande(client.getId()); InputHelper.pause(); break;
                case 3: commandeService.afficherCommandesClient(client.getId()); InputHelper.pause(); break;
                case 4: commandeService.officialiserAchat(client.getId()); InputHelper.pause(); break;
                case 5: commandeService.annulerCommande(client.getId()); InputHelper.pause(); break;
                case 6: menuSAVClient(client); break;
                case 7: commandeService.editerFacture(client.getId()); InputHelper.pause(); break;
                case 0: continuer = false;
                        Formatter.afficherInfo("Deconnexion. A bientot " + client.getNom() + " !"); break;
                default: Formatter.afficherErreur("Choix invalide.");
            }
        }
    }

    private void menuSAVClient(Utilisateur client) {
        System.out.println("\n" + Colors.YELLOW_BOLD +
            "  ==========================================================\n" +
            "  ||   SERVICE APRES-VENTE                                ||\n" +
            "  ==========================================================" + Colors.RESET);
        Formatter.menuItem("1", "😡", "Se plaindre",            Colors.YELLOW);
        Formatter.menuItem("2", "💡", "Faire des suggestions",  Colors.YELLOW);
        Formatter.menuItem("0", "🔙", "Retour",                 Colors.RED_BOLD);
        System.out.println(Colors.YELLOW_BOLD +
            "  ==========================================================" + Colors.RESET);

        switch (InputHelper.lireEntier("Votre choix")) {
            case 1: savService.ajouterPlainte(client.getId());    break;
            case 2: savService.ajouterSuggestion(client.getId()); break;
            case 0: break;
            default: Formatter.afficherErreur("Choix invalide.");
        }
        InputHelper.pause();
    }

    // ═══════════════════════════════════════════════════
    // PORTAIL VENDEUR
    // ═══════════════════════════════════════════════════
    public void afficherMenuVendeur(Utilisateur vendeur) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n" + Colors.GREEN_BOLD +
                "  ==========================================================\n" +
                "  ||   PORTAIL VENDEUR  -  " + String.format("%-33s", vendeur.getNom()) + "||\n" +
                "  ==========================================================" + Colors.RESET);
            Formatter.menuItem("1", "💰", "Tenue de la caisse (Vente)",         Colors.GREEN);
            Formatter.menuItem("2", "📦", "Consulter les produits restants",    Colors.GREEN);
            Formatter.menuItem("3", "🧾", "Historique de mes ventes",           Colors.GREEN);
            Formatter.menuItem("4", "📋", "Differentes commandes enregistrees", Colors.GREEN);
            Formatter.menuItem("5", "⚠️", "Voir les alertes de stock",          Colors.YELLOW);
            Formatter.menuItem("6", "📅", "Statistiques journalieres",          Colors.GREEN);
            Formatter.menuItem("7", "📆", "Statistiques hebdomadaires",         Colors.GREEN);
            Formatter.menuItem("8", "📊", "Statistiques mensuelles",            Colors.GREEN);
            Formatter.menuItem("9", "🎯", "Objectifs du jour",                  Colors.GREEN);
            Formatter.menuItem("10","🚚", "Reapprovisionner le stock",          Colors.GREEN);
            Formatter.menuItem("0", "🚪", "Deconnexion",                        Colors.RED_BOLD);
            System.out.println(Colors.GREEN_BOLD +
                "  ==========================================================" + Colors.RESET);

            switch (InputHelper.lireEntier("Votre choix")) {
                case 1: venteService.enregistrerVente(vendeur.getId()); InputHelper.pause(); break;
                case 2: stockService.afficherCatalogue();               InputHelper.pause(); break;
                case 3: venteService.afficherHistoriqueVentes();        InputHelper.pause(); break;
                case 4: commandeService.validerCommande();              InputHelper.pause(); break;
                case 5: stockService.afficherAlertesStock();            InputHelper.pause(); break;
                case 6: statService.afficherStatistiquesJournalieres(); InputHelper.pause(); break;
                case 7: statService.afficherStatistiquesHebdomadaires();InputHelper.pause(); break;
                case 8: statService.afficherStatistiquesMensuelles();   InputHelper.pause(); break;
                case 9: statService.afficherObjectifsDuJour();          InputHelper.pause(); break;
                case 10:stockService.reapprovisionner();                InputHelper.pause(); break;
                case 0: continuer = false;
                        Formatter.afficherInfo("Deconnexion. Bonne journee " + vendeur.getNom() + " !"); break;
                default: Formatter.afficherErreur("Choix invalide.");
            }
        }
    }

    // ═══════════════════════════════════════════════════
    // PORTAIL PROPRIETAIRE
    // ═══════════════════════════════════════════════════
    public void afficherMenuProprietaire(Utilisateur prop) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n" + Colors.PURPLE_BOLD +
                "  ==========================================================\n" +
                "  ||   TABLEAU DE BORD PROPRIETAIRE - " + String.format("%-20s", prop.getNom()) + "||\n" +
                "  ==========================================================" + Colors.RESET);

            System.out.println(Colors.PURPLE_BOLD + "  ||" + Colors.YELLOW_BOLD + " [Global & Finances]" + Colors.RESET);
            Formatter.menuItem("1", "🌍", "Voir comment la quincaillerie fonctionne en gros", Colors.PURPLE);
            Formatter.menuItem("2", "📓", "Plan comptable",                    Colors.PURPLE);
            Formatter.menuItem("3", "🔮", "Bilan previsionnel",                Colors.PURPLE);
            Formatter.menuItem("4", "📈", "Bilan comptable & CA",              Colors.PURPLE);
            Formatter.menuItem("5", "🧾", "Historique global des ventes",      Colors.PURPLE);

            System.out.println(Colors.PURPLE_BOLD + "  ||" + Colors.YELLOW_BOLD + " [Stock]"    + Colors.RESET);
            Formatter.menuItem("6", "📦", "Etat des stocks & alertes",         Colors.PURPLE);
            Formatter.menuItem("7", "📊", "Statistiques stock (valeur, etc.)", Colors.PURPLE);
            Formatter.menuItem("8", "➕", "Ajouter un nouveau produit",        Colors.PURPLE);
            Formatter.menuItem("9", "🏷️", "Modifier le prix d'un produit",     Colors.PURPLE);
            Formatter.menuItem("10","🚚", "Reapprovisionner le stock",         Colors.PURPLE);

            System.out.println(Colors.PURPLE_BOLD + "  ||" + Colors.YELLOW_BOLD + " [SAV & RH]" + Colors.RESET);
            Formatter.menuItem("11","🎧", "Consulter plaintes & suggestions",  Colors.PURPLE);
            Formatter.menuItem("12","👥", "Liste des employes",                Colors.PURPLE);
            Formatter.menuItem("13","📋", "Toutes les commandes clients",      Colors.PURPLE);
            Formatter.menuItem("14","👤", "Ajouter un vendeur",                Colors.PURPLE);
            Formatter.menuItem("15","❌", "Supprimer un vendeur",              Colors.PURPLE);

            Formatter.menuItem("0", "🚪", "Deconnexion",                       Colors.RED_BOLD);
            System.out.println(Colors.PURPLE_BOLD +
                "  ==========================================================" + Colors.RESET);

            switch (InputHelper.lireEntier("Votre choix")) {
                case 1:  statService.afficherTableauDeBordGlobal();  InputHelper.pause(); break;
                case 2:  statService.afficherPlanComptable();        InputHelper.pause(); break;
                case 3:  statService.afficherBilanPrevisionnel();    InputHelper.pause(); break;
                case 4:  statService.afficherBilanComptable();       InputHelper.pause(); break;
                case 5:  venteService.afficherHistoriqueVentes();     InputHelper.pause(); break;
                case 6:  stockService.afficherCatalogue();
                         stockService.afficherAlertesStock();         InputHelper.pause(); break;
                case 7:  statService.afficherStatistiquesStock();     InputHelper.pause(); break;
                case 8:  stockService.ajouterProduit();               InputHelper.pause(); break;
                case 9:  stockService.modifierPrix();                 InputHelper.pause(); break;
                case 10: stockService.reapprovisionner();             InputHelper.pause(); break;
                case 11: savService.afficherSAV();                    InputHelper.pause(); break;
                case 12: authService.afficherEmployes();              InputHelper.pause(); break;
                case 13: commandeService.afficherToutesCommandes();   InputHelper.pause(); break;
                case 14: 
                    int confirmAdd = InputHelper.lireEntier("Entrez votre ID Proprietaire pour confirmer");
                    if (confirmAdd == prop.getId()) authService.ajouterVendeur();
                    else Formatter.afficherErreur("ID incorrect. Operation annulee.");
                    InputHelper.pause(); break;
                case 15: 
                    int confirmDel = InputHelper.lireEntier("Entrez votre ID Proprietaire pour confirmer");
                    if (confirmDel == prop.getId()) authService.supprimerVendeur();
                    else Formatter.afficherErreur("ID incorrect. Operation annulee.");
                    InputHelper.pause(); break;
                case 0:  continuer = false;
                         Formatter.afficherInfo("Deconnexion proprietaire."); break;
                default: Formatter.afficherErreur("Choix invalide.");
            }
        }
    }
}