package services;

import modele.Plainte;
import modele.Suggestion;
import outils.Formatter;
import outils.InputHelper;
import outils.Colors;

import java.time.LocalDate;
import java.util.*;

public class SAVService {
    private List<Plainte>    plaintes;
    private List<Suggestion> suggestions;
    private static final String FICHIER_P = "plaintes.json";
    private static final String FICHIER_S = "suggestions.json";

    public SAVService() {
        plaintes    = new ArrayList<>();
        suggestions = new ArrayList<>();
        chargerDonnees();
    }

    private void chargerDonnees() {
        for (Map<String, String> obj : JsonService.lire(FICHIER_P)) {
            try {
                plaintes.add(new Plainte(
                    Integer.parseInt(obj.get("id")),
                    Integer.parseInt(obj.get("clientId")),
                    obj.get("date"),
                    obj.get("contenu")
                ));
            } catch (Exception e) { /* ignore */ }
        }
        for (Map<String, String> obj : JsonService.lire(FICHIER_S)) {
            try {
                suggestions.add(new Suggestion(
                    Integer.parseInt(obj.get("id")),
                    Integer.parseInt(obj.get("clientId")),
                    obj.get("date"),
                    obj.get("contenu")
                ));
            } catch (Exception e) { /* ignore */ }
        }
    }

    private void sauvegarderPlaintes() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Plainte p : plaintes) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",       String.valueOf(p.getId()));
            obj.put("clientId", String.valueOf(p.getClientId()));
            obj.put("date",     p.getDate());
            obj.put("contenu",  p.getContenu());
            data.add(obj);
        }
        JsonService.ecrire(FICHIER_P, data);
    }

    private void sauvegarderSuggestions() {
        List<Map<String, String>> data = new ArrayList<>();
        for (Suggestion s : suggestions) {
            Map<String, String> obj = new LinkedHashMap<>();
            obj.put("id",       String.valueOf(s.getId()));
            obj.put("clientId", String.valueOf(s.getClientId()));
            obj.put("date",     s.getDate());
            obj.put("contenu",  s.getContenu());
            data.add(obj);
        }
        JsonService.ecrire(FICHIER_S, data);
    }

    public void ajouterPlainte(int clientId) {
        String contenu = InputHelper.lireChaine("Decrivez votre plainte");
        if (contenu.isEmpty()) { Formatter.afficherErreur("Plainte vide, annulation."); return; }
        int nextId = plaintes.isEmpty() ? 1 : plaintes.get(plaintes.size() - 1).getId() + 1;
        plaintes.add(new Plainte(nextId, clientId, LocalDate.now().toString(), contenu));
        sauvegarderPlaintes();
        Formatter.afficherSucces("Plainte #" + nextId + " enregistree. Nous la traitons dans les 48h.");
    }

    public void ajouterSuggestion(int clientId) {
        String contenu = InputHelper.lireChaine("Votre suggestion pour nous ameliorer");
        if (contenu.isEmpty()) { Formatter.afficherErreur("Suggestion vide, annulation."); return; }
        int nextId = suggestions.isEmpty() ? 1 : suggestions.get(suggestions.size() - 1).getId() + 1;
        suggestions.add(new Suggestion(nextId, clientId, LocalDate.now().toString(), contenu));
        sauvegarderSuggestions();
        Formatter.afficherSucces("Merci pour votre suggestion ! Elle a bien ete enregistree.");
    }

    public void afficherSAV() {
        // Plaintes
        Formatter.afficherTitre("SAV - Plaintes Clients", Colors.RED_BOLD, "");
        if (plaintes.isEmpty()) {
            Formatter.afficherInfo("Aucune plainte enregistree.");
        } else {
            for (Plainte p : plaintes) {
                System.out.printf("  " + Colors.RED_BOLD + "[#%-3d]" + Colors.RESET +
                    " Client %-5d  %s  ->  %s%n",
                    p.getId(), p.getClientId(), p.getDate(), p.getContenu());
            }
        }

        // Suggestions
        Formatter.afficherTitre("SAV - Suggestions Clients", Colors.BLUE_BOLD, "");
        if (suggestions.isEmpty()) {
            Formatter.afficherInfo("Aucune suggestion enregistree.");
        } else {
            for (Suggestion s : suggestions) {
                System.out.printf("  " + Colors.BLUE_BOLD + "[#%-3d]" + Colors.RESET +
                    " Client %-5d  %s  ->  %s%n",
                    s.getId(), s.getClientId(), s.getDate(), s.getContenu());
            }
        }
        System.out.println();
    }

    public int getNombrePlaintes()    { return plaintes.size(); }
    public int getNombreSuggestions() { return suggestions.size(); }
}
