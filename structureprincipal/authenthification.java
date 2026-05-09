import services.AuthService;
import modele.Utilisateur;
import outils.InputHelper;
import outils.Formatter;
import outils.Colors;

public class authenthification {
    private AuthService authService;

    public authenthification(AuthService authService) {
        this.authService = authService;
    }

    public Utilisateur loginClient() {
        System.out.println("\n" + Colors.CYAN_BOLD +
            "  ==========================================================\n" +
            "  ||   👤 ESPACE CLIENT - CONNEXION / ENREGISTREMENT      ||\n" +
            "  ==========================================================" + Colors.RESET);
        
        String nom = InputHelper.lireChaine("Votre nom complet");

        Utilisateur u = authService.authentifierParNom("CLIENT", nom);
        if (u != null) {
            Formatter.afficherSucces("Bienvenue de retour " + u.getNom() + " !");
            return u;
        } else {
            return authService.creerNouveauClient(nom);
        }
    }

    public Utilisateur loginEmploye(String role) {
        String label = role.equals("PROPRIETAIRE") ? "Proprietaire" : "Vendeur";
        String icone = role.equals("PROPRIETAIRE") ? "👑" : "💼";
        System.out.println("\n" + Colors.PURPLE_BOLD +
            "  ==========================================================\n" +
            "  ||   " + icone + " CONNEXION " + String.format("%-38s", label.toUpperCase()) + "||\n" +
            "  ==========================================================" + Colors.RESET);

        if (role.equals("PROPRIETAIRE")) {
            int count = authService.compterProprietaires();
            if (count < 2) {
                System.out.println(Colors.YELLOW + "  Il y a " + count + "/2 comptes Proprietaire." + Colors.RESET);
                String choix = InputHelper.lireChaine("Voulez-vous (1) Vous connecter ou (2) Creer un compte ?");
                if (choix.equals("2")) {
                    String nom = InputHelper.lireChaine("Nom Proprietaire");
                    String mdp;
                    do {
                        mdp = InputHelper.lireChaine("Mot de passe (min 5 caracteres)");
                    } while (mdp.length() < 5);
                    return authService.creerNouveauProprietaire(nom, mdp);
                }
            } else {
                System.out.println(Colors.YELLOW + "  Les 2 comptes Proprietaire sont deja crees." + Colors.RESET);
            }
            
            System.out.println(Colors.CYAN + "  Proprietaires enregistres :" + Colors.RESET);
            for (Utilisateur p : authService.getUtilisateurs()) {
                if (p.getRole().equals("PROPRIETAIRE")) {
                    System.out.println("  - " + p.getNom() + " (ID: " + p.getId() + ")");
                }
            }
        }

        String nom = InputHelper.lireChaine("Nom " + label);
        String mdp = InputHelper.lireChaine("Mot de passe");

        Utilisateur u = authService.authentifierEmploye(role, nom, mdp);
        if (u != null) {
            Formatter.afficherSucces("Connexion reussie. Bienvenue " + u.getNom() + " !");
            return u;
        } else {
            Formatter.afficherErreur("Identifiants incorrects !");
            return null;
        }
    }
}
