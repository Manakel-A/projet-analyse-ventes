import java.util.Scanner;

public class Personnee {
    private String nom;

    // Getters and Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public static void main(String[] args) {
        Personnee pers = new Personnee();
        System.out.println("Entrez votre nom SVP");
        Scanner sc = new Scanner(System.in);

        // String nom = sc.next();
        // pers.setNom(nom);

        pers.setNom(sc.next());
        System.out.println("Bonjour\t" + pers.getNom());

        
    }
}
