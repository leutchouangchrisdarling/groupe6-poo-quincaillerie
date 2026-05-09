package services;

import java.io.*;
import java.util.*;
import modele.*;

public class CsvService {
    private static final String BASE_PATH = "structureprincipal/donnees/";

    public static List<String[]> lireFichier(String nomFichier) {
        List<String[]> lignes = new ArrayList<>();
        File file = new File(BASE_PATH + nomFichier);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Erreur création fichier : " + e.getMessage());
            }
            return lignes;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if (!ligne.trim().isEmpty()) {
                    lignes.add(ligne.split(","));
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lecture " + nomFichier + ": " + e.getMessage());
        }
        return lignes;
    }

    public static void ecrireFichier(String nomFichier, List<String> lignesCSV) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(BASE_PATH + nomFichier))) {
            for (String ligne : lignesCSV) {
                pw.println(ligne);
            }
        } catch (IOException e) {
            System.out.println("Erreur écriture " + nomFichier + ": " + e.getMessage());
        }
    }
}
