package rush_hour.jeu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import application.EnumDirection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * La classe {@code lireDonnee} est responsable de la lecture des données à partir d'un fichier spécifié
 * et de la création d'une liste de données correspondantes, ainsi qu'une liste d'images associées.
 * Les données lues comprennent la couleur, la longueur, la colonne, la ligne et la direction des éléments.
 * Les images sont chargées à partir des fichiers correspondants dans le répertoire "source".
 *
 * <p>Cette classe utilise un fichier texte spécifié pour lire les données et générer les éléments correspondants.</p>
 *
 */

public class lireDonnee {

	private String couleur;
	private int longeur;
	private int colonne;
	private int ligne;
	private char direction;
	private List<donnee> arrDonnee;
	private List<ImageView> arrImage;
	private String nomImage;
	int n = 0;
	
	/**
     * Constructeur de la classe {@code lireDonnee}.
     *
     * @param strNomFichier Le nom du fichier à partir duquel les données doivent être lues.
     */

	lireDonnee(String strNomFichier) {
		BufferedReader brFichier = null;
		boolean continuer = true;
		this.arrDonnee = new ArrayList<>();
		this.arrImage = new ArrayList<>();
		try {
			brFichier = new BufferedReader(new FileReader(strNomFichier));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			while (continuer) {
				try {
					String[] sizes = brFichier.readLine().trim().split(",");
					couleur = sizes[0].trim();
					longeur = Integer.parseInt(sizes[1].trim());
					colonne = Integer.parseInt(sizes[2].trim());
					ligne = Integer.parseInt(sizes[3].trim());
					direction = sizes[4].charAt(0);

					arrDonnee.add(new donnee(couleur, longeur, colonne, ligne, direction));
					//System.out.println(arrDonnee.size());
					EnumDirection enumDirection = EnumDirection.getEnum(Character.toString(direction));
					if (longeur == 2) {
						this.nomImage = ("auto_" + enumDirection.getDescription() + "_" + couleur + ".png");
					} else if (longeur == 3) {
						this.nomImage = ("camion_" + enumDirection.getDescription() + "_" + couleur + ".png");
					}

					String imagePath = System.getProperty("user.dir") + File.separator + "source" + File.separator + nomImage;
					Image image = new Image("file:///" + imagePath);
					ImageView imageView = new ImageView(image);
					
					arrImage.add(imageView);
					// n++;

				} catch (Exception e) {
					continuer = false;
					//System.out.println("Mon code marche pas :(");
				}

			}
			for (int i = 0; i < arrImage.size(); i++) {
				//System.out.println(arrImage.get(i));
			}

		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}

		for (int i = 0; i < arrDonnee.size(); i++) {
			//System.out.println(arrDonnee.get(i).getCouleur() + " " + arrDonnee.get(i).getLongeur() + " "+ arrDonnee.get(i).getColonne() + " " + arrDonnee.get(i).getLigne());
		}
	}
	
	/**
     * Obtient la liste des données lues à partir du fichier.
     *
     * @return La liste des données.
     */

	public List<donnee> getArrDonnee() {
		return arrDonnee;
	}

	/**
     * Définit la liste des données lues à partir du fichier.
     *
     * @param arrDonnee La nouvelle liste des données.
     */
	
	public void setArrDonnee(ArrayList<donnee> arrDonnee) {
		this.arrDonnee = arrDonnee;
	}
	
	/**
     * Obtient la liste des images associées aux données lues.
     *
     * @return La liste des images.
     */

	public List<ImageView> getArrImage() {
		return arrImage;
	}
	
	

}
