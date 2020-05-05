package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {  //configurações do controller
	
	private SellerService service;
	
	@FXML
	private TableView<Seller> tableViewSeller;

	@FXML
	private TableColumn<Seller, Integer> tableColumnId;

	@FXML
	private TableColumn<Seller, String> tableColumnName;

	@FXML
	private TableColumn<Seller, String> tableColumnEmail;

	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;

	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;  //acrescenta um botão de atualizar em cada uma das linhas da tabela

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;  //acrescenta um botão de remover em cada uma das linhas da tabela
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;  //carregar os departamentos aqui
	
	@FXML
	public void onBtNewAction(ActionEvent event) {  //Botão p cadastrar novo department (qdo clicar o botão chama o método p abrir o formulário)
		Stage parentStage = Utils.currentStage(event); 
		Seller obj = new Seller();  
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage); 
	}

	public void setSellerService(SellerService service) {
		this.service = service; // Injetar a independência na classe sem colocar a implementação (new Seller)
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();  //para iniciar algum componente da tela
	}

	private void initializeNodes() {  //iniciar a tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));  //ter o cuidado do nome "id" ser igual lá na classe seller
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");  //chama o método p formatar lá da classe utils 
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);  //chama o método p formatar lá da classe utils
	
		Stage stage = (Stage) Main.getMainScene().getWindow();  //acessar a janela da cena
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());  //tableview encaixar na altura da janela
	}

	public void updateTableView() {  //acessa os serviços, carrega os vendedores e joga lá na ObservableList, que associarei à tableView, que irá p tela
		if (service == null) {
			throw new IllegalStateException("Service was null");  //se o programador esquecer
		}
		List<Seller> list = service.findAll();   //recupera a lista
		obsList = FXCollections.observableArrayList(list); //carrega no obsList
		tableViewSeller.setItems(obsList);		//mostra na tela
		initEditButtons(); //mostra o botão de edição que abre o formulário de edição
		initRemoveButtons(); //mostra o botão remove
	}
	
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {  //carrega a janela p preencher o formulário
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));  
			Pane pane = loader.load();  //abrir janela / carregar view

			SellerFormController controller = loader.getController();  //pegar uma referência p o controlador (controller)
			controller.setSeller(obj);  //injeta o seller no controlador da tela do formulário
			controller.setServices(new SellerService(), new DepartmentService());  //injeta o sellerService e o departmentService
			controller.loadAssociatedObjects();  //carrega os departments do BD e joga num combobox
			controller.subscribeDataChangeListener(this);  //inscrevendo p receber o evento de atualizar dados qdo são alterados
			controller.updateFormData();  //carrega os dados do obj no formulário
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);  //se pode ou não redimensionar a janela
			dialogStage.initOwner(parentStage);  //pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL);  //trava a janela: não pode acessar a outra se não fechar essa
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@Override
	public void onDataChanged() { //qdo recebe notificação de dados alterados...
		updateTableView();  //...atualiza dados na tabela
		
	}

	private void initEditButtons() {  //cria um botão de edição em cada linha da tabela 
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));  // é chamado lá no updateTableViewData
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {  //CellFactory: instancia os botões e configura o evento
			private final Button button = new Button("edit");
		
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
					event -> createDialogForm(  //chama o método que cria a janela do formulário
					obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));  //obj: é o seller da linha de edição que for clicada
			}
		});
	}
	
	private void initRemoveButtons() {  //cria um botão remove em cada linha da tabela para apagar //o método é chamado lá no updateTableView
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
				protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));  //chama o método abaixo
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");  //mostra um alert p usuário

		if (result.get() == ButtonType.OK) {  //o get chama o objeto dentro do objeto Optional
			if (service == null) {
				throw new IllegalStateException("Service was null");  //o programador esqueceu de injetar
			}
			try { 
				service.remove(obj);  //remove
				updateTableView();  //atualiza
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);  //se tiver chave estrangeira (seller) ele não deixa remover
			}
		}
	}
}