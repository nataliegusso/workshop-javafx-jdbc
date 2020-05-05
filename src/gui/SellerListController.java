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

public class SellerListController implements Initializable, DataChangeListener {  //configura��es do controller
	
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
	private TableColumn<Seller, Seller> tableColumnEDIT;  //acrescenta um bot�o de atualizar em cada uma das linhas da tabela

	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;  //acrescenta um bot�o de remover em cada uma das linhas da tabela
	
	@FXML
	private Button btNew;
	
	private ObservableList<Seller> obsList;  //carregar os departamentos aqui
	
	@FXML
	public void onBtNewAction(ActionEvent event) {  //Bot�o p cadastrar novo department (qdo clicar o bot�o chama o m�todo p abrir o formul�rio)
		Stage parentStage = Utils.currentStage(event); 
		Seller obj = new Seller();  
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage); 
	}

	public void setSellerService(SellerService service) {
		this.service = service; // Injetar a independ�ncia na classe sem colocar a implementa��o (new Seller)
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();  //para iniciar algum componente da tela
	}

	private void initializeNodes() {  //iniciar a tabela
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));  //ter o cuidado do nome "id" ser igual l� na classe seller
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");  //chama o m�todo p formatar l� da classe utils 
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);  //chama o m�todo p formatar l� da classe utils
	
		Stage stage = (Stage) Main.getMainScene().getWindow();  //acessar a janela da cena
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());  //tableview encaixar na altura da janela
	}

	public void updateTableView() {  //acessa os servi�os, carrega os vendedores e joga l� na ObservableList, que associarei � tableView, que ir� p tela
		if (service == null) {
			throw new IllegalStateException("Service was null");  //se o programador esquecer
		}
		List<Seller> list = service.findAll();   //recupera a lista
		obsList = FXCollections.observableArrayList(list); //carrega no obsList
		tableViewSeller.setItems(obsList);		//mostra na tela
		initEditButtons(); //mostra o bot�o de edi��o que abre o formul�rio de edi��o
		initRemoveButtons(); //mostra o bot�o remove
	}
	
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {  //carrega a janela p preencher o formul�rio
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));  
			Pane pane = loader.load();  //abrir janela / carregar view

			SellerFormController controller = loader.getController();  //pegar uma refer�ncia p o controlador (controller)
			controller.setSeller(obj);  //injeta o seller no controlador da tela do formul�rio
			controller.setServices(new SellerService(), new DepartmentService());  //injeta o sellerService e o departmentService
			controller.loadAssociatedObjects();  //carrega os departments do BD e joga num combobox
			controller.subscribeDataChangeListener(this);  //inscrevendo p receber o evento de atualizar dados qdo s�o alterados
			controller.updateFormData();  //carrega os dados do obj no formul�rio
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);  //se pode ou n�o redimensionar a janela
			dialogStage.initOwner(parentStage);  //pai da janela
			dialogStage.initModality(Modality.WINDOW_MODAL);  //trava a janela: n�o pode acessar a outra se n�o fechar essa
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@Override
	public void onDataChanged() { //qdo recebe notifica��o de dados alterados...
		updateTableView();  //...atualiza dados na tabela
		
	}

	private void initEditButtons() {  //cria um bot�o de edi��o em cada linha da tabela 
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));  // � chamado l� no updateTableViewData
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {  //CellFactory: instancia os bot�es e configura o evento
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
					event -> createDialogForm(  //chama o m�todo que cria a janela do formul�rio
					obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));  //obj: � o seller da linha de edi��o que for clicada
			}
		});
	}
	
	private void initRemoveButtons() {  //cria um bot�o remove em cada linha da tabela para apagar //o m�todo � chamado l� no updateTableView
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
				button.setOnAction(event -> removeEntity(obj));  //chama o m�todo abaixo
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");  //mostra um alert p usu�rio

		if (result.get() == ButtonType.OK) {  //o get chama o objeto dentro do objeto Optional
			if (service == null) {
				throw new IllegalStateException("Service was null");  //o programador esqueceu de injetar
			}
			try { 
				service.remove(obj);  //remove
				updateTableView();  //atualiza
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);  //se tiver chave estrangeira (seller) ele n�o deixa remover
			}
		}
	}
}