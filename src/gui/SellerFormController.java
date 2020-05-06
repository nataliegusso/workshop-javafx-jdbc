package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {   //configura��es do controller
	
	private Seller entity;  //depend�ncia c seller

	private SellerService service;  //criar depend�ncia
	
	private DepartmentService departmentService;  //depend�ncia entre o controller e o departmentSevice: poder escolher o department do seller, mas tem que buscar no BD os departments p preencher a lista
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();  //guarda uma lista de objetos interessados em receber o evento, para eese objetos se inscreverem (add) cria-se o m�todo subscribeDataChangeListener
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dpBirthDate;
	
	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;  //qdo aperta na seta e aparece uma caixa cheia de op��es  //ObservableList<Department> obsList //private ObservableList<Department> obsList //vai carregar a lista no loadAssociatedObjects
	

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorBirthDate;
	
	@FXML
	private Label labelErrorBaseSalary;
	
	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	
	private ObservableList<Department> obsList;  //carrega a lista de departments do BD p/ colocar no combobox 	//vai carregar no loadAssociatedObjects
		
	public void setSeller(Seller entity) {  //o controlador passa a ter uma int�ncia do seller
		this.entity = entity;
	}
	
	public void setServices(SellerService service, DepartmentService departmentService) { //injeta 2 services ao mesmo tempo
		this.service = service;
		this.departmentService = departmentService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {  //objetos se inscrevem (add) na lista
		dataChangeListeners.add(listener);
	}
		
	@FXML
	public void onBtSaveAction(ActionEvent event) {  //salva o seller no bd //ActionEvent event: chama a janela atual p depois fechar
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();  //pega os dados das caixinhas e instancia um novo seller
			service.saveOrUpdate(entity);  //salva, mas tem que injetar l� no SellerListController antes
			notifyDataChangeListeners(); //DataChangeListener (observer) para atualizar o que foi feito no SellerFromController (subject)
			Utils.currentStage(event).close();  //manda fechar a janela
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {  //Subject: emiss�o de eventos
		for (DataChangeListener listener : dataChangeListeners) { //executar o onDataChanged l� da classe DataChangeListener (emitir o evento p os listeners)
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();  //cria um objeto vazio

		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); //chama a fun��o que pega a string da caixa e converte num inteiro, se der errado ele passa um nulo
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {  //campo nome n�o pode ser vazio //trim: elimina espa�os em branco no in�cio ou fim
			exception.addError("name", "Field can't be empty");  //chama o m�todo na classe ValidationException
		}
		obj.setName(txtName.getText());  //pega o nome digitado
				
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addError("email", "Field can't be empty");
		}
		obj.setEmail(txtEmail.getText());

		if (dpBirthDate.getValue() == null) {
			exception.addError("birthDate", "Field can't be empty");
		}
		else {
			Instant instant = Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));  //atStartOfDay: converte a data escolhida no hor�rio do computador do usu�rio para o instant q � uma data independentemente de localidade
			obj.setBirthDate(Date.from(instant));  //o meu obj espera um dado do tipo Date, tem que converter o instant
		}

		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addError("baseSalary", "Field can't be empty");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));  //� string e tem que converter em double, mas pode dar erro ao preencher, da� ele retorna um nulo  //est� l� no utils (F3)

		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0) {   //se na minha cole��o de erros tivr pelo menos um erro, lan�a exce��o
			throw exception;
		}
		
		return obj; //retorna o obj
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();  //manda fechar a janela
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {  //Restri��es
		Constraints.setTextFieldInteger(txtId);  //S� aceitar inteiro
		Constraints.setTextFieldMaxLength(txtName, 70);  //Tamanho m�ximo
		Constraints.setTextFieldDouble(txtBaseSalary);  //Double
		Constraints.setTextFieldMaxLength(txtEmail, 60);  //Tamanho m�ximo
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");  //formato p data
		initializeComboBoxDepartment();  //ComboBox com lista de departments
	}
	
	public void updateFormData() {  //pega os dados do seller e coloca nas caixas do formul�rios
		if (entity == null) {		//programa��o defensiva caso o programador esque�a de colocar os valores
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));  //converte o id que � integer em string e joga na caixinha id
		txtName.setText(entity.getName());  //pega o name e joga na caixinha name
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));  //como � Double, e est� como String, tem que converter e define a pontua��o com o locale
		
		if (entity.getBirthDate() != null) {
//			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
			dpBirthDate.setValue(LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
		
		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();  //pega o primeiro elemento do combo box se estiver nulo
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());  //pega os dados do vendedor e preenche com os departments o formul�rio
		}
	}
	
	public void loadAssociatedObjects() {  //chama o DepartmentService e carrega a lista de departments do BD p/ colocar no combobox
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService was null");  //esqueceu de fazer inje��o de independ�ncia
		}
		List<Department> list = departmentService.findAll();  //carrega em uma lista os departments
		obsList = FXCollections.observableArrayList(list);  //joga os departments p a obsList 
		comboBoxDepartment.setItems(obsList);  //associa a lista ao combobox
	}
	
	private void setErrorMessages(Map<String, String> errors) {	//percorre a lista de erros da exce��o e escreve no formul�rio
		Set<String> fields = errors.keySet();  

		//if (fields.contains("name")) {
		//	labelErrorName.setText(errors.get("name"));  //joga a mensagem que est� no name
		//}else {
		//	labelErrorName.setText("");  //n�o joga a mensagem ou apaga a mensagem
		//}

		//operador condicional tern�rio: labelErrorName.setText((fields.contains("name") ? True : False)
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));  
		labelErrorEmail.setText((fields.contains("email") ? errors.get("email") : ""));
		labelErrorBirthDate.setText((fields.contains("birthDate") ? errors.get("birthDate") : ""));
		labelErrorBaseSalary.setText((fields.contains("baseSalary") ? errors.get("baseSalary") : ""));
	}
	
	private void initializeComboBoxDepartment() {  //chama l� no initializeNodes
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}
}
