import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Etudiant1 {
	private String matricule;
	private String nom;
	private String prenom;  
	private String sexe;  
	private int age; 
	private double moyenne;

    public Etudiant1(String matricule, String nom, String prenom, String sexe, int age, double moyenne) {
        this.matricule = matricule;
        this.nom = nom;
        this.prenom = prenom;
	this.sexe = sexe;
        this.age = age;
        this.moyenne = moyenne;
    }
  
    // Création des getters
    public String getMatricule() { 
        return matricule; 
    }
    public String getNom() { 
        return nom; 
    }
    public String getPrenom() { 
        return prenom;
    }
    public int getAge() { 
        return age; 
    }
    public String getSexe() { 
        return sexe; 
    }
    public double getMoyenne() { 
        return moyenne; 
    }


    public static void main(String[] args) throws Exception {

        FileWriter fw = new FileWriter("exo1.txt");

        fw.write("E001 ALI Aya F 18 19,6\n");
        fw.write("E002 DARL Manioc M 34 14,0\n");
        fw.write("E003 DOIT Mensah M 26 15,5\n");

        fw.close();

        Scanner sc = new Scanner(System.in);
        System.out.println("Entrez le matricule : ");
        String recherche = sc.nextLine();

        File fichier = new File("exo1.txt");
        Scanner lecture = new Scanner(fichier);


        while (lecture.hasNext()) {

            String matricule = lecture.next();
            String nom = lecture.next();
            String prenom = lecture.next();
	    String sexe = lecture.next();
            int age = lecture.nextInt();
            double moyenne = lecture.nextDouble();

            Etudiant1 e = new Etudiant1(matricule, nom, prenom,sexe,age, moyenne);

            if (e.getMatricule().equals(recherche)) {

                System.out.println("Matricule : " + e.getMatricule());
                System.out.println("Nom : " + e.getNom());
                System.out.println("Prénom : " + e.getPrenom());
		System.out.println("Sexe : " + e.getSexe());
                System.out.println("Age : " + e.getAge());
                System.out.println("Moyenne : " + e.getMoyenne());

                return;
            }  
        }
	System.out.println("Aucun étudiant trouvé.");     
    }
}
