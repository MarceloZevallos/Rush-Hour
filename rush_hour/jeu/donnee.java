package rush_hour.jeu;


public class donnee {

	private String couleur;
	private int longeur;
	private int colonne;
	private int ligne;
	private char direction;
	
	public donnee(String couleur, int longeur, int colonne, int ligne, char direction) {
		this.colonne = colonne;
		this.couleur = couleur;
		this.ligne = ligne;
		this.direction = direction;
		this.longeur = longeur;
	}
	
	
	
	public String getCouleur() {
		return couleur;
	}

	public void setCouleur(String couleur) {
		this.couleur = couleur;
	}

	public int getLongeur() {
		return longeur;
	}

	public void setLongeur(int longeur) {
		this.longeur = longeur;
	}

	public int getColonne() {
		return colonne;
	}

	public void setColonne(int colonne) {
		this.colonne = colonne;
	}

	public int getLigne() {
		return ligne;
	}

	public void setLigne(int ligne) {
		this.ligne = ligne;
	}

	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}

}
