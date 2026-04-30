package modele;

import java.util.Map;
import outils.Formatter;

public class Commande {
  private int id;
  private int clientId;
  private Map<Integer, Integer> produitsQuantites;
  private String statut;
  private double montantTotal;

  public Commande(int id, int clientId, Map<Integer, Integer> produitsQuantites,
      double montantTotal, String statut) {
    this.id = id;
    this.clientId = clientId;
    this.produitsQuantites = produitsQuantites;
    this.montantTotal = montantTotal;
    this.statut = statut;
  }

  public int getId() {
    return id;
  }

  public int getClientId() {
    return clientId;
  }

  public Map<Integer, Integer> getProduitsQuantites() {
    return produitsQuantites;
  }

  public String getStatut() {
    return statut;
  }

  public double getMontantTotal() {
    return montantTotal;
  }

  public void setStatut(String s) {
    this.statut = s;
  }

  @Override
  public String toString() {
    return "Commande #" + id + " | Client: " + clientId
        + " | Total: " + Formatter.formatMontant(montantTotal)
        + " | Statut: " + statut;
  }
}
