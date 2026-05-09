package modele;

import outils.Colors;

public class Client extends Utilisateur {

  public Client(int id, String nom) {
    super(id, nom, "CLIENT");
  }

  @Override
  public void afficherInfos() {
    System.out.println(Colors.BLUE + "Client ID: " + id + " | Nom: " + nom + Colors.RESET);
  }
}
