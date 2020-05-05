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
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {  //depois da v�rgula � uma fun��o para inicializar o controlador (express�o lambda vinda dos comandos mostrados abaixo)
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}	
	//DepartmentListController controller = loader.getController(); //carrega a view e acesssa o controle
	//controller.setDepartmentService(new DepartmentService());  //injeta depend�cia do service no controller
	//controller.updateTableView();  //atualiza dados na tela
	
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});  //n�o tem nada p ser feito, logo declaro x -> {}. Mas p funcionar, tenho que acrescentar esta declara��o na fun��o (Consumer<T>: generics)
	}	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {  //Fun��o para abrir outra tela //Synchronized: garante que n�o ser� interrompido
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene(); //Chama a tela principal na classe Main
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();  //getRoot: pega o primeiro elemento da view (ScrollPane, conte�do), tem que fazer uma casting p ScrollPane e um p VBox
			//tem que preservar o menuBar, excluir os filhos do VBox Main e incluir os da VBox da tela About
			Node mainMenu = mainVBox.getChildren().get(0);  //get(0) � o primeiro filho: mainMenu
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController(); //o get controller vai chamar o controlador 
			initializingAction.accept(controller);  //executa a fun��o depois da v�rgula
			
		}catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

			
			

}
