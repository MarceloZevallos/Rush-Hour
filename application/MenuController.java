package application;

import rush_hour.jeu.*;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import rush_hour.jeu.JeuController;

/**
 * La classe {@code MenuController} représente le contrôleur du menu principal du jeu Rush Hour.
 * Elle étend la classe JavaFX {@link javafx.stage.Stage}, fournissant des méthodes pour gérer les interactions
 * utilisateur et naviguer vers différents niveaux de jeu.
 *
 * <p>Les principales fonctionnalités incluent la sélection du niveau de difficulté et la sortie du jeu.</p>
 *
 * <p>Cette classe utilise le format FXML de JavaFX pour définir l'interface utilisateur et la gestion des événements.</p>
 *
 * @author Marcelo Zevallos
 */

public class MenuController extends Stage {
	private String path;

	@FXML
	Rectangle rectFacile;
	@FXML
	Rectangle rectMoyen;
	@FXML
	Rectangle rectDifficile;

	@FXML
	protected void Facile(MouseEvent event) throws IOException {
		try {
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
				rectFacile.setOpacity(0.5);
			} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
				rectFacile.setOpacity(0);
			}

			if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
				// Charger la vue du jeu
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/rush_hour/jeu/Jeu.fxml"));
				Parent root = loader.load();

				// Obtenez le contrôleur du jeu depuis le FXMLLoader
				JeuController jeuController = loader.getController();

				// Transférez la chaîne au contrôleur du jeu
				jeuController.setNiveau("facile");

				// Créer une nouvelle scène avec les dimensions de l'écran principal
				Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(),
						Screen.getPrimary().getVisualBounds().getHeight());

				// Obtenir la référence à la scène actuelle
				Scene currentScene = ((Node) event.getSource()).getScene();

				// Obtenir la référence à la fenêtre actuelle
				Stage currentStage = (Stage) currentScene.getWindow();

				// Fermer la scène actuelle
				currentStage.close();

				// Créer une nouvelle fenêtre
				Stage nouvelleFenetre = new Stage();
				nouvelleFenetre.setTitle("Rush Hour!");

				// Mettre la fenêtre en mode plein écran
				nouvelleFenetre.setFullScreen(true);

				// Définir la nouvelle scène pour la nouvelle fenêtre
				nouvelleFenetre.setScene(scene);

				// Afficher la nouvelle fenêtre
				nouvelleFenetre.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void Moyen(MouseEvent event) throws IOException {
		try {
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
				rectMoyen.setOpacity(0.5);
			} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
				rectMoyen.setOpacity(0);
			}
			if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
				// Charger la vue du jeu
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/rush_hour/jeu/Jeu.fxml"));
				Parent root = loader.load();

				// Obtenez le contrôleur du jeu depuis le FXMLLoader
				JeuController jeuController = loader.getController();

				// Transférez la chaîne au contrôleur du jeu
				jeuController.setNiveau("moyen");

				// Créer une nouvelle scène avec les dimensions de l'écran principal
				Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(),
						Screen.getPrimary().getVisualBounds().getHeight());

				// Obtenir la référence à la scène actuelle
				Scene currentScene = ((Node) event.getSource()).getScene();

				// Obtenir la référence à la fenêtre actuelle
				Stage currentStage = (Stage) currentScene.getWindow();

				// Fermer la scène actuelle
				currentStage.close();

				// Créer une nouvelle fenêtre
				Stage nouvelleFenetre = new Stage();
				nouvelleFenetre.setTitle("Rush Hour!");

				// Mettre la fenêtre en mode plein écran
				nouvelleFenetre.setFullScreen(true);

				// Définir la nouvelle scène pour la nouvelle fenêtre
				nouvelleFenetre.setScene(scene);

				// Afficher la nouvelle fenêtre
				nouvelleFenetre.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void Difficile(MouseEvent event) throws IOException {
		try {
			if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
				rectDifficile.setOpacity(0.5);
			} else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
				rectDifficile.setOpacity(0);
			}
			if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
				// Charger la vue du jeu
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/rush_hour/jeu/Jeu.fxml"));
				Parent root = loader.load();

				// Obtenez le contrôleur du jeu depuis le FXMLLoader
				JeuController jeuController = loader.getController();

				// Transférez la chaîne au contrôleur du jeu
				jeuController.setNiveau("difficile");

				// Créer une nouvelle scène avec les dimensions de l'écran principal
				Scene scene = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(),
						Screen.getPrimary().getVisualBounds().getHeight());

				// Obtenir la référence à la scène actuelle
				Scene currentScene = ((Node) event.getSource()).getScene();

				// Obtenir la référence à la fenêtre actuelle
				Stage currentStage = (Stage) currentScene.getWindow();

				// Fermer la scène actuelle
				currentStage.close();

				// Créer une nouvelle fenêtre
				Stage nouvelleFenetre = new Stage();
				nouvelleFenetre.setTitle("Rush Hour!");

				// Mettre la fenêtre en mode plein écran
				nouvelleFenetre.setFullScreen(true);

				// Définir la nouvelle scène pour la nouvelle fenêtre
				nouvelleFenetre.setScene(scene);

				// Afficher la nouvelle fenêtre
				nouvelleFenetre.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}
	
	@FXML
	protected void quitter(MouseEvent event) throws Exception {
		try {
			System.exit(0);
		}catch(Exception e) {
			
		}
	}

}
