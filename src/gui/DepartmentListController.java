package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{  //configurações do controller
	
	private DepartmentService service;
	
	@FXML
	private TableView<Department> tableViewDepartment;

	@FXML
	private TableColumn<Department, Integer> tableColumnId;

	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	private Button btNew;
	
	private ObservableList<Department> obsList;  //carregar os departamentos aqui
	
	@FXML
	public void onBtNewAction(ActionEvent event) {  //Botão p cadastrar novo department. Tratamento de eventos ao clicar o botão (qdo clicar o botão chama o método p abrir o formulário)
		Stage parentStage = Utils.currentStage(event); //acessa o currentStage dentro do utils
		Department obj = new Department();  //Formulário começa vazio. Tem que injetar no controlador do DepartmentFormController, por isso coloca-se obj no createDialogForm abaixo
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);  //obj (Department), nome da tela, e o stage da tela atual
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service; // Injetar a independência na classe sem colocar a implementação (new Department)
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();  //para iniciar algum componente da tela
	}

	private void initializeNodes() {  //vou iniciar a tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();  //Para acessar a janela da cena
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());  //Para a tableview encaixar na altura da janela
	}

	public void updateTableView() {  //acessa os serviços, carrega os departamentos e joga lá na ObservableList, que associarei à tableView, que irá p tela
		if (service == null) {
			throw new IllegalStateException("Service was null");  //se o programador esquecer de injetar a dependência (manual)
		}
		List<Department> list = service.findAll();   //recupera a lista
		obsList = FXCollections.observableArrayList(list); //carrega no obsList
		tableViewDepartment.setItems(obsList);		//mostra na tela
	}
	
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {  //carrega a janela p preencher o formulário do departamento
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));  
			Pane pane = loader.load();  //abrir janela / carregar view

			DepartmentFormController controller = loader.getController();  //pegar uma referência p o controlador (controller)
			controller.setDepartment(obj);  //injeta o departemnet no controlador da tela do formulário
			controller.setDepartmentService(new DepartmentService());  //injeta o departmentService
			controller.updateFormData();  //carrega os dados do obj no formulário
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);  //se pode ou não redimensionar a janela
			dialogStage.initOwner(parentStage);  //pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL);  //trava a janela: não pode acessar a outra se não fechar essa
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
}

 