import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Etudiants{

    public static void main(String[] args) throws Exception {

        //Ecrire les 3 étudiants dans le fichier
        FileWriter fw = new FileWriter("mardi.txt");

        fw.write("E001 Aya Ayana F 16 16,5\n");
        fw.write("E002 Damso Mano M 15 14,0\n");
        fw.write("E003 Mercredi Mana F 19 18,9\n");

        fw.close();

        //Demander le matricule
        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez le matricule : ");
        String recherche = sc.nextLine();  

        //Lire le fichier
        File fichier = new File("mardi.txt");
        Scanner lecture = new Scanner(fichier);

        while (lecture.hasNext()) {

            String matricule = lecture.next();      
            String nom = lecture.next();
	    String prenom = lecture.next();
	    String sexe = lecture.next(); 
	    int age= lecture.nextInt(); 
            double moyenne = lecture.nextDouble();  

            if (matricule.equals(recherche)) {

		System.out.println("Matricule : " + matricule);
                System.out.println("Nom : " + nom);
		System.out.println("Prénom : " + prenom);
                System.out.println("Sexe : " + sexe);
                System.out.println("Age : " + age);
                System.out.println("Moyenne : " + moyenne);
		return;
	    }
        }
	System.out.println("Aucun étudiant trouvé.");
    }
}
