package services;

import modele.Produit;
import outils.Formatter;
import outils.Colors;
import outils.InputHelper;

import java.util.*;

public class StockService {
    private List<Produit> stock;
    private static final String FICHIER = "produits.json";

    public StockService() {
        stock = new ArrayList<>();
        chargerStock();
        if (stock.isEmpty()) seederStock();
    }

    private void chargerStock() {
        List<Map<String, String>> data = JsonService.lire(FICHIER);
        for (Map<String, String> obj : data) {
            try {
                stock.add(new Produit(
                    Integer.parseInt(obj.get("id")),
                    obj.get("nom"),
                    obj.get("categorie"),
                    Double.parseDouble(obj.get("prix")),
                    Integer.parseInt(obj.get("stock")),
                    Integer.parseInt(obj.get("seuilAlerte"))
                ));
            } catch (Exception e) { /* ligne corrompue */ }
        }
    }

    private void seederStock() {
        String[][] produits = {
            {"1","Ciment Portland 50kg","Construction","4500","500","50"},
            {"2","Fer a beton 10mm","Construction","3500","1000","100"},
            {"3","Tole ondulee 3m","Toiture","6500","300","30"},
            {"4","Peinture Blanche 15L","Finition","15000","150","15"},
            {"5","Clous 5cm (kg)","Quincaillerie","1200","200","20"},
            {"6","Ampoule LED 12W","Electricite","1500","500","50"},
            {"7","Fil electrique 1.5mm (m)","Electricite","300","1000","100"},
            {"8","Tuyau PVC 100mm","Plomberie","4000","250","25"},
            {"9","Robinet lavabo","Plomberie","8500","80","10"},
            {"10","Marteau de charpentier","Outillage","3500","75","10"},
            {"11","Sable fin (Tonne)","Materiaux","15000","100","20"},
            {"12","Gravier (Tonne)","Materiaux","20000","100","20"},
            {"13","Serrure porte simple","Quincaillerie","5000","90","10"},
            {"14","Colle PVC","Plomberie","1500","120","15"},
            {"15","Truelle","Outillage","2500","60","5"},
            {"16","Brouette metallique","Outillage","25000","40","5"},
            {"17","Pelle carree","Outillage","4500","80","10"},
            {"18","Brique pleine (unite)","Materiaux","250","5000","500"},
            {"19","Parpaing 15cm (unite)","Materiaux","350","3000","300"},
            {"20","Peinture Acrylique 5L","Finition","6000","200","20"},
            {"21","Pinceau 50mm","Outillage","800","150","15"},
            {"22","Rouleau a peindre","Outillage","2000","100","10"},
            {"23","Disjoncteur 16A","Electricite","3500","120","10"},
            {"24","Prise murale simple","Electricite","1200","300","30"},
            {"25","Interrupteur simple","Electricite","1000","300","30"},
            {"26","Cordon de soudure","Outillage","4500","50","5"},
            {"27","Machette","Outillage","2000","100","10"},
            {"28","Siphon lavabo","Plomberie","3000","60","5"},
            {"29","Coude PVC 90deg","Plomberie","500","400","40"},
            {"30","Charniere (paire)","Quincaillerie","800","200","20"},
            {"31","Cadenas laiton 40mm","Quincaillerie","2500","150","15"},
            {"32","Grillage (rouleau 10m)","Cloture","12000","50","5"},
            {"33","Fil de fer recuit (kg)","Construction","1500","300","30"}
        };
        for (String[] p : produits) {
            stock.add(new Produit(Integer.parseInt(p[0]), p[1], p[2],
                Double.parseDouble(p[3]), Integer.parseInt(p[4]), Integer.parseInt(p[5])));
        }
        sauvegarderStock();
    }

    public void sauvegarderStock() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Produit p : stock) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",          String.valueOf(p.getId()));
            obj.put("nom",         p.getNom());
            obj.put("categorie",   p.getCategorie());
            obj.put("prix",        String.valueOf(p.getPrix()));
            obj.put("stock",       String.valueOf(p.getQuantiteStock()));
            obj.put("seuilAlerte", String.valueOf(p.getSeuilAlerte()));
            data.add(obj);
        }
        JsonService.ecrire(FICHIER, data);
    }

    public Produit getProduitById(int id) {
        return stock.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public List<Produit> getTousLesProduits() { return stock; }

    public void afficherCatalogue() {
        Formatter.afficherTitre("Catalogue des Produits", Colors.CYAN_BOLD, "");
        System.out.printf("%n" + Colors.CYAN_BOLD + "  %-4s  %-24s  %-14s  %-14s  %-8s  %-8s%n" + Colors.RESET,
            "ID", "NOM", "CATEGORIE", "PRIX", "STOCK", "ALERTE");
        Formatter.separateur(Colors.CYAN);
        for (Produit p : stock) {
            String alerte = p.estEnAlerte() ? Colors.RED_BOLD + " [!]" + Colors.RESET : "";
            System.out.printf("  %-4d  %-24s  %-14s  %-14s  %-8d  %-8d%s%n",
                p.getId(), p.getNom(), p.getCategorie(),
                Formatter.formatMontant(p.getPrix()),
                p.getQuantiteStock(), p.getSeuilAlerte(), alerte);
        }
        System.out.println();
    }

    public void afficherAlertesStock() {
        Formatter.afficherTitre("Alertes Stock Faible", Colors.RED_BOLD, "");
        boolean alerte = false;
        for (Produit p : stock) {
            if (p.estEnAlerte()) {
                System.out.printf("  " + Colors.RED_BOLD + "[!] %-24s" + Colors.RESET + " Stock: %-5d  Seuil: %d%n",
                    p.getNom(), p.getQuantiteStock(), p.getSeuilAlerte());
                alerte = true;
            }
        }
        if (!alerte) Formatter.afficherSucces("Tous les stocks sont a un niveau acceptable.");
    }

    public boolean reduireStock(int idProduit, int quantite) {
        Produit p = getProduitById(idProduit);
        if (p != null && p.getQuantiteStock() >= quantite) {
            int avant = p.getQuantiteStock();
            p.setQuantiteStock(p.getQuantiteStock() - quantite);
            int apres = p.getQuantiteStock();
            sauvegarderStock();
            System.out.println(outils.Colors.BLUE_BOLD + "  📦 STOCK MODIFIE : " + p.getNom() + 
                " | Avant: " + avant + " -> Apres: " + apres + outils.Colors.RESET);
            return true;
        }
        return false;
    }

    public void augmenterStock(int idProduit, int quantite) {
        Produit p = getProduitById(idProduit);
        if (p != null) {
            int avant = p.getQuantiteStock();
            p.setQuantiteStock(p.getQuantiteStock() + quantite);
            int apres = p.getQuantiteStock();
            sauvegarderStock();
            System.out.println(outils.Colors.BLUE_BOLD + "  📦 STOCK MODIFIE : " + p.getNom() + 
                " | Avant: " + avant + " -> Apres: " + apres + outils.Colors.RESET);
        }
    }

    public void ajouterProduit() {
        String nom       = InputHelper.lireChaine("Nom du produit");
        String cat       = InputHelper.lireChaine("Categorie");
        double prix      = InputHelper.lireDouble("Prix unitaire (FCFA)");
        int    qte       = InputHelper.lireEntier("Quantite en stock");
        int    seuil     = InputHelper.lireEntier("Seuil d'alerte");
        int    maxId     = stock.stream().mapToInt(Produit::getId).max().orElse(0);
        stock.add(new Produit(maxId + 1, nom, cat, prix, qte, seuil));
        sauvegarderStock();
        Formatter.afficherSucces("Produit \"" + nom + "\" ajoute avec succes !");
    }

    public void modifierPrix() {
        afficherCatalogue();
        int id = InputHelper.lireEntier("ID du produit a modifier");
        Produit p = getProduitById(id);
        if (p == null) { Formatter.afficherErreur("Produit introuvable."); return; }
        double nvPrix = InputHelper.lireDouble("Nouveau prix pour \"" + p.getNom() + "\"");
        p.setPrix(nvPrix);
        sauvegarderStock();
        Formatter.afficherSucces("Prix mis a jour : " + Formatter.formatMontant(nvPrix));
    }

    public void reapprovisionner() {
        afficherCatalogue();
        int id  = InputHelper.lireEntier("ID du produit a reapprovisionner");
        Produit p = getProduitById(id);
        if (p == null) { Formatter.afficherErreur("Produit introuvable."); return; }
        int qte = InputHelper.lireEntier("Quantite a ajouter");
        p.setQuantiteStock(p.getQuantiteStock() + qte);
        sauvegarderStock();
        Formatter.afficherSucces("Stock mis a jour : " + p.getNom() + " -> " + p.getQuantiteStock() + " unites");
    }
}
