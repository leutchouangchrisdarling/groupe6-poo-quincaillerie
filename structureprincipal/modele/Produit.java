package modele;

public class Produit {
  private int id;
  private String nom;
  private String categorie;
  private double prix;
  private int quantiteStock;
  private int seuilAlerte;

  public Produit(int id, String nom, String categorie, double prix, int quantiteStock, int seuilAlerte) {
    this.id = id;
    this.nom = nom;
    this.categorie = categorie;
    this.prix = prix;
    this.quantiteStock = quantiteStock;
    this.seuilAlerte = seuilAlerte;
  }

  public int getId() {
    return id;
  }

  public String getNom() {
    return nom;
  }

  public String getCategorie() {
    return categorie;
  }

  public double getPrix() {
    return prix;
  }

  public int getQuantiteStock() {
    return quantiteStock;
  }

  public int getSeuilAlerte() {
    return seuilAlerte;
  }

  public void setQuantiteStock(int q) {
    this.quantiteStock = q;
  }

  public void setPrix(double p) {
    this.prix = p;
  }

  public boolean estEnAlerte() {
    return quantiteStock <= seuilAlerte;
  }

  @Override
  public String toString() {
    return String.format("%-4d | %-24s | %-14s | %-14s | %-6d",
        id, nom, categorie,
        outils.Formatter.formatMontant(prix),
        quantiteStock);
  }
}
