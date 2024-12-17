import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Relation {
    private String nom;
    private Attribut[] listAttribut;
    private Object[][] valeurs;
    private int nombreDeLignes;

    public Relation(String name, Attribut[] listAttribut) {
        this.listAttribut = listAttribut;
        this.valeurs = new Object[0][listAttribut.length];
        this.nombreDeLignes = 0;
        this.nom = name;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void insererDonnees(Object[] nouvelleValeur) throws Exception {
        // System.out.println("nouvelleValeur.length: "+nouvelleValeur.length+",
        // listAttribut.length: "+listAttribut.length);
        if (nouvelleValeur.length != listAttribut.length) {
            throw new IllegalArgumentException("Le nombre de valeurs doit correspondre au nombre d'attributs.");
        }
        for (int i = 0; i < listAttribut.length; i++) {
            // System.out.println("listAttribut[i].getDomaine().isInstance(nouvelleValeur["+i+"]):
            // "+listAttribut[i].getDomaine().isInstance(nouvelleValeur[i]));
            // System.out.println("listAttribut["+i+"].getDomaine()
            // "+listAttribut[i].getDomaine()+" nouvelleValeur["+i+"].toString()
            // "+nouvelleValeur[i].toString());
            // System.out.println("nouvelleValeur["+i+"]: "+nouvelleValeur[i]);

            if (nouvelleValeur[i] != null && !listAttribut[i].getDomaine().appartient(nouvelleValeur[i])) {
                throw new IllegalArgumentException(" Le domaine pour l'attribut: " + listAttribut[i].getNom()
                        + " ne contient pas la valeur: " + nouvelleValeur[i] + " ");
            }
        }
        Object[][] nouvellesValeurs = new Object[nombreDeLignes + 1][listAttribut.length];
        for (int i = 0; i < nombreDeLignes; i++) {
            nouvellesValeurs[i] = valeurs[i].clone();
        }
        nouvellesValeurs[nombreDeLignes] = nouvelleValeur;
        valeurs = nouvellesValeurs.clone();
        nombreDeLignes++;

    }

    public void afficherValeurs() {

        for (Attribut attribut : listAttribut) {
            System.out.print(attribut.getNom() + "\t");
        }
        System.out.println();

        for (Object[] ligne : valeurs) {
            for (Object val : ligne) {
                System.out.print(val + "\t");
            }
            System.out.println();
        }
    }

    public Relation union(Relation autreRelation, String[] nouveauxNoms, String nom) throws Exception {

        if (this.listAttribut.length != autreRelation.listAttribut.length) {
            throw new IllegalArgumentException("Le nombre d'attributs doit être le même dans les deux relations.");
        }

        Attribut[] nouveauxAttributs = new Attribut[nouveauxNoms.length];
        for (int i = 0; i < nouveauxNoms.length; i++) {
            Ensemble type = determineType(this.listAttribut[i].getDomaine(),
                    autreRelation.listAttribut[i].getDomaine());
            nouveauxAttributs[i] = new Attribut(nouveauxNoms[i], type);
        }

        Relation nouvelleRelation = new Relation(nom, nouveauxAttributs);

        for (Object[] ligne : this.valeurs) {
            nouvelleRelation.insererDonnees(ligne);
        }

        for (Object[] ligne : autreRelation.valeurs) {
            nouvelleRelation.insererDonnees(ligne);
        }

        nouvelleRelation.eliminerDoublons();

        return nouvelleRelation;
    }

    private void eliminerDoublons() {
        Object[][] valeursUniq = new Object[nombreDeLignes][listAttribut.length];
        int index = 0;

        for (int i = 0; i < nombreDeLignes; i++) {
            boolean estDoublon = false;
            for (int j = 0; j < index; j++) {
                if (lignesEgales(valeurs[i], valeursUniq[j])) {
                    estDoublon = true;
                    break;
                }
            }
            if (!estDoublon) {
                valeursUniq[index] = new Object[listAttribut.length];
                for (int k = 0; k < listAttribut.length; k++) {
                    valeursUniq[index][k] = valeurs[i][k] != null ? valeurs[i][k] : "null";
                }
                index++;
            }
        }

        Object[][] nouvellesValeurs = new Object[index][listAttribut.length];
        for (int i = 0; i < index; i++) {
            for (int j = 0; j < listAttribut.length; j++) {
                nouvellesValeurs[i][j] = valeursUniq[i][j];
            }
        }
        valeurs = nouvellesValeurs;
        nombreDeLignes = index;
    }

    public Relation intersection(Relation autreRelation, String[] nouveauxNoms, String nom) throws Exception {
        if (this.listAttribut.length != autreRelation.listAttribut.length) {
            throw new IllegalArgumentException("Le nombre d'attributs doit être le même dans les deux relations.");
        }

        Attribut[] nouveauxAttributs = new Attribut[nouveauxNoms.length];
        for (int i = 0; i < nouveauxNoms.length; i++) {
            Ensemble stock = new Ensemble();
            stock.ajouter(autreRelation.valeurs[0][i]);
            Ensemble type = determineType(this.listAttribut[i].getDomaine(), stock);
            nouveauxAttributs[i] = new Attribut(nouveauxNoms[i], type);
        }

        // System.out.println("new Attribut :");
        for (int i = 0; i < nouveauxAttributs.length; i++) {
            //System.out.println("nouveauxAttributs[" + i + "]: " + nouveauxAttributs[i].getNom());
            for (int j = 0; j < nouveauxAttributs[i].getDomaine().getElements().length; j++) {
                if (nouveauxAttributs[i].getDomaine().getElements()[j] != null) {
                    //System.out.println("GetDomaine().getElements()[" + j + "]:"
                            //+ nouveauxAttributs[i].getDomaine().getElements()[j]);
                }
            }
        }

        Relation nouvelleRelation = new Relation(nom, nouveauxAttributs);

        for (Object[] ligne1 : this.valeurs) {
            for (Object[] ligne2 : autreRelation.valeurs) {
                if (lignesEgales(ligne1, ligne2)) {
                    nouvelleRelation.insererDonnees(ligne1);
                }
            }
        }
        return nouvelleRelation;
    }

    private boolean lignesEgales(Object[] ligne1, Object[] ligne2) {
        for (int i = 0; i < listAttribut.length; i++) {
            if (!valeursEgaux(ligne1[i], ligne2[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean valeursEgaux(Object val1, Object val2) {
        if (val1 == null && val2 == null) {
            return true;
        } else if (val1 == null || val2 == null) {
            return false;
        } else if (val1.getClass().equals(val2.getClass())) {
            return val1.equals(val2);
        } else {
            return val1.toString().equals(val2.toString());
        }
    }

    private Ensemble determineType(Ensemble type1, Ensemble type2) throws Exception {
        return type1.union(type2);
    }

    public Relation difference(Relation autreRelation, String[] nouveauxNoms, String nom) throws Exception {
        // System.out.println("this.listAttribut.length: "+this.listAttribut.length+",
        // autreRelation.listAttribut.length: "+autreRelation.listAttribut.length);
        if (this.listAttribut.length != autreRelation.listAttribut.length) {
            throw new IllegalArgumentException("Le nombre d'attributs doit être le même dans les deux relations.");
        }
        Attribut[] nouveauxAttributs = new Attribut[nouveauxNoms.length];
        for (int i = 0; i < nouveauxNoms.length; i++) {
            Ensemble type = determineType(this.listAttribut[i].getDomaine(),
                    autreRelation.listAttribut[i].getDomaine());
            nouveauxAttributs[i] = new Attribut(nouveauxNoms[i], type);
        }
        Relation nouvelleRelation = new Relation(nom, nouveauxAttributs);
        for (Object[] ligne1 : this.valeurs) {
            boolean estDoublon = false;
            for (Object[] ligne2 : autreRelation.valeurs) {
                if (lignesEgales(ligne1, ligne2)) {
                    estDoublon = true;
                    break;
                }
            }
            if (!estDoublon) {
                nouvelleRelation.insererDonnees(ligne1);
            }
        }
        return nouvelleRelation;
    }

    public Relation projection(String[] nomsAttributs) throws Exception {
        int count = 0;
        Attribut[] attributsProjetes = new Attribut[nomsAttributs.length];
        // verifieny raha misy tsy ao ny anarany attribut nomena
        for (String nomAttribut : nomsAttributs) {
            for (Attribut attribut : listAttribut) {
                if (attribut.getNom().equals(nomAttribut)) {
                    attributsProjetes[count++] = attribut.reproduit();
                    break;
                }
            }
        }
        // maka anle attribut
        Attribut[] resultatAttributs = new Attribut[count];
        for (int i = 0; i < count; i++) {
            resultatAttributs[i] = attributsProjetes[i].reproduit();
        }
        Relation nouvelleRelation = new Relation("Projection", resultatAttributs);
        for (Object[] ligne : valeurs) {
            Object[] nouvelleValeur = new Object[count];
            int index = 0;
            for (int i = 0; i < listAttribut.length; i++) {
                for (Attribut attr : resultatAttributs) {
                    if (listAttribut[i].getNom().equals(attr.getNom())) {
                        nouvelleValeur[index++] = ligne[i];
                        Ensemble stock = new Ensemble();
                        stock.ajouter(ligne[i]);
                        attr.setDomaine(determineType(attr.getDomaine(), stock));
                        break;
                    }
                }
            }
            nouvelleRelation.insererDonnees(nouvelleValeur);
        }
        nouvelleRelation.eliminerDoublons();
        return nouvelleRelation;
    }

    public Relation produitCartesien(Relation r2) throws Exception {
        Attribut[] attributsCombines = new Attribut[listAttribut.length + r2.listAttribut.length];

        int index = 0;
        for (Attribut attr : listAttribut) {
            attributsCombines[index++] = attr.reproduit();
            // System.out.println(" " + attr.getNom() +" ");
        }
        for (Attribut attr : r2.listAttribut) {
            attributsCombines[index++] = attr.reproduit();
            // System.out.println(" " + attr.getNom() +" ");
        }
        int l = 0;
        Object[] no = new Object[valeurs[0].length + r2.valeurs.length];
        for (l = 0; l < valeurs[0].length; l++) {
            no[l] = valeurs[0][l];
        }
        for (int j = 0; j < r2.valeurs[0].length; j++) {
            no[l] = r2.valeurs[0][j];
            l++;
        }

        for (int i = 0; i < attributsCombines.length; i++) {
            Ensemble stock = new Ensemble();
            stock.ajouter(no[i]);
            Ensemble nou = determineType(attributsCombines[i].getDomaine(), stock);
            attributsCombines[i].setDomaine(nou);
        }

        Relation nouvelleRelation = new Relation("ProduitCartesien", attributsCombines);

        for (Object[] ligne1 : valeurs) {
            for (Object[] ligne2 : r2.valeurs) {
                Object[] nouvelleLigne = new Object[ligne1.length + ligne2.length];
                int i = 0;
                for (Object val : ligne1) {
                    // System.out.println(" "+val.toString()+" ");
                    // System.out.println(val.getClass());
                    nouvelleLigne[i++] = val;
                }
                for (Object val : ligne2) {
                    // System.out.println(" "+val.toString()+" ");
                    // System.out.println(val.getClass());
                    nouvelleLigne[i++] = val;
                }
                nouvelleRelation.insererDonnees(nouvelleLigne);
            }
        }
        return nouvelleRelation;
    }

    @SuppressWarnings("rawtypes")
    public Relation selection(String condition) throws Exception {
        String[] parts = condition.split(" ");
        if (parts.length != 3) {
            System.out.println("Condition non valide.");
            return null;
        }

        String nomAttribut = parts[0];
        String operateur = parts[1];
        String valeur = parts[2].replace("'", "");
        Attribut attribut = null;
        for (Attribut att : listAttribut) {
            if (att.getNom().equals(nomAttribut)) {
                attribut = att;
                break;
            }
        }
        if (attribut == null) {
            System.out.println("Attribut " + nomAttribut + " n'existe pas.");
            return null;
        }
        boolean validOp = false;
        for (int i = 0; i < attribut.getDomaine().getElements().length; i++) {
            if (attribut.getDomaine().getElements()[i].getClass().equals(Class.class)) {
                if (valideOperateur((Class) attribut.getDomaine().getElements()[i], operateur)) {
                    validOp = true;
                    break;
                }
            } else {
                if (valideOperateur(attribut.getDomaine().getElements()[i].getClass(), operateur)) {
                    validOp = true;
                    break;
                }
            }
        }

        if (validOp == false) {
            System.out.println("Operateur non valide pour le type de l'attribut.");
            return null;
        }

        Relation resultat = new Relation(getNom() + "_selection", listAttribut);

        boolean estattribut=false;
        String nom=" ";

        for (Attribut af: listAttribut) {
            //System.out.println("comparaison des valeurs : "+valeur+"   au nom attribut "+af.getNom()+"  ::"+valeur.compareTo(af.getNom()));
            if (valeur.compareTo(af.getNom())==0) {
                estattribut=true;
                nom=af.getNom();
                //System.out.println(" la valeur est un autre colonne: "+valeur);
                break;
            }
        }

        for (Object[] ligne : valeurs) {
            if (estattribut==true) {
                if (verifierCondition(attribut, ligne, operateur,   String.valueOf( ligne[indexOfAttribut(nom)]))) {
                    resultat.insererDonnees(ligne);
                }
            } else {
                if (verifierCondition(attribut, ligne, operateur, valeur)) {
                    resultat.insererDonnees(ligne);
                }
            }
        }
        // resultat.afficherValeurs();
        return resultat;
    }

    public Relation selectionMultiple(String condition) throws Exception {
        if (condition.split(" ").length == 3) {
            return selection(condition);
        }

        String orPart = "or\\(|\\)or|\\)or\\(|or \\(|\\) or|\\) or \\(|OR\\(|\\)OR|\\)OR\\(|OR \\(|\\) OR|\\) OR \\(|";
        orPart += "ou\\(|\\)ou|\\)ou\\(|ou \\(|\\) ou|\\) ou \\(|OU\\(|\\)OU|\\)OU\\(|OU \\(|\\) OU|\\) OU \\(|\\";
        String andPart = "and\\(|\\)and|\\)and\\(|and \\(|\\) and|\\) and \\(|AND\\(|\\)AND|\\)AND\\(|AND \\(|\\) AND|\\) AND \\(|\\";
        andPart += "et\\(|\\)et|\\)et\\(|et \\(|\\) et|\\) et \\(|ET\\(|\\)ET|\\)ET|\\(ET \\(|\\) ET|\\) ET \\( ";
        //System.out.println(condition);
        // Séparation des conditions
        String[] parts = condition.split(orPart + andPart);
        Relation resultat = new Relation("resultat", listAttribut);

        for (int i = 0; i < parts.length; i++) {
            // Nettoyage des parenthèses
            // System.out.println("parts["+i+"]: "+parts[i]);
            parts[i] = parts[i].replaceAll("\\)|\\(", " ").trim();

            // Vérification de l'opérateur
            int index = operator(condition, parts[i]);
            //System.out.println("index: " + index + " , parts[" + i + "]: " + parts[i]);
            Relation selectionResult = selectionMultiple(parts[i]);
            //System.out.println("Selection result ");
            //selectionResult.afficherValeurs();

            // if (selectionResult.nombreDeLignes==0) {
            //     return selectionResult;
            // }

            String[] newNom = new String[listAttribut.length];
            for (int j = 0; j < newNom.length; j++) {
                newNom[j] = listAttribut[j].getNom();
            }

            if (index == -1) {
                // Si aucune condition logique n'est présente, union
                resultat = resultat.union(selectionResult, newNom, "UnionResultat");
            } else if (index == 1) {
                // Intersection si "and" trouvé
                resultat = resultat.intersection(selectionResult, newNom, "IntersectionResultat");
            } else if (index == 0) {
                // Union si "or" trouvé
                resultat = resultat.union(selectionResult, newNom, "UnionResultat");
            }
            

            //resultat.afficherValeurs();
            //System.out.println();
        }

        return resultat;
    }

    public int operator(String condition, String partCondition) {
        int index = condition.indexOf(partCondition);
        for (int i = index; i >= 0; i--) {
            if (condition.startsWith("and", i) || condition.startsWith("AND", i)
                    || condition.startsWith("et", i) || condition.startsWith("ET", i)
                    || condition.startsWith("&&", i)) {
                return 1;
            } else if (condition.startsWith("or", i) || condition.startsWith("OR", i)
                    || condition.startsWith("ou", i) || condition.startsWith("OU", i)
                    || condition.startsWith("||", i)) {
                return 0;
            }
        }
        return -1;
    }

    private boolean verifierCondition(Attribut attribut, Object[] ligne, String operateur, String valeur) {
        Object val = ligne[obtenirIndexAttribut(attribut.getNom())];
        
        if (val.getClass().equals(String.class) || val.getClass().equals(Character.class)) {
            if (operateur.equals("=")) {
                return val.toString().equals(valeur);
            } else if (operateur.equals("!=")) {
                return !val.toString().equals(valeur);
            }
        } else if (Number.class.isAssignableFrom(val.getClass())) {
            Double valeurNombre = Double.parseDouble(valeur);
            Double valCast = Double.parseDouble(val.toString());
            switch (operateur) {
                case ">":
                    return valCast > valeurNombre;
                case ">=":
                    return valCast >= valeurNombre;
                case "<":
                    return valCast < valeurNombre;
                case "<=":
                    return valCast <= valeurNombre;
                case "=":
                    return valCast.equals(valeurNombre);
                default:
                    return false;
            }
        } else if (val.getClass().equals(Boolean.class)) {
            boolean valeurBooleen = Boolean.parseBoolean(valeur);
            if (operateur.equals("is")) {
                return val.equals(valeurBooleen);
            } else if (operateur.equals("isnot")) {
                return !val.equals(valeurBooleen);
            }
        } else if (val.getClass().equals(Date.class)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // Format de date à adapter
            try {
                Date valeurDate = format.parse(valeur);
                Date valDate = (Date) val;
                switch (operateur) {
                    case ">":
                        return valDate.after(valeurDate);
                    case ">=":
                        return !valDate.before(valeurDate);
                    case "<":
                        return valDate.before(valeurDate);
                    case "<=":
                        return !valDate.after(valeurDate);
                    case "=":
                        return valDate.equals(valeurDate);
                    default:
                        return false;
                }
            } catch (ParseException e) {
                // Gérer l'exception, par exemple en retournant false
                return false;
            }
        }
        return false;
    }

    private boolean valideOperateur(Class<?> type, String operateur) {
        if (type.equals(String.class) || type.equals(Character.class)) {
            return operateur.equals("=") || operateur.equals("!=");
        } else if (Number.class.isAssignableFrom(type)) {
            return operateur.equals(">") || operateur.equals(">=") || operateur.equals("<") || operateur.equals("<=")
                    || operateur.equals("=");
        } else if (type.equals(Boolean.class)) {
            return operateur.equals("is") || operateur.equals("isnot");
        } else if (type.equals(Date.class)) {
            return operateur.equals(">") || operateur.equals(">=") || operateur.equals("<") || operateur.equals("<=")
                    || operateur.equals("=");
        }
        return false;
    }

    private int obtenirIndexAttribut(String nomAttribut) {
        for (int i = 0; i < listAttribut.length; i++) {
            if (listAttribut[i].getNom() == nomAttribut) {
                return i;
            }
        }
        return 0;
    }

    public Relation jointureNaturelle(Relation r2, String nomNouvelleRelation) throws Exception {
        Attribut[] attributsCommuns = chercherAttributsCommuns(r2);

        if (attributsCommuns.length == 0) {
            throw new IllegalArgumentException("Aucun attribut commun trouvé entre les deux relations.");
        }

        Attribut[] nouveauxAttributs = creerAttributsSansDoublons(r2, attributsCommuns);

        Relation nouvelleRelation = new Relation(nomNouvelleRelation, nouveauxAttributs);

        for (Object[] ligne1 : valeurs) {
            for (Object[] ligne2 : r2.valeurs) {
                if (valeursEgalesSurAttributsCommuns(ligne1, ligne2, r2, attributsCommuns)) {
                    Object[] nouvelleLigne = creerLigneSansDoublons(ligne1, ligne2, r2, attributsCommuns);
                    nouvelleRelation.insererDonnees(nouvelleLigne);
                }
            }
        }

        return nouvelleRelation;
    }

    private Attribut[] chercherAttributsCommuns(Relation r2) {
        int count = 0;
        Attribut[] communs = new Attribut[Math.min(listAttribut.length, r2.listAttribut.length)];

        for (Attribut attr1 : listAttribut) {
            for (Attribut attr2 : r2.listAttribut) {
                if (attr1.getNom().compareTo(attr2.getNom())==0 && attr1.getDomaine().egal(attr2.getDomaine())) {
                    communs[count++] = attr1;
                }
            }
        }

        Attribut[] result = new Attribut[count];
        System.arraycopy(communs, 0, result, 0, count);
        return result;
    }

    private Attribut[] creerAttributsSansDoublons(Relation r2, Attribut[] attributsCommuns) {
        int taille = listAttribut.length + r2.listAttribut.length - attributsCommuns.length;
        Attribut[] result = new Attribut[taille];
        int index = 0;

        for (Attribut attr1 : listAttribut) {
            result[index++] = attr1;
        }

        for (Attribut attr2 : r2.listAttribut) {
            boolean estCommun = false;
            for (Attribut commun : attributsCommuns) {
                if (attr2.getNom().equals(commun.getNom())) {
                    estCommun = true;
                    break;
                }
            }
            if (!estCommun) {
                result[index++] = attr2;
            }
        }

        return result;
    }

    private boolean valeursEgalesSurAttributsCommuns(Object[] ligne1, Object[] ligne2, Relation r2,
            Attribut[] attributsCommuns) {
        for (Attribut commun : attributsCommuns) {
            int index1 = indexOfAttribut(commun.getNom());
            int index2 = r2.indexOfAttribut(commun.getNom());
            if (!valeursEgaux(ligne1[index1], ligne2[index2])) {
                return false;
            }
        }
        return true;
    }

    private Object[] creerLigneSansDoublons(Object[] ligne1, Object[] ligne2, Relation r2,
            Attribut[] attributsCommuns) {
        Object[] nouvelleLigne = new Object[listAttribut.length + r2.listAttribut.length - attributsCommuns.length];
        int index = 0;

        for (Object val : ligne1) {
            nouvelleLigne[index++] = val;
        }

        for (int i = 0; i < ligne2.length; i++) {
            boolean estCommun = false;
            for (Attribut commun : attributsCommuns) {
                if (r2.listAttribut[i].getNom().equals(commun.getNom())) {
                    estCommun = true;
                    break;
                }
            }
            if (!estCommun) {
                nouvelleLigne[index++] = ligne2[i];
            }
        }

        return nouvelleLigne;
    }

    public int indexOfAttribut(String nomAttribut) {
        for (int i = 0; i < listAttribut.length; i++) {
            if (listAttribut[i].getNom().equals(nomAttribut)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Attribut introuvable: " + nomAttribut);
    }

    public Relation tetaJointure(Relation r2, String condition, String nomNouvelleRelation) throws Exception {
        String[] elementsCondition = analyserCondition(condition);
        String nomAttr1 = elementsCondition[0];
        String nomAttr2 = elementsCondition[1];
        String operateur = elementsCondition[2];

        int indexAttr1 = indexOfAttribut(nomAttr1.split("\\.")[1]);
        int indexAttr2 = r2.indexOfAttribut(nomAttr2.split("\\.")[1]);

        Attribut attr1 = listAttribut[indexAttr1];
        Attribut attr2 = r2.listAttribut[indexAttr2];

        if (!attr1.getDomaine().egal(attr2.getDomaine())) {
            throw new IllegalArgumentException("Les types des attributs ne correspondent pas.");
        }

        boolean valOp = false;

        for (int i = 0; i < attr1.getDomaine().getElements().length; i++) {
            if (attr1.getDomaine().getElements()[i].getClass().equals(Class.class)) {
                if (valideOperateur((Class<?>)attr1.getDomaine().getElements()[i], operateur)) {
                    valOp = true;
                    break;
                }    
            } else {
                if (valideOperateur(attr1.getDomaine().getElements()[i].getClass(), operateur)) {
                    valOp = true;
                    break;
                }
            }
        }

        if (valOp == false) {
            return null;
        }

        Attribut[] nouveauxAttributs = creerAttributsSansDoublons(r2, new Attribut[0]);
        Relation nouvelleRelation = new Relation(nomNouvelleRelation, nouveauxAttributs);

        for (Object[] ligne1 : valeurs) {
            for (Object[] ligne2 : r2.valeurs) {
                if (verifierCondition(ligne1[indexAttr1], ligne2[indexAttr2], operateur)) {
                    Object[] nouvelleLigne = creerLigneSansDoublons(ligne1, ligne2, r2, new Attribut[0]);
                    nouvelleRelation.insererDonnees(nouvelleLigne);
                }
            }
        }

        return nouvelleRelation;
    }

    private String[] analyserCondition(String condition) {
        String[] elements = condition.split(" ");
        if (elements.length != 3) {
            throw new IllegalArgumentException("Condition mal formée: " + condition);
        }
        return new String[] { elements[0], elements[2], elements[1] };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private boolean verifierCondition(Object val1, Object val2, String operateur) {
        
        if (val1 instanceof Date && val2 instanceof Date) {
            // Cast les objets en Date
            Date date1 = (Date) val1;
            Date date2 = (Date) val2;

            switch (operateur) {
                case "=":
                    return date1.equals(date2);
                case "!=":
                    return !date1.equals(date2);
                case ">":
                    return date1.after(date2);
                case "<":
                    return date1.before(date2);
                case ">=":
                    return !date1.before(date2);
                case "<=":
                    return !date1.after(date2);
                default:
                    throw new IllegalArgumentException("Opérateur non reconnu: " + operateur);
            }
        }

        switch (operateur) {
            case "=":
                return val1.equals(val2);
            case "!=":
                return !val1.equals(val2);
            case ">":
                return ((Comparable) val1).compareTo(val2) > 0;
            case "<":
                return ((Comparable) val1).compareTo(val2) < 0;
            case ">=":
                return ((Comparable) val1).compareTo(val2) >= 0;
            case "<=":
                return ((Comparable) val1).compareTo(val2) <= 0;
            case "is":
                return val1.equals(val2);
            case "isnot":
                return !val1.equals(val2);
            default:
                throw new IllegalArgumentException("Opérateur non reconnu: " + operateur);
        }
    }

}
