import java.util.Scanner;

public class Convertisseur {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        int choix;

        System.out.println("=== CONVERTISSEUR ===");

        System.out.println("1. FCFA -> Euro");
        System.out.println("2. Euro -> FCFA");

        System.out.print("Choisis une option (1 ou 2) : ");
        choix = scanner.nextInt();

        if (choix == 1) {

            System.out.print("Entre le montant en FCFA : ");
            double fcfa = scanner.nextDouble();

            double euro = fcfa / 655.957;

            System.out.println("Resultat : " + euro + " Euro");

        } else if (choix == 2) {

            System.out.print("Entre le montant en Euro : ");
            double euro = scanner.nextDouble();

            double fcfa = euro * 655.957;

            System.out.println("Resultat : " + fcfa + " FCFA");

        } else {

            System.out.println("Choix invalide !");
        }

        scanner.close();
    }
}