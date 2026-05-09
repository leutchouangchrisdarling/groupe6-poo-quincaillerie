package modele;

public abstract class Utilisateur {
    protected int id;
    protected String nom;
    protected String role;

    public Utilisateur(int id, String nom, String role) {
        this.id = id;
        this.nom = nom;
        this.role = role;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getRole() { return role; }

    public abstract void afficherInfos();
}
