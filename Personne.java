public class Personne {
	private String nom;
	private String prenom;  
	private String sexe;  
	private int age;  

	Personne(){
		this.nom = "AÏNADOU";
		this.prenom = "Mondoukpè";
		this.sexe = "FEMININ";
		this.age = 19;
	}

	public Personne(String leNom, String lePrenom, String leSexe, int lAge){
		this.nom = "AÏNADOU";
		this.prenom = "Mondoukpè";
		this.sexe = "F";
		this.age = 19;
	}

	// Création des getters
	public String getNom(){
		return nom;
	}

	public String getPrenom(){
		return prenom;
	}

	public String getSexe(){
		return sexe;
	}
	
	public int getAge(){
		return age;
	}

	// Création des setters
	public void setNom(String nom){
		this.nom = nom;
	}

	public void setPrenom(String prenom){
		this.prenom = prenom;
	}

	public void setSexe(String sexe){
		this.sexe = sexe;
	}

	public void setAge(int age){
		this.age = age;
	}

	public static void main(String[] args){
		Personne pers = new Personne();
		System.out.println("Nom : "+pers.getNom()+" | Prénom : "+pers.getPrenom()+" | Sexe : "+pers.getSexe()+" | Âge : "+pers.getAge());
	}

}