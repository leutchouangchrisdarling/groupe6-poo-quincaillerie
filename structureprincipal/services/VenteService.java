package services;

import modele.Vente;
import modele.Produit;
import outils.Formatter;
import outils.InputHelper;
import outils.Colors;

import java.time.LocalDate;
import java.util.*;

public class VenteService {
    private List<Vente> ventes;
    private static final String FICHIER = "ventes.json";
    private StockService stockService;

    public VenteService(StockService stockService) {
        this.stockService = stockService;
        ventes = new ArrayList<>();
        chargerVentes();
    }

    private void chargerVentes() {
        List<Map<String, String>> data = JsonService.lire(FICHIER);
        for (Map<String, String> obj : data) {
            try {
                ventes.add(new Vente(
                    Integer.parseInt(obj.get("id")),
                    Integer.parseInt(obj.get("vendeurId")),
                    Integer.parseInt(obj.get("produitId")),
                    Integer.parseInt(obj.get("quantite")),
                    Double.parseDouble(obj.get("total")),
                    obj.get("date")
                ));
            } catch (Exception e) { /* ligne corrompue */ }
        }
    }

    public void sauvegarderVentes() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Vente v : ventes) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",        String.valueOf(v.getId()));
            obj.put("vendeurId", String.valueOf(v.getVendeurId()));
            obj.put("produitId", String.valueOf(v.getProduitId()));
            obj.put("quantite",  String.valueOf(v.getQuantite()));
            obj.put("total",     String.valueOf(v.getMontantTotal()));
            obj.put("date",      v.getDate());
            data.add(obj);
        }
        JsonService.ecrire(FICHIER, data);
    }

    public void enregistrerVente(int vendeurId) {
        Formatter.afficherTitre("Enregistrement d'une Vente (Caisse)", Colors.GREEN_BOLD, "💰");
        stockService.afficherCatalogue();

        Map<Integer, Integer> panier = new LinkedHashMap<>();
        double totalGlobal = 0.0;
        List<String[]> factItems = new ArrayList<>();
        String auj = LocalDate.now().toString();

        while (true) {
            int produitId = InputHelper.lireEntier("ID du produit a vendre (0 = terminer l'encaissement)");
            if (produitId == 0) break;

            Produit p = stockService.getProduitById(produitId);
            if (p == null) { Formatter.afficherErreur("Produit introuvable."); continue; }

            int qte = InputHelper.lireEntier("Quantite souhaitee (dispo: " + p.getQuantiteStock() + ")");
            if (qte <= 0) { Formatter.afficherErreur("Quantite invalide."); continue; }
            if (qte > p.getQuantiteStock()) {
                Formatter.afficherErreur("Stock insuffisant. Disponible : " + p.getQuantiteStock());
                continue;
            }

            panier.merge(produitId, qte, Integer::sum);
            double prixLigne = p.getPrix() * qte;
            totalGlobal += prixLigne;
            
            System.out.println(Colors.GREEN_BOLD + "  [+] Ajoute au panier : " + qte + "x " + p.getNom()
                + " -> " + Formatter.formatMontant(prixLigne) + Colors.RESET);
        }

        if (panier.isEmpty()) {
            Formatter.afficherInfo("Encaissement annule - panier vide.");
            return;
        }

        int firstId = ventes.isEmpty() ? 1 : ventes.get(ventes.size() - 1).getId() + 1;
        for (Map.Entry<Integer, Integer> e : panier.entrySet()) {
            Produit p = stockService.getProduitById(e.getKey());
            if (stockService.reduireStock(p.getId(), e.getValue())) {
                double total = e.getValue() * p.getPrix();
                int nextId   = ventes.isEmpty() ? 1 : ventes.get(ventes.size() - 1).getId() + 1;
                Vente vente  = new Vente(nextId, vendeurId, p.getId(), e.getValue(), total, auj);
                ventes.add(vente);
                factItems.add(new String[]{p.getNom(), String.valueOf(e.getValue()), Formatter.formatMontant(total)});
            }
        }
        sauvegarderVentes();
        Formatter.afficherSucces("Ventes validees avec succes !");
        
        String ref = "VTE-" + auj.replace("-","") + "-" + firstId;
        Formatter.afficherFacture("TICKET DE CAISSE", ref, "Vendeur ID: " + vendeurId, factItems, totalGlobal);
    }

    public void afficherHistoriqueVentes() {
        Formatter.afficherTitre("Historique des Ventes", Colors.BLUE_BOLD, "");
        if (ventes.isEmpty()) { Formatter.afficherInfo("Aucune vente enregistree."); return; }
        System.out.printf("%n" + Colors.CYAN_BOLD + "  %-5s  %-12s  %-10s  %-10s  %-8s  %-14s%n" + Colors.RESET,
            "ID", "DATE", "VENDEUR", "PRODUIT", "QUANTITE", "TOTAL");
        Formatter.separateur(Colors.CYAN);
        for (Vente v : ventes) {
            System.out.printf("  %-5d  %-12s  %-10d  %-10d  %-8d  %-14s%n",
                v.getId(), v.getDate(), v.getVendeurId(),
                v.getProduitId(), v.getQuantite(), Formatter.formatMontant(v.getMontantTotal()));
        }
        System.out.println();
    }

    public List<Vente> getToutesLesVentes() { return ventes; }
}
