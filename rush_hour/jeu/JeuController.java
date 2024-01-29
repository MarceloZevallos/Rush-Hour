package rush_hour.jeu;

import application.*;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Light.Point;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Contrôleur pour le jeu, gérant la logique de l'interface utilisateur, les
 * interactions et le déroulement du jeu.
 */

public class JeuController implements Runnable, javafx.fxml.Initializable {

	@FXML
	private StackPane root;
	@FXML
	private Label label;
	@FXML
	private Pane jeu = new Pane();
	@FXML
	private Label coups;

	public String path = "null";
	private int nbrCoups = 0;
	final Duration DURATION = Duration.millis(500);
	private Point[] lstPoints = new Point[36];
	private Circle[] grille = new Circle[36];

	private Group Border_Right;
	private Group Border_Left;
	private Group Border_Top;
	private Group Border_Bottom;
	private Rectangle checkWin = new Rectangle(50, 50, Color.GREEN);
	private ArrayList<Group> vehicules = new ArrayList<>();
	private lireDonnee lD;

	private int seconds = 0;
	private boolean collisionDetected = false;
	private boolean isDragged = false;
	private volatile boolean isRunning = true;
	private double colliderPos = 0;

	private ImageView player = new ImageView("file:///"+
			new String(System.getProperty("user.dir") + File.separator + "source" + File.separator +"auto_H_rouge.png"));
	private int playerNum;
	private Scene scene;

	/**
	 * Initialize l'état initial du jeu, charge les données et configure les
	 * éléments de l'interface.
	 * 
	 * @param arg0 L'URL utilisée pour résoudre les chemins relatifs pour l'objet
	 *             root, ou null si l'objet root n'a pas été chargé à partir de
	 *             FXML.
	 * @param arg1 Les ressources utilisées pour localiser l'objet root, ou null si
	 *             l'objet root n'a pas été localisé.
	 */

	public void initialize(URL arg0, ResourceBundle arg1) {

		Platform.runLater(() -> {
			// System.out.println("Niveau recu: " + path);
			lD = new lireDonnee(path + ".txt");
			scene = jeu.getScene();
			initialiserTableau(lD);
			ajouterVoiture(lD);

			for (int i = 0; i < vehicules.size(); i++) {
				if (lD.getArrDonnee().get(i).getDirection() == 'V') {
					if (lD.getArrDonnee().get(i).getLongeur() == 2) {
						moveOnMouseDragY(scene, vehicules.get(i), 2);
					} else if (lD.getArrDonnee().get(i).getLongeur() == 3) {
						moveOnMouseDragY(scene, vehicules.get(i), 3);
					}

				} else if (lD.getArrDonnee().get(i).getDirection() == 'H') {
					if (lD.getArrDonnee().get(i).getLongeur() == 2) {
						moveOnMouseDragX(scene, vehicules.get(i), 2);
					} else if (lD.getArrDonnee().get(i).getLongeur() == 3) {
						moveOnMouseDragX(scene, vehicules.get(i), 3);
					}

				}
			}
		});

		Rectangle BorderBody1 = new Rectangle(145, 500, Color.RED);
		Rectangle BorderBody2 = new Rectangle(145, 500, Color.RED);
		Rectangle BorderBody3 = new Rectangle(500, 145, Color.RED);
		Rectangle BorderBody4 = new Rectangle(500, 145, Color.RED);

		Border_Right = new Group(BorderBody1);
		Border_Left = new Group(BorderBody2);
		Border_Top = new Group(BorderBody3);
		Border_Bottom = new Group(BorderBody4);

		Border_Right.setLayoutX(530);
		Border_Left.setLayoutX(-180);
		Border_Top.setLayoutY(-160);
		Border_Bottom.setLayoutY(440);

		checkWin.setLayoutX(410);
		checkWin.setLayoutY(150);

		checkWin.setVisible(false);
		Border_Right.setVisible(false);
		Border_Left.setVisible(false);
		Border_Top.setVisible(false);
		Border_Bottom.setVisible(false);

		jeu.getChildren().addAll(Border_Right, Border_Left, Border_Top, Border_Bottom, checkWin);

		new Thread(this).start();
	}

	/**
	 * Initialise le tableau de jeu en configurant les points de la grille.
	 * 
	 * @param lD L'objet lireDonnee contenant les données du jeu.
	 */

	public void initialiserTableau(lireDonnee lD) {
		int n = 0;
		int m = 0;
		for (int i = 0; i < lstPoints.length; i++) {
			if (i % 6 == 0) {
				m = m + 72;
				n = 0;
			}
			lstPoints[i] = new Point();
			lstPoints[i].setX(n + 60);
			lstPoints[i].setY(m - 40);
			// System.out.println(lstPoints[i].getX());
			n = n + 72;
		}
		try {
			for (int i = 0; i < lstPoints.length; i++) {
				Circle c = new Circle(lstPoints[i].getX(), lstPoints[i].getY(), 10, Color.GREEN);
				c.setVisible(false);
				grille[i] = c;
				jeu.getChildren().add(grille[i]);
				//System.out.println(grille[i].getCenterX());
			}
		} catch (Exception e) {

		}

	}

	/**
	 * Ajoute des voitures au jeu en fonction des données fournies.
	 * 
	 * @param lD L'objet lireDonnee contenant les données et images des voitures.
	 */

	protected void ajouterVoiture(lireDonnee lD) {

		int len = lD.getArrImage().size();

		// System.out.println(lD.getArrImage().size());
		for (int i = 0; i < len; i++) {
			donnee voitureD = lD.getArrDonnee().get(i);
			if (voitureD.getDirection() == 'H') {
				ImageView img = lD.getArrImage().get(i);
				if (voitureD.getLongeur() == 2) {
					img.setFitHeight(70);
					img.setFitWidth(140);
					vehicules.add(new Group(img));
					vehicules.get(i).setTranslateX(lD.getArrDonnee().get(i).getColonne() * 72);
					vehicules.get(i).setTranslateY(lD.getArrDonnee().get(i).getLigne() * 72);
					double playerCenterX = vehicules.get(i).getTranslateX()
							+ vehicules.get(i).getBoundsInLocal().getWidth() / 2;
					double playerCenterY = vehicules.get(i).getTranslateY()
							+ vehicules.get(i).getBoundsInLocal().getHeight() / 2;
					double nearestX = 0;
					double minDistance = Double.MAX_VALUE;
					for (int j = 0; j < grille.length - 1; j++) {
						double distance = Math.abs(playerCenterX - grille[j].getCenterX());

						if (distance < minDistance) {
							minDistance = distance;
							nearestX = grille[j].getCenterX();
						}
					}
					int nearestCircleIndex = findNearestCircleIndex(playerCenterX, playerCenterY, grille);
					vehicules.get(i).setTranslateY(lstPoints[nearestCircleIndex].getY()
							- vehicules.get(i).getBoundsInLocal().getHeight() / 2 - 2);
					vehicules.get(i)
							.setTranslateX(nearestX - vehicules.get(i).getBoundsInLocal().getWidth() / (3) + 16);
					jeu.getChildren().addAll(vehicules.get(i));
					if (isPlayer(img)) {
						playerNum = i;
					}

				} else if (voitureD.getLongeur() == 3) {
					img.setFitWidth(200);
					img.setFitHeight(70);
					vehicules.add(new Group(img));
					vehicules.get(i).setTranslateX(lD.getArrDonnee().get(i).getColonne() * 72);
					vehicules.get(i).setTranslateY(lD.getArrDonnee().get(i).getLigne() * 72);
					double playerCenterX = vehicules.get(i).getTranslateX()
							+ vehicules.get(i).getBoundsInLocal().getWidth() / 2;
					double playerCenterY = vehicules.get(i).getTranslateY()
							+ vehicules.get(i).getBoundsInLocal().getHeight() / 2;
					double nearestX = 0;
					double minDistance = Double.MAX_VALUE;
					for (int j = 0; j < grille.length - 1; j++) {
						double distance = Math.abs(playerCenterX - grille[j].getCenterX());

						if (distance < minDistance) {
							minDistance = distance;
							nearestX = grille[j].getCenterX();
						}
					}
					int nearestCircleIndex = findNearestCircleIndex(playerCenterX, playerCenterY, grille);
					vehicules.get(i).setTranslateY(lstPoints[nearestCircleIndex].getY()
							- vehicules.get(i).getBoundsInLocal().getHeight() / 2 - 2);
					vehicules.get(i).setTranslateX(nearestX - vehicules.get(i).getBoundsInLocal().getWidth() / 3 - 30);
					jeu.getChildren().addAll(vehicules.get(i));
				}

			} else if (voitureD.getDirection() == 'V') {
				ImageView img = lD.getArrImage().get(i);
				if (voitureD.getLongeur() == 2) {
					img.setFitWidth(70);
					img.setFitHeight(140);
					vehicules.add(new Group(img));
					vehicules.get(i).setTranslateX(lD.getArrDonnee().get(i).getColonne() * 72);
					vehicules.get(i).setTranslateY(lD.getArrDonnee().get(i).getLigne() * 72);
					double playerCenterX = vehicules.get(i).getTranslateX()
							+ vehicules.get(i).getBoundsInLocal().getWidth() / 2;
					double playerCenterY = vehicules.get(i).getTranslateY()
							+ vehicules.get(i).getBoundsInLocal().getHeight() / 2;

					double nearestX = 0;
					double minDistance = Double.MAX_VALUE;

					for (int j = 0; j < grille.length - 1; j++) {
						double distance = Math.abs(playerCenterX - grille[j].getCenterX());

						if (distance < minDistance) {
							minDistance = distance;
							nearestX = grille[j].getCenterX();
						}
					}
					int nearestCircleIndex = findNearestCircleIndex(playerCenterX, playerCenterY, grille);
					vehicules.get(i).setTranslateY(lstPoints[nearestCircleIndex].getY()
							- vehicules.get(i).getBoundsInLocal().getHeight() / 2 - 36);// 36
					vehicules.get(i).setTranslateX(nearestX - vehicules.get(i).getBoundsInLocal().getWidth() / 3 - 10);
					jeu.getChildren().addAll(vehicules.get(i));

				} else if (voitureD.getLongeur() == 3) {
					img.setFitWidth(70);
					img.setFitHeight(210);
					vehicules.add(new Group(img));
					vehicules.get(i).setTranslateX(lD.getArrDonnee().get(i).getColonne() * 72);
					vehicules.get(i).setTranslateY(lD.getArrDonnee().get(i).getLigne() * 72);
					double playerCenterX = vehicules.get(i).getTranslateX()
							+ vehicules.get(i).getBoundsInLocal().getWidth() / 2;
					double playerCenterY = vehicules.get(i).getTranslateY()
							+ vehicules.get(i).getBoundsInLocal().getHeight() / 2;

					double nearestX = 0;
					double minDistance = Double.MAX_VALUE;

					for (int j = 0; j < grille.length - 1; j++) {
						double distance = Math.abs(playerCenterX - grille[j].getCenterX());

						if (distance < minDistance) {
							minDistance = distance;
							nearestX = grille[j].getCenterX();
						}
					}

					int nearestCircleIndex = findNearestCircleIndex(playerCenterX, playerCenterY, grille);
					vehicules.get(i).setTranslateY(lstPoints[nearestCircleIndex].getY()
							- vehicules.get(i).getBoundsInLocal().getHeight() / 2 - 0);
					vehicules.get(i).setTranslateX(nearestX - vehicules.get(i).getBoundsInLocal().getWidth() / 3 - 6);
					jeu.getChildren().addAll(vehicules.get(i));
				}

			}

		}
	}

	/**
	 * Vérifie si l'image donnée correspond à celle du joueur.
	 * 
	 * @param imageView L'ImageView de la voiture à vérifier.
	 * @return true si l'image correspond à celle du joueur, false sinon.
	 */

	private boolean isPlayer(ImageView imageView) {
		// Get image URLs as strings
		String url1 = player.getImage().getUrl();
		String url2 = imageView.getImage().getUrl();

		// Compare URLs
		return url1.equals(url2);
	}

	/**
	 * Gère le mouvement horizontal des véhicules lorsqu'ils sont glissés avec la
	 * souris.
	 * 
	 * @param scene  La scène dans laquelle le mouvement se produit.
	 * @param player Le groupe de véhicules à déplacer.
	 * @param type   Le type de véhicule (longueur).
	 */

	private void moveOnMouseDragX(Scene scene, Group player, int type) {
		// Variables to store initial click position
		double[] dragAnchorX = { 0 };
		double[] initialX = { 0 };

		player.setOnMouseEntered((MouseEvent event) -> {
			if (scene != null) {
				scene.setCursor(Cursor.E_RESIZE); // Replace with the appropriate cursor type
			}

		});

		player.setOnMousePressed((MouseEvent event) -> {
			dragAnchorX[0] = event.getSceneX() - player.getTranslateX();
			initialX[0] = player.getTranslateX(); // Store the initial X position
			collisionDetected = false;
			isDragged = false;
		});

		player.setOnMouseDragged((MouseEvent event) -> {
			double newX = event.getSceneX() - dragAnchorX[0];
			isDragged = true;

			if (!collisionDetected) {
				if (checkForCollisions(player, player.getTranslateX(), 'X')) {
					// Set the collision flag to true
					collisionDetected = true;
					System.out.println("Game Over");
					isDragged = false;
					player.setTranslateX(initialX[0]);
					return;
				} else if (!(checkForCollisions(player, player.getTranslateX(), 'X'))) {
					player.setTranslateX(newX);
				}
			}
		});

		player.setOnMouseReleased((MouseEvent event) -> {
			double playerCenterX = player.getTranslateX() + player.getBoundsInLocal().getWidth() / 2;
			double minDistance = Double.MAX_VALUE;
			double nearestX = 0;
			Bounds playerBounds = vehicules.get(playerNum).getBoundsInParent();
			Bounds checkWinBounds = checkWin.getBoundsInParent();

			Platform.runLater(() -> {
				if (scene != null) {
					scene.setCursor(Cursor.DEFAULT); // Replace with the appropriate cursor type
				}

			});

			if (playerBounds.intersects(checkWinBounds)) {
				System.out.println("Game Over - The red car has a clear path to the exit!");
				triumph();
			}

			if (!isDragged) {
				return;
			} else if (isDragged) {
				nbrCoups++;
				coups.setText(Integer.toString(nbrCoups));
				for (int i = 0; i < grille.length - 1; i++) {
					double distance = Math.abs(playerCenterX - grille[i].getCenterX());

					if (distance < minDistance) {
						minDistance = distance;
						nearestX = grille[i].getCenterX();
					}
				}

				if (!collisionDetected) {
					if (type == 2) {
						player.setTranslateX(nearestX - player.getBoundsInLocal().getWidth() / 6 - 7);

					} else if (type == 3) {
						player.setTranslateX(nearestX - player.getBoundsInLocal().getWidth() / 2);
					}
				} else {
					// Reset the X position to its initial value
					player.setTranslateX(initialX[0]);
				}
			}
		});

	}

	/**
	 * Gère le mouvement vertical des véhicules lorsqu'ils sont glissés avec la
	 * souris.
	 * 
	 * @param scene  La scène dans laquelle le mouvement se produit.
	 * @param player Le groupe de véhicules à déplacer.
	 * @param type   Le type de véhicule (longueur).
	 */

	private void moveOnMouseDragY(Scene scene, Group player, int type) {
		// Variables to store initial click position
		double[] dragAnchorY = { 0 };
		double[] initialY = { 0 };
		
		player.setOnMouseEntered((MouseEvent event)->{
			if (scene != null) {
			    scene.setCursor(Cursor.S_RESIZE); // Replace with the appropriate cursor type
			}
		
	});

		player.setOnMousePressed((MouseEvent event) -> {
			dragAnchorY[0] = event.getSceneY() - player.getTranslateY();
			initialY[0] = player.getTranslateY(); // Store the initial X position
			collisionDetected = false;
			// isDragged = false;
		});

		player.setOnMouseDragged((MouseEvent event) -> {
			double newY = event.getSceneY() - dragAnchorY[0];
			isDragged = true;
			//scene.setCursor(Cursor.V_RESIZE);


			// Ensure the new Y position is within bounds
			if (!collisionDetected) {

				if (checkForCollisions(player, player.getTranslateY(), 'Y')) {
					// Set the collision flag to true
					collisionDetected = true;
					isDragged = false;
					System.out.println("Game Over");
					stopCloseToGroup(player, initialY[0], 'Y');
					return;
				} else if (!(checkForCollisions(player, player.getTranslateY(), 'Y'))) {
					player.setTranslateY(newY);
				}

			}
		});

		player.setOnMouseReleased((MouseEvent event) -> {
			double playerCenterY = player.getTranslateY() + player.getBoundsInLocal().getHeight() / 2;
			double minDistance = Double.MAX_VALUE;
			double nearestY = 0;
			
			Platform.runLater(()->{
				if (scene != null) {
				    scene.setCursor(Cursor.DEFAULT); // Replace with the appropriate cursor type
				}

			});

			if (!isDragged) {
				return;
			} else if (isDragged) {
				nbrCoups++;
				coups.setText(Integer.toString(nbrCoups));

				for (int i = 0; i < grille.length - 1; i++) {
					double distance = Math.abs(playerCenterY - grille[i].getCenterY());
					// System.out.println("i: " + i + ", distance: " + distance);
					if (distance < minDistance) {
						minDistance = distance;
						nearestY = grille[i].getCenterY();
					}
				}
				if (!collisionDetected) {
					if (type == 2) {
						player.setTranslateY(nearestY - player.getBoundsInLocal().getHeight() / 6 - 10);
					} else if (type == 3) {
						player.setTranslateY(nearestY - player.getBoundsInLocal().getHeight() / 2);
					}

				} else {
					//player.setTranslateY(initialY[0]);
					stopCloseToGroup(player, initialY[0], 'Y');
				}

			}
		});
	}
	
	private void stopCloseToGroup(Group player, double newAxis, char axis) {
	    double playerCenterY = newAxis + player.getBoundsInLocal().getHeight() / 2;
	    double minDistance = Double.MAX_VALUE;
	    double nearestY = 0;

	    for (int i = 0; i < grille.length - 1; i++) {
	        double distance = Math.abs(playerCenterY - grille[i].getCenterY());
	        if (distance < minDistance) {
	            minDistance = distance;
	            nearestY = grille[i].getCenterY();
	        }
	    }

	    if (axis == 'Y') {
	        player.setTranslateY(nearestY - player.getBoundsInLocal().getHeight() / 6 - 10);
	    } else {
	        // Handle other axes if needed
	    }
	}

	/**
	 * Vérifie s'il y a des collisions entre le joueur et d'autres objets dans le
	 * jeu.
	 * 
	 * @param player      Le groupe représentant le joueur.
	 * @param position    La position actuelle du joueur.
	 * @param orientation L'orientation du mouvement ('X' pour horizontal, 'Y' pour
	 *                    vertical).
	 * @return true si une collision est détectée, false sinon.
	 */

	private boolean checkForCollisions(Group player, double position, char orientation) {
		for (Node playerShape : player.getChildren()) {
			Bounds playerBounds = playerShape.localToScene(playerShape.getBoundsInLocal());

			for (Node staticBlock : jeu.getChildren()) {
				if (staticBlock instanceof Group && staticBlock != player) {
					Group staticGroup = (Group) staticBlock;
					for (Node staticShape : staticGroup.getChildren()) {
						Bounds staticBounds = staticShape.localToScene(staticShape.getBoundsInLocal());
						if (playerBounds.intersects(staticBounds) || staticBounds.intersects(playerBounds)) {
							System.out.println("staticBounds: "+staticBounds.getCenterY());
							System.out.println("playerBounds: "+playerBounds.getCenterY());
							colliderPos = playerBounds.getCenterY();
							return true; // Collision detected
						}

					}
				}
			}
		}
		return false; // No collision detected
	}

	/**
	 * Trouve l'indice du cercle le plus proche dans la grille, basé sur la position
	 * du joueur.
	 * 
	 * @param playerCenterX La position X du centre du joueur.
	 * @param playerCenterY La position Y du centre du joueur.
	 * @param circles       Le tableau de cercles représentant la grille.
	 * @return L'indice du cercle le plus proche.
	 */

	private int findNearestCircleIndex(double playerCenterX, double playerCenterY, Circle[] circles) {
		double minDistance = Double.MAX_VALUE;
		int nearestCircleIndex = -1;

		for (int i = 0; i < circles.length - 1; i++) {
			double distance = Math.hypot(playerCenterX - circles[i].getCenterX(),
					playerCenterY - circles[i].getCenterY());

			if (distance < minDistance) {
				minDistance = distance;
				nearestCircleIndex = i;
			}
		}

		return nearestCircleIndex;
	}

	/**
	 * Affiche un écran de victoire lorsque le joueur gagne.
	 */

	public void triumph() {
		Stage winStage = new Stage();
		Button winOk = new Button("Quitter");
		Label titreWin = new Label("Félicitation!\n");
		Label descWin = new Label("Vous avez terminé le jeu \r\n" + "en un temps de " + label.getText() + " et de "
				+ nbrCoups + " déplacement.\r\n");
		Label quitLabel = new Label("Appuyer sur Quitter pour retourner" + "\n au menu principal.");

		winOk.setPadding(new Insets(10, 30, 10, 30));
		winOk.setAlignment(Pos.CENTER);
		titreWin.setStyle("-fx-font-weight: bold; -fx-font-size: 32px;");
		titreWin.setAlignment(Pos.CENTER);
		descWin.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
		quitLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

		HBox.setMargin(winOk, new Insets(0, 10, 0, 0));
		HBox hbox = new HBox(10, winOk);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);

		VBox vbox = new VBox(titreWin, descWin, quitLabel, hbox);
		VBox.setMargin(titreWin, new Insets(50, 0, 0, 10));
		VBox.setMargin(descWin, new Insets(0, 0, 0, 10));
		VBox.setMargin(quitLabel, new Insets(0, 0, 0, 10));
		StackPane stack = new StackPane(vbox);
		stack.setAlignment(Pos.CENTER);

		winOk.setOnMouseClicked(event -> {
			winStage.close();
			try {
				retourMenu(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Scene winScene = new Scene(stack, 400, 400);

		winStage.setResizable(false);
		winStage.setScene(winScene);
		winStage.setTitle("Victoire!");
		winStage.show();
	}

	/**
	 * Gère le retour au menu principal.
	 * 
	 * @param event L'événement de clic de souris qui déclenche cette action.
	 * @throws Exception En cas d'erreur lors du chargement du menu.
	 */

	@FXML
	protected void retourMenu(MouseEvent event) throws Exception {
		// Node source = (Node) event.getSource();

		// Get the corresponding Scene and Stage
		Scene currentScene = jeu.getScene();
		Stage currentStage = (Stage) currentScene.getWindow();
		// System.out.println(currentStage.getTitle());
		// Close the current window
		currentStage.close();

		BorderPane jeu = (BorderPane) FXMLLoader.load(getClass().getResource("/application/Menu.fxml"));
		Scene scene = new Scene(jeu, Screen.getPrimary().getVisualBounds().getWidth(),
				Screen.getPrimary().getVisualBounds().getHeight());
		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

		Stage fenetre = new Stage();
		fenetre.setTitle("Rush Hour par Marcelo Zevallos");
		fenetre.setFullScreen(true);
		fenetre.setScene(scene);
		fenetre.show();
	}

	/**
	 * Redémarre le jeu en réinitialisant l'état et en rechargeant les données.
	 * 
	 * @param event L'événement de clic de souris qui déclenche cette action.
	 * @throws Exception En cas d'erreur lors du redémarrage.
	 */

	@FXML
	protected void restart(MouseEvent event) throws Exception {
		jeu.getChildren().clear();
		label.setText(null);
		nbrCoups = 0;
		coups.setText(Integer.toString(nbrCoups));
		stopTimer();

		Platform.runLater(() -> {
			isRunning = true;
			// System.out.println("Niveau recu: " + path);
			lD = new lireDonnee(path + ".txt");
			initialiserTableau(lD);
			ajouterVoiture(lD);

			for (int i = 0; i < vehicules.size(); i++) {
				if (lD.getArrDonnee().get(i).getDirection() == 'V') {
					if (lD.getArrDonnee().get(i).getLongeur() == 2) {
						moveOnMouseDragY(scene, vehicules.get(i), 2);
					} else if (lD.getArrDonnee().get(i).getLongeur() == 3) {
						moveOnMouseDragY(scene, vehicules.get(i), 3);
					}

				} else if (lD.getArrDonnee().get(i).getDirection() == 'H') {
					if (lD.getArrDonnee().get(i).getLongeur() == 2) {
						moveOnMouseDragX(scene, vehicules.get(i), 2);
					} else if (lD.getArrDonnee().get(i).getLongeur() == 3) {
						moveOnMouseDragX(scene, vehicules.get(i), 3);
					}

				}
			}
		});

		Rectangle BorderBody1 = new Rectangle(145, 500, Color.RED);
		Rectangle BorderBody2 = new Rectangle(145, 500, Color.RED);
		Rectangle BorderBody3 = new Rectangle(500, 145, Color.RED);
		Rectangle BorderBody4 = new Rectangle(500, 145, Color.RED);

		Border_Right = new Group(BorderBody1);
		Border_Left = new Group(BorderBody2);
		Border_Top = new Group(BorderBody3);
		Border_Bottom = new Group(BorderBody4);

		Border_Right.setLayoutX(490);
		Border_Left.setLayoutX(-180);
		Border_Top.setLayoutY(-160);
		Border_Bottom.setLayoutY(440);

		checkWin.setLayoutX(490);
		checkWin.setLayoutY(150);

		checkWin.setVisible(false);
		Border_Right.setVisible(false);
		Border_Left.setVisible(false);
		Border_Top.setVisible(false);
		Border_Bottom.setVisible(false);

		jeu.getChildren().addAll(Border_Right, Border_Left, Border_Top, Border_Bottom, checkWin);

		new Thread(this).start();
	}

	/**
	 * Exécute une boucle pour suivre le temps de jeu.
	 */

	@Override
	public void run() {

		while (isRunning) {

			// Formater les minutes et les secondes
			int minutes = seconds / 60;
			int remainingSeconds = seconds % 60;

			// Formater avec deux chiffres
			DecimalFormat df = new DecimalFormat("00");
			String minuteFormatee = df.format(minutes);
			String secondeFormatee = df.format(remainingSeconds);

			// Mettre à jour l'étiquette sur l'interface graphique
			Platform.runLater(() -> label.setText(minuteFormatee + " : " + secondeFormatee));

			// Incrémenter le compteur de secondes
			seconds++;

			// Attendre une seconde avant la prochaine mise à jour
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Arrête le chronomètre et réinitialise le compteur de temps.
	 */

	public void stopTimer() {
		isRunning = false;
		seconds = 0;
		Platform.runLater(() -> label.setText("00 : 00"));
	}

	/**
	 * Définit le niveau de jeu basé sur une chaîne donnée.
	 * 
	 * @param string La chaîne représentant le chemin du niveau à charger.
	 */

	public void setNiveau(String string) {
		this.path = string;
		// System.out.println("Niveau recu: " + string);
		// System.out.println("Niveau recu: " + path);
	}
}
