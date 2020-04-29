package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() { //Tratar as ações. Aparece no console do eclipse
		System.out.println("onMenuItemSellerAction");
	}	

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml");
	}	

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml");
	}	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	private synchronized void loadView(String absoluteName) {  //Função para abrir outra tela //Synchronized: garante que não será interrompido
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene(); //Chama a tela principal na classe Main
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();  //getRoot: pega o primeiro elemento da view (ScrollPane, conteúdo), tem que fazer uma casting p ScrollPane e um p VBox
			//tem que preservar o menuBar, excluir os filhos do VBox Main e incluir os da VBox da tela About
			Node mainMenu = mainVBox.getChildren().get(0);  //get(0) é o primeiro filho: mainMenu
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
		}catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
}
