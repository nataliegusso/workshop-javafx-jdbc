package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerAction() { 
		loadView("/gui/SellerList.fxml", (SellerListController controller) -> {  
			controller.setSellerService(new SellerService());
			controller.updateTableView();
		});
	}	

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {  //depois da vírgula é uma função para inicializar o controlador (expressão lambda vinda dos comandos mostrados abaixo)
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}	
	//DepartmentListController controller = loader.getController(); //carrega a view e acesssa o controle
	//controller.setDepartmentService(new DepartmentService());  //injeta dependêcia do service no controller
	//controller.updateTableView();  //atualiza dados na tela
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});  //não tem nada p ser feito, logo declaro x -> {}. Mas p funcionar, tenho que acrescentar esta declaração na função (Consumer<T>: generics)
	}	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {  //Função para abrir outra tela //Synchronized: garante que não será interrompido
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
			
			T controller = loader.getController(); //o get controller vai chamar o controlador 
			initializingAction.accept(controller);  //executa a função depois da vírgula
			
		}catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

			
			

}
