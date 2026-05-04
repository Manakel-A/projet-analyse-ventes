import java.util.Scanner;
import java.util.Random;

public class DevinerNombre {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int secret = random.nextInt(10) + 1;
        int essai;

        do {
            System.out.print("Devine le nombre (1-10) : ");
            essai = scanner.nextInt();

            if (essai < secret) {
                System.out.println("Plus grand !");
            } else if (essai > secret) {
                System.out.println("Plus petit !");
            }

        } while (essai != secret);

        System.out.println("Bravo ! Trouvé 🎉");
    }
}