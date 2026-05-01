public class Personnes {
	protected String nom;
	protected String prenom;  
	protected String sexe;  
	protected int age;  

	/*Personnes(){
		this.nom = "AÏNADOU";
		this.prenom = "Mondoukpè";
		this.sexe = "F";
		this.age = 19;
	}*/

	public Personnes(String leNom, String lePrenom, String leSexe, int lAge){
		this.nom = leNom;
		this.prenom = lePrenom;
		this.sexe =leSexe;
		this.age =lAge;
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



	static class Etudiant extends Personnes{
		private  String matricule;
		private String option;
		private String niveau;
		private Double moyenne;

	

	public Etudiant(String leNom, String lePrenom ,String leSexe, int lAge, String leMatricule, String lOption, String leNiveau, Double leMoyenne){
	super(leNom,lePrenom,leSexe,lAge);
	this.matricule = leMatricule;
	this.option = lOption;
	this.niveau = leNiveau;
	this.moyenne = leMoyenne;
	}
	public String getMatricule(){
		return matricule;
	}
	public String getOption(){
		return option;
	}
	public String getNiveau(){
		return niveau;
	}
	public Double getMoyenne(){
		return moyenne;
	}
	
	public void setMatricule(String matricule){
		this.matricule = matricule;
	}

	public void setOption(String option){
		this.option = option;
	}

	public void setNiveau(String niveau){
		this.niveau = niveau;
	}

	public void setMoyenne(Double moyenne){
		this.moyenne = moyenne;
	}


	
	}

	public static void main(String[] args){
		/*Personnes pers = new Personnes("AÏNADOU", "Mondoukpè", "F", 19);
		System.out.println("Nom : "+pers.getNom()+" | Prénom : "+pers.getPrenom()+" | Sexe : "+pers.getSexe()+" | Âge : "+pers.getAge());*/
	
		Etudiant E = new Etudiant("AÏNADOU", "Mondoukpè", "F", 19,"E001", "SIL", "DeuxièmeAnnée", 16.0);
		System.out.println("Nom : "+E.getNom()+" | Prénom : "+E.getPrenom()+" | Sexe : "+E.getSexe()+" | Âge : "+E.getAge()+" | Matricule = "+E.getMatricule()+" | Option = "+E.getOption()+" | Niveau = "+E.getNiveau()+" | Moyenne = "+E.getMoyenne());

		}
}