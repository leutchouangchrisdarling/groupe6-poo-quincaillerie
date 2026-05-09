package modele;

import outils.Colors;

public class Proprietaire extends Utilisateur {
    private String motDePasse;

    public Proprietaire(int id, String nom, String motDePasse) {
        super(id, nom, "PROPRIETAIRE");
        this.motDePasse = motDePasse;
    }

    public String getMotDePasse() { return motDePasse; }

    @Override
    public void afficherInfos() {
        System.out.println(Colors.RED + "Propriétaire ID: " + id + " | Nom: " + nom + Colors.RESET);
    }
}
