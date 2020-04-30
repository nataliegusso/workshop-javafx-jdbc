package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable{
	
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
	public void onBtNewAction() {  //Tratamento de eventos ao clicar o botão
		System.out.println("onBtNewAction");
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
	
}

 