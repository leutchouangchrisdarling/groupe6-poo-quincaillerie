package services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Service JSON minimal (sans librairie externe).
 * Gere la lecture/ecriture de tableaux JSON simples.
 * Format: [ {...}, {...} ]
 */
public class JsonService {

    private static final String BASE_PATH = "donnees/";

    // ─────────────────────────────────────────────────
    // LIRE un fichier JSON -> liste de maps
    // ─────────────────────────────────────────────────
    public static List<Map<String, String>> lire(String nomFichier) {
        List<Map<String, String>> resultats = new ArrayList<>();
        File file = new File(BASE_PATH + nomFichier);
        if (!file.exists()) {
            creerFichierVide(file);
            return resultats;
        }
        try {
            String contenu = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8).trim();
            if (contenu.isEmpty() || contenu.equals("[]")) return resultats;

            // Supprimer [ et ] exterieurs
            if (contenu.startsWith("[")) contenu = contenu.substring(1);
            if (contenu.endsWith("]")) contenu = contenu.substring(0, contenu.length() - 1);

            // Decouper par objets { ... }
            List<String> objets = decouperObjets(contenu);
            for (String obj : objets) {
                Map<String, String> map = parseObjet(obj);
                if (!map.isEmpty()) resultats.add(map);
            }
        } catch (IOException e) {
            System.out.println("[ERREUR JSON] Lecture " + nomFichier + ": " + e.getMessage());
        }
        return resultats;
    }

    // ─────────────────────────────────────────────────
    // ECRIRE une liste de maps -> fichier JSON
    // ─────────────────────────────────────────────────
    public static void ecrire(String nomFichier, List<Map<String, String>> donnees) {
        File file = new File(BASE_PATH + nomFichier);
        file.getParentFile().mkdirs();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("[");
            for (int i = 0; i < donnees.size(); i++) {
                pw.print("  {");
                Map<String, String> obj = donnees.get(i);
                List<String> cles = new ArrayList<>(obj.keySet());
                for (int j = 0; j < cles.size(); j++) {
                    String cle = cles.get(j);
                    pw.print("\"" + cle + "\": \"" + echapper(obj.get(cle)) + "\"");
                    if (j < cles.size() - 1) pw.print(", ");
                }
                pw.print("}");
                if (i < donnees.size() - 1) pw.print(",");
                pw.println();
            }
            pw.println("]");
        } catch (IOException e) {
            System.out.println("[ERREUR JSON] Ecriture " + nomFichier + ": " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────
    // HELPERS PARSING
    // ─────────────────────────────────────────────────
    private static List<String> decouperObjets(String contenu) {
        List<String> objets = new ArrayList<>();
        int debut = -1;
        int profondeur = 0;
        for (int i = 0; i < contenu.length(); i++) {
            char c = contenu.charAt(i);
            if (c == '{') {
                if (profondeur == 0) debut = i;
                profondeur++;
            } else if (c == '}') {
                profondeur--;
                if (profondeur == 0 && debut >= 0) {
                    objets.add(contenu.substring(debut, i + 1));
                    debut = -1;
                }
            }
        }
        return objets;
    }

    private static Map<String, String> parseObjet(String obj) {
        Map<String, String> map = new LinkedHashMap<>();
        // Supprimer { et }
        obj = obj.trim();
        if (obj.startsWith("{")) obj = obj.substring(1);
        if (obj.endsWith("}")) obj = obj.substring(0, obj.length() - 1);

        // Regex-free parsing: cherche "cle": "valeur"
        // On split sur ," mais en faisant attention aux valeurs embedded
        int i = 0;
        while (i < obj.length()) {
            // Cherche debut de cle
            int dqCle = obj.indexOf('"', i);
            if (dqCle < 0) break;
            int fqCle = obj.indexOf('"', dqCle + 1);
            if (fqCle < 0) break;
            String cle = obj.substring(dqCle + 1, fqCle);

            // Cherche ':'
            int colon = obj.indexOf(':', fqCle + 1);
            if (colon < 0) break;

            // Cherche debut valeur
            int dqVal = obj.indexOf('"', colon + 1);
            if (dqVal < 0) break;
            int fqVal = trouverFinChaine(obj, dqVal + 1);
            if (fqVal < 0) break;
            String valeur = obj.substring(dqVal + 1, fqVal);

            map.put(cle, desechapper(valeur));
            i = fqVal + 1;
        }
        return map;
    }

    private static int trouverFinChaine(String s, int debut) {
        for (int i = debut; i < s.length(); i++) {
            if (s.charAt(i) == '\\') { i++; continue; }
            if (s.charAt(i) == '"') return i;
        }
        return -1;
    }

    private static String echapper(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String desechapper(String s) {
        if (s == null) return "";
        return s.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n");
    }

    private static void creerFichierVide(File file) {
        try {
            file.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(file), StandardCharsets.UTF_8))) {
                pw.println("[]");
            }
        } catch (IOException e) {
            // silence
        }
    }
}
