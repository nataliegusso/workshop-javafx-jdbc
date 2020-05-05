package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {   //configurações do controller
	
	private Department entity;  //dependência c departamento

	private DepartmentService service;  //criar dependência
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();  //guarda uma lista de objetos interessados em receber o evento, para eese objetos se inscreverem (add) cria-se o método subscribeDataChangeListener
	
	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	
	public void setDepartment(Department entity) {  //o controlador passa a ter uma intância do departamento
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {  //objetos se inscrevem (add) na lista
		dataChangeListeners.add(listener);
	}
		
	@FXML
	public void onBtSaveAction(ActionEvent event) {  //salva o department no bd //ActionEvent event: chama a janela atual p depois fechar
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();  //pega os dados das caixinhas e instancia um novo departament
			service.saveOrUpdate(entity);  //salva, mas tem que injetar lá no DepartmentListController antes
			notifyDataChangeListeners(); //DataChangeListener (observer) para atualizar o que foi feito no DepartmentFromController (subject)
			Utils.currentStage(event).close();  //manda fechar a janela
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListeners() {  //Subject: emissão de eventos
		for (DataChangeListener listener : dataChangeListeners) { //executar o onDataChanged lá da classe DataChangeListener (emitir o evento p os listeners)
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();  //cria um objeto vazio

		ValidationException exception = new ValidationException("Validation error");
		
		obj.setId(Utils.tryParseToInt(txtId.getText())); //chama a função que pega a string da caixa e converte num inteiro
		
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {  //campo nome não pode ser vazio //trim: elimina espaços em branco no início ou fim
			exception.addError("name", "Field can't be empty");  //chama o método na classe ValidationException
		}
		
		obj.setName(txtName.getText());  //pega o nome digitado
		
		if (exception.getErrors().size() > 0) {  //se na minha coleção de erros tivr pelo menos um erro, lança exceção
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
	
	public void updateFormData() {  //pega os dados do departamento e coloca nas caixas do formulários
		if (entity == null) {		//programação defensiva caso o programador esqueça de colocar os valores
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));  //converte o id que é integer em string e joga na caixinha id
		txtName.setText(entity.getName());  //pega o name e joga na caixinha name
	}

	private void initializeNodes() {  //Restrições
		Constraints.setTextFieldInteger(txtId);  //Só aceitar inteiro
		Constraints.setTextFieldMaxLength(txtName, 30);  //Tamanho máximo
	}

	private void setErrorMessages(Map<String, String> errors) {	//percorre a lista de erros da exceção e escreve no formulário
		Set<String> fields = errors.keySet();  

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
}
