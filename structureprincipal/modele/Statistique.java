package modele;

/**
 * Classe Statistique (placeholder pour extensions futures).
 * Les statistiques sont calculees dans StatistiqueService.
 */
public class Statistique {
    private String date;
    private double caJournalier;
    private int    nombreVentes;

    public Statistique(String date, double caJournalier, int nombreVentes) {
        this.date          = date;
        this.caJournalier  = caJournalier;
        this.nombreVentes  = nombreVentes;
    }

    public String getDate()         { return date; }
    public double getCaJournalier() { return caJournalier; }
    public int    getNombreVentes() { return nombreVentes; }
}
