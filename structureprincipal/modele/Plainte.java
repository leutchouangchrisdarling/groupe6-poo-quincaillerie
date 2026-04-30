package modele;

public class Plainte {
  private int id;
  private int clientId;
  private String date;
  private String contenu;

  public Plainte(int id, int clientId, String date, String contenu) {
    this.id = id;
    this.clientId = clientId;
    this.date = date;
    this.contenu = contenu;
  }

  public int getId() {
    return id;
  }

  public int getClientId() {
    return clientId;
  }

  public String getDate() {
    return date;
  }

  public String getContenu() {
    return contenu;
  }

  @Override
  public String toString() {
    return "Plainte #" + id + " [" + date + "] Client " + clientId + " : " + contenu;
  }
}
