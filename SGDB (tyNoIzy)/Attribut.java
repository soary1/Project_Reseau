public class Attribut {
    private String nom;  
    private Ensemble domaine;  

    public Attribut(String nom, Ensemble domaine) {
        this.nom = nom;
        this.domaine = domaine;
    }
    // loic edit this file
    // soaryyyy
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDomaine(Ensemble domaine) {
        this.domaine = domaine;
    }

    public Ensemble getDomaine() {
        return domaine;
    }

    public Attribut reproduit() {
        return new Attribut(nom, domaine);
    }
}
