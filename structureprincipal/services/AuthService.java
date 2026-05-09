package services;

import modele.*;
import outils.Formatter;
import outils.InputHelper;

import java.util.*;

public class AuthService {
    private List<Utilisateur> utilisateurs;
    private static final String FICHIER = "utilisateurs.json";

    public AuthService() {
        utilisateurs = new ArrayList<>();
        chargerUtilisateurs();
        // Aucune creation de compte par defaut selon la demande.
        // C'est a l'utilisateur d'entrer les informations (si liste vide, il devra en creer).
    }

    private void chargerUtilisateurs() {
        List<Map<String, String>> data = JsonService.lire(FICHIER);
        for (Map<String, String> obj : data) {
            String role = obj.getOrDefault("role", "");
            int id     = Integer.parseInt(obj.getOrDefault("id", "0"));
            String nom = obj.getOrDefault("nom", "");
            String mdp = obj.getOrDefault("mdp", "");

            switch (role) {
                case "PROPRIETAIRE": utilisateurs.add(new Proprietaire(id, nom, mdp)); break;
                case "VENDEUR":      utilisateurs.add(new Vendeur(id, nom, mdp));      break;
                case "CLIENT":       utilisateurs.add(new Client(id, nom));             break;
            }
        }
    }

    private void sauvegarder() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Utilisateur u : utilisateurs) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",   String.valueOf(u.getId()));
            obj.put("nom",  u.getNom());
            obj.put("role", u.getRole());
            if (u instanceof Proprietaire) obj.put("mdp", ((Proprietaire) u).getMotDePasse());
            else if (u instanceof Vendeur) obj.put("mdp", ((Vendeur) u).getMotDePasse());
            else obj.put("mdp", "");
            data.add(obj);
        }
        JsonService.ecrire(FICHIER, data);
    }

    public Utilisateur authentifierParNom(String role, String nom) {
        for (Utilisateur u : utilisateurs) {
            if (u.getRole().equals(role) && u.getNom().equalsIgnoreCase(nom.trim())) {
                return u;
            }
        }
        return null;
    }

    public Utilisateur authentifierEmploye(String role, String nom, String mdp) {
        for (Utilisateur u : utilisateurs) {
            if (u.getRole().equals(role) && u.getNom().equalsIgnoreCase(nom.trim())) {
                if (u instanceof Proprietaire && ((Proprietaire) u).getMotDePasse().equals(mdp)) return u;
                if (u instanceof Vendeur     && ((Vendeur)      u).getMotDePasse().equals(mdp)) return u;
            }
        }
        return null;
    }

    public Client creerNouveauClient(String nom) {
        int maxId = utilisateurs.stream().mapToInt(Utilisateur::getId).max().orElse(0);
        Client c = new Client(maxId + 1, nom);
        utilisateurs.add(c);
        sauvegarder();
        Formatter.afficherSucces("Compte cree ! (Votre profil est memorise)");
        return c;
    }

    public Proprietaire creerNouveauProprietaire(String nom, String mdp) {
        int maxId = utilisateurs.stream().mapToInt(Utilisateur::getId).max().orElse(0);
        Proprietaire p = new Proprietaire(maxId + 1, nom, mdp);
        utilisateurs.add(p);
        sauvegarder();
        Formatter.afficherSucces("Compte Proprietaire cree ! Votre ID est : " + p.getId());
        return p;
    }

    public void afficherEmployes() {
        Formatter.afficherTitre("Liste des Employes", outils.Colors.PURPLE_BOLD, "");
        System.out.println();
        System.out.printf(outils.Colors.CYAN_BOLD + "  %-5s  %-25s  %-15s%n" + outils.Colors.RESET, "ID", "NOM", "ROLE");
        Formatter.separateur(outils.Colors.CYAN);
        for (Utilisateur u : utilisateurs) {
            if (!u.getRole().equals("CLIENT")) {
                System.out.printf("  %-5d  %-25s  %-15s%n", u.getId(), u.getNom(), u.getRole());
            }
        }
        System.out.println();
    }

    public void ajouterVendeur() {
        String nom = InputHelper.lireChaine("Nom du nouveau vendeur");
        String mdp = InputHelper.lireChaine("Mot de passe du vendeur");
        int maxId  = utilisateurs.stream().mapToInt(Utilisateur::getId).max().orElse(0);
        Vendeur v  = new Vendeur(maxId + 1, nom, mdp);
        utilisateurs.add(v);
        sauvegarder();
        Formatter.afficherSucces("Vendeur \"" + nom + "\" ajoute avec succes !");
    }

    public void supprimerVendeur() {
        afficherEmployes();
        String nom = InputHelper.lireChaine("Nom du vendeur a supprimer");
        Utilisateur target = null;
        for(Utilisateur u : utilisateurs) {
            if(u.getRole().equals("VENDEUR") && u.getNom().equalsIgnoreCase(nom)) { target = u; break; }
        }
        if(target != null) {
            utilisateurs.remove(target);
            sauvegarder();
            Formatter.afficherSucces("Vendeur supprime avec succes !");
        } else {
            Formatter.afficherErreur("Vendeur introuvable.");
        }
    }

    public int compterProprietaires() {
        return (int) utilisateurs.stream().filter(u -> u.getRole().equals("PROPRIETAIRE")).count();
    }

    public List<Utilisateur> getUtilisateurs() { return utilisateurs; }
}
