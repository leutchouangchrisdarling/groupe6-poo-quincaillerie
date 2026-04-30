package services;

import modele.Commande;
import modele.Produit;
import outils.Formatter;
import outils.InputHelper;
import outils.Colors;

import java.util.*;

public class CommandeService {
    private List<Commande> commandes;
    private static final String FICHIER = "commandes.json";
    private StockService stockService;

    public CommandeService(StockService stockService) {
        this.stockService = stockService;
        commandes = new ArrayList<>();
        chargerCommandes();
    }

    private void chargerCommandes() {
        List<Map<String, String>> data = JsonService.lire(FICHIER);
        for (Map<String, String> obj : data) {
            try {
                int id       = Integer.parseInt(obj.get("id"));
                int clientId = Integer.parseInt(obj.get("clientId"));
                double total = Double.parseDouble(obj.get("total"));
                String statut = obj.getOrDefault("statut", "EN_ATTENTE");

                Map<Integer, Integer> produits = new LinkedHashMap<>();
                String items = obj.getOrDefault("produits", "");
                if (!items.isEmpty()) {
                    for (String item : items.split(";")) {
                        String[] parts = item.split(":");
                        if (parts.length == 2) {
                            produits.put(Integer.parseInt(parts[0].trim()),
                                         Integer.parseInt(parts[1].trim()));
                        }
                    }
                }
                commandes.add(new Commande(id, clientId, produits, total, statut));
            } catch (Exception e) { /* ligne corrompue */ }
        }
    }

    public void sauvegarderCommandes() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Commande c : commandes) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",       String.valueOf(c.getId()));
            obj.put("clientId", String.valueOf(c.getClientId()));
            obj.put("total",    String.valueOf(c.getMontantTotal()));
            obj.put("statut",   c.getStatut());
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<Integer, Integer> e : c.getProduitsQuantites().entrySet()) {
                sb.append(e.getKey()).append(":").append(e.getValue()).append(";");
            }
            obj.put("produits", sb.toString());
            data.add(obj);
        }
        JsonService.ecrire(FICHIER, data);
    }

    public void passerCommande(int clientId) {
        Formatter.afficherTitre("Passer une Commande", Colors.CYAN_BOLD, "");
        Map<Integer, Integer> panier = new LinkedHashMap<>();
        double total = 0.0;

        while (true) {
            stockService.afficherCatalogue();
            int produitId = InputHelper.lireEntier("ID du produit (0 = terminer la commande)");
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
            total += p.getPrix() * qte;
            System.out.println(Colors.GREEN_BOLD + "  [+] Ajoute : " + qte + "x " + p.getNom()
                + " -> " + Formatter.formatMontant(p.getPrix() * qte) + Colors.RESET);
        }

        if (panier.isEmpty()) {
            Formatter.afficherInfo("Commande annulee - panier vide.");
            return;
        }

        // Recap panier
        System.out.println("\n" + Colors.YELLOW_BOLD + "  === RECAPITULATIF COMMANDE ===" + Colors.RESET);
        for (Map.Entry<Integer, Integer> e : panier.entrySet()) {
            Produit p = stockService.getProduitById(e.getKey());
            System.out.printf("  %-25s x%-4d  %s%n", p.getNom(), e.getValue(),
                Formatter.formatMontant(p.getPrix() * e.getValue()));
        }
        System.out.println(Colors.GREEN_BOLD + "  TOTAL : " + Formatter.formatMontant(total) + Colors.RESET);

        String confirm = InputHelper.lireChaine("Confirmer la commande ? (oui/non)");
        if (!confirm.equalsIgnoreCase("oui")) {
            Formatter.afficherInfo("Commande annulee.");
            return;
        }

        // Reduire le stock pour chaque produit
        for (Map.Entry<Integer, Integer> e : panier.entrySet()) {
            stockService.reduireStock(e.getKey(), e.getValue());
        }

        int nextId  = commandes.isEmpty() ? 1 : commandes.get(commandes.size() - 1).getId() + 1;
        Commande cmd = new Commande(nextId, clientId, panier, total, "EN_ATTENTE");
        commandes.add(cmd);
        sauvegarderCommandes();

        Formatter.afficherSucces("Commande #" + nextId + " enregistree ! Total : " + Formatter.formatMontant(total));
        
        List<String[]> factItems = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : panier.entrySet()) {
            Produit p = stockService.getProduitById(e.getKey());
            factItems.add(new String[]{p.getNom(), String.valueOf(e.getValue()), Formatter.formatMontant(e.getValue() * p.getPrix())});
        }
        Formatter.afficherFacture("COMMANDE CLIENT", "CMD-" + nextId, "Client ID: " + clientId, factItems, total);
    }

    public void afficherCommandesClient(int clientId) {
        Formatter.afficherTitre("Mes Commandes", Colors.CYAN_BOLD, "");
        boolean trouve = false;
        for (Commande c : commandes) {
            if (c.getClientId() == clientId) {
                String couleurStatut = c.getStatut().equals("VALIDEE") ? Colors.GREEN : Colors.YELLOW;
                System.out.printf("  Commande #%-4d | Total : %-14s | Statut : %s%s%s%n",
                    c.getId(), Formatter.formatMontant(c.getMontantTotal()),
                    couleurStatut, c.getStatut(), Colors.RESET);
                trouve = true;
            }
        }
        if (!trouve) Formatter.afficherInfo("Aucune commande enregistree pour votre compte.");
    }

    public void afficherToutesCommandes() {
        Formatter.afficherTitre("Toutes les Commandes", Colors.PURPLE_BOLD, "");
        if (commandes.isEmpty()) { Formatter.afficherInfo("Aucune commande."); return; }
        System.out.printf("%n" + Colors.CYAN_BOLD + "  %-5s  %-10s  %-16s  %-14s%n" + Colors.RESET,
            "ID", "CLIENT ID", "STATUT", "TOTAL");
        Formatter.separateur(Colors.CYAN);
        for (Commande c : commandes) {
            String couleur = c.getStatut().equals("VALIDEE") ? Colors.GREEN : Colors.YELLOW;
            System.out.printf("  %-5d  %-10d  %s%-16s%s  %-14s%n",
                c.getId(), c.getClientId(),
                couleur, c.getStatut(), Colors.RESET,
                Formatter.formatMontant(c.getMontantTotal()));
        }
        System.out.println();
    }

    public void validerCommande() {
        afficherToutesCommandes();
        int id = InputHelper.lireEntier("ID de la commande a valider");
        for (Commande c : commandes) {
            if (c.getId() == id) {
                c.setStatut("VALIDEE");
                sauvegarderCommandes();
                Formatter.afficherSucces("Commande #" + id + " validee !");
                return;
            }
        }
        Formatter.afficherErreur("Commande introuvable.");
    }

    public void officialiserAchat(int clientId) {
        Formatter.afficherTitre("Officialiser un Achat", Colors.CYAN_BOLD, "");
        afficherCommandesClient(clientId);
        int id = InputHelper.lireEntier("ID de la commande a officialiser (receptionnee/terminee)");
        for (Commande c : commandes) {
            if (c.getId() == id && c.getClientId() == clientId) {
                if (c.getStatut().equals("OFFICIALISEE")) {
                    Formatter.afficherInfo("Cette commande est deja officialisee.");
                    return;
                }
                c.setStatut("OFFICIALISEE");
                sauvegarderCommandes();
                Formatter.afficherSucces("L'achat de la commande #" + id + " a ete officialise avec succes !");
                return;
            }
        }
        Formatter.afficherErreur("Commande introuvable ou vous n'avez pas les droits sur cette commande.");
    }

    public void annulerCommande(int clientId) {
        Formatter.afficherTitre("Annuler une Commande", Colors.RED_BOLD, "");
        afficherCommandesClient(clientId);
        int id = InputHelper.lireEntier("ID de la commande a annuler (0 pour retour)");
        if (id == 0) return;

        for (Commande c : commandes) {
            if (c.getId() == id && c.getClientId() == clientId) {
                if (c.getStatut().equals("ANNULEE")) {
                    Formatter.afficherInfo("Cette commande est deja annulee.");
                    return;
                }
                if (c.getStatut().equals("OFFICIALISEE")) {
                    Formatter.afficherErreur("Impossible d'annuler une commande deja officialisee.");
                    return;
                }
                
                // Remettre les produits en stock
                for (Map.Entry<Integer, Integer> e : c.getProduitsQuantites().entrySet()) {
                    stockService.augmenterStock(e.getKey(), e.getValue());
                }
                
                c.setStatut("ANNULEE");
                sauvegarderCommandes();
                Formatter.afficherSucces("Commande #" + id + " annulee. Les stocks ont ete re-credites.");
                return;
            }
        }
        Formatter.afficherErreur("Commande introuvable ou vous n'avez pas les droits.");
    }

    public void editerFacture(int clientId) {
        Formatter.afficherTitre("Editer une Facture", Colors.CYAN_BOLD, "🧾");
        afficherCommandesClient(clientId);
        int id = InputHelper.lireEntier("ID de la commande a facturer (0 pour retour)");
        if (id == 0) return;

        for (Commande c : commandes) {
            if (c.getId() == id && c.getClientId() == clientId) {
                if (c.getStatut().equals("ANNULEE")) {
                    Formatter.afficherErreur("Impossible de facturer une commande annulee.");
                    return;
                }
                
                List<String[]> factItems = new ArrayList<>();
                for (Map.Entry<Integer, Integer> e : c.getProduitsQuantites().entrySet()) {
                    Produit p = stockService.getProduitById(e.getKey());
                    if (p != null) {
                        double total = e.getValue() * p.getPrix();
                        factItems.add(new String[]{p.getNom(), String.valueOf(e.getValue()), Formatter.formatMontant(total)});
                    }
                }
                String ref = "CMD-" + c.getId() + "-CLI-" + clientId;
                Formatter.afficherFacture("FACTURE", ref, "Commande " + c.getStatut(), factItems, c.getMontantTotal());
                return;
            }
        }
        Formatter.afficherErreur("Commande introuvable ou vous n'avez pas les droits.");
    }
}
