public class Main {
    public static void main(String[] args) throws Exception {

        ///////////////////////// TEST FAHAROA UNION INTERSECTION PROJECTION NETY
        // Attribut[] attributs1 = {
        // new Attribut("Nom", new Ensemble(new Object[]{String.class})),
        // new Attribut("Age", new Ensemble(new Object[] {Integer.class})),
        // new Attribut("EstEtudiant", new Ensemble(new Object[] {Boolean.class}))
        // };

        // Relation relation1 = new Relation("individus1",attributs1);

        // relation1.insererDonnees(new Object[]{"Alice", 30, true});
        // relation1.insererDonnees(new Object[]{"Bob", 25, false});
        // relation1.insererDonnees(new Object[]{"Charlie", 22, true});
        // relation1.insererDonnees(new Object[]{"Diana", 28, false});

        // Attribut[] attributs2 = {
        // new Attribut("Nom", new Ensemble(new Object[]{String.class})),
        // new Attribut("Age", new Ensemble(new Object[] {Integer.class})),
        // new Attribut("EstEtudiant", new Ensemble(new Object[] {Boolean.class}))
        // };

        // Relation relation2 = new Relation("individus2",attributs2);

        // relation2.insererDonnees(new Object[]{"Eve", 35, true});
        // relation2.insererDonnees(new Object[]{"Bob", 25, false});
        // relation2.insererDonnees(new Object[]{"Alice", 30, true});
        // relation2.insererDonnees(new Object[]{"Grace", 29, true});

        // System.out.println(" Voici la relation r1 ");
        // relation1.afficherValeurs();
        // System.out.println();

        // System.out.println(" Voici la relation r2 ");
        // relation2.afficherValeurs();
        // System.out.println();

        // String[] nouveauxNoms = {"Nom", "Age", "EstEtudiant"};
        // Relation relationUnion = relation1.union(relation2,
        // nouveauxNoms,"individus3");

        // System.out.println("Contenu de la relation unie :");
        // relationUnion.afficherValeurs();
        // System.out.println(" ");

        // String[] nouveauxNomsIntersection = {"Nom", "Age", "EstEtudiant"};
        // Relation relationIntersection = relation1.intersection(relation2,
        // nouveauxNomsIntersection,"individus4");

        // System.out.println("Contenu de la relation d'intersection :");
        // relationIntersection.afficherValeurs();
        // System.out.println(" ");

        // String[] nouveauxNomsDifference = {"Nom", "Age", "EstEtudiant"};
        // Relation relationDifference = relation1.difference(relation2,
        // nouveauxNomsDifference,"individus5");

        // System.out.println("Contenu de la relation de différence :");
        // relationDifference.afficherValeurs();
        // System.out.println(" ");

        // Relation relationProjection = relation1.projection(new String[] {"Nom"});

        // System.out.println("Contenur de la relation de projection");
        // relationProjection.afficherValeurs();

        ///////////////// TEST VOALOHANY
        
        // try {
        //     Attribut[] attrR1 = {
        //         new Attribut("A", new Ensemble(new Object[] {1, 2, 3, 4 })),
        //         new Attribut("B", new Ensemble(new Object[] {"a", "b", "c"})) };
        
        //         Relation r1 = new Relation("R1", attrR1);
        //         r1.insererDonnees(new Object[]{1, "a"});
        //         r1.insererDonnees(new Object[]{1, "b"});
        //         r1.insererDonnees(new Object[]{3, "a"});
        
        //         Attribut[] attrR2 = {
        //         new Attribut("A", new Ensemble(new Object[] {1, 2, 3, 4})),
        //         new Attribut("B", new Ensemble(new Object[] {"a", "b", "c"})),
        //         new Attribut("E", new Ensemble(new Object[] {"a", "b", "c"})) };
        
        //         Relation r2 = new Relation("R2", attrR2);
        //         r2.insererDonnees(new Object[]{1, "b", "a"});
        //         r2.insererDonnees(new Object[]{2, "b", "c"});
        //         r2.insererDonnees(new Object[]{4, "a", "a"});
        
        //         System.out.println(" R1 ");
        //         r1.afficherValeurs();
        //         System.out.println();
        
        //         System.out.println(" R2 ");
        //         r2.afficherValeurs();
        //         System.out.println();
        
        //         Relation rJoin = r1.jointureNaturelle( r2, "JoinResult");
        //         System.out.println("Jointure naturelle entre r1 et r2: ");
        //         rJoin.afficherValeurs();
        //         System.out.println();
        
        //         Relation rTetaJoin = r1.tetaJointure( r2, "R1.A = R2.A", "TetaJoinResult");
        //         System.out.println("tetaJointure entre r1 et r2 ou R1.A = R2.A");
        //         rTetaJoin.afficherValeurs();

        // } catch (Exception e) {
        //     System.out.print("New Exception");
        //     System.out.println(e.getMessage());
        //     e.printStackTrace();
        // }
        

        //////////////////////////////// TEST FAHATELO PRODUIT CARTESIENNE ET PROJECTION
        //////////////////////////////// NETY

        // try {
        // Attribut nom = new Attribut("nom", new Ensemble(new Object[] { String.class
        // }));
        // Attribut prenom = new Attribut("prenom", new Ensemble(new Object[] {
        // String.class }));
        // Attribut age = new Attribut("age", new Ensemble(new Object[] { Integer.class
        // }));
        // Relation r1 = new Relation("Personne", new Attribut[] { nom, prenom, age });
        // r1.insererDonnees(new Object[] { "Doe", "John", 30 });
        // r1.insererDonnees(new Object[] { "Smith", "Anna", 25 });

        // Attribut marque = new Attribut("marque", new Ensemble(new Object[] {
        // String.class }));
        // Attribut place = new Attribut("place", new Ensemble(new Object[] {
        // Integer.class }));
        // Relation r2 = new Relation("Voiture", new Attribut[] { marque, place });
        // r2.insererDonnees(new Object[] { "Toyota", 4 });
        // r2.insererDonnees(new Object[] { "Honda", 5 });

        // System.out.println("Voici la relation r1");
        // r1.afficherValeurs();
        // System.out.println(" ");

        // System.out.println("Voici la relation r2");
        // r2.afficherValeurs();
        // System.out.println(" ");

        // System.out.println("Projection de r1 sur prenom et age:");
        // Relation projectionResult = r1.projection(new String[] { "prenom", "age" });
        // projectionResult.afficherValeurs();
        // System.out.println();

        // System.out.println("Produit Cartesien de r1 et r2:");
        // Relation produitResult = r1.produitCartesien(r2);
        // produitResult.afficherValeurs();

        // } catch (Exception e) {
        // System.out.print("New Exception: ");
        // System.out.println(e.getMessage());
        // }

        ///////////////////////////////////////// TEST FAHAEFATRA SELECTION ET SELECTION
    //     ///////////////////////////////////////// MULTIPLE

        // try {
        //     Attribut[] attributs = {
        //             new Attribut("nom", new Ensemble(new Object[] { "Koto", "Rana", "Ali","Sara"})),
        //             new Attribut("age", new Ensemble(new Object[] { 13,20,22,25,30  })),
        //             new Attribut("age2", new Ensemble(new Object[] { 13,20,22,25,30  })),
        //             new Attribut("etat", new Ensemble(new Object[] { true, false }))
        //     };

        //     // Créer une relation et insérer des données
        //     Relation r1 = new Relation("Personnes", attributs);
        //     r1.insererDonnees(new Object[] { "Koto", 20, 13, true });
        //     r1.insererDonnees(new Object[] { "Rana", 13, 13,false });
        //     r1.insererDonnees(new Object[] { "Koto", 13, 20,true });
        //     r1.insererDonnees(new Object[] { "Ali", 30, 30,false });
        //     r1.insererDonnees(new Object[] { "Sara", 25, 20,true });

        //     System.out.println(" Voici la relation r1 :");
        //     r1.afficherValeurs();
        //     System.out.println(" ");

        //     // Sélection simple
        //     Relation selectionResultat = r1.selection("age < age2");
        //     System.out.println("Résultat de la sélection simple :");
        //     selectionResultat.afficherValeurs();
        //     System.out.println();

        //     //Sélection multiple avec condition complexe
        //     Relation selectionMultipleResultat = r1.selectionMultiple("((nom = 'Koto' ) and age = age2 ) and age <= 22 ");
        //     System.out.println("Résultat de la sélection multiple :");
        //     selectionMultipleResultat.afficherValeurs();
        // } catch (Exception e) {
        //     System.out.print("New Exception: ");
        //     System.out.println(e.getMessage());
        //     e.printStackTrace();
        // }

     }
}
