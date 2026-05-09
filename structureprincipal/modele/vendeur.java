package modele;

import outils.Colors;

public class Vendeur extends Utilisateur {
    private String motDePasse;

    public Vendeur(int id, String nom, String motDePasse) {
        super(id, nom, "VENDEUR");
        this.motDePasse = motDePasse;
    }

    public String getMotDePasse() { return motDePasse; }

    @Override
    public void afficherInfos() {
        System.out.println(Colors.CYAN + "Vendeur ID: " + id + " | Nom: " + nom + Colors.RESET);
    }
}
