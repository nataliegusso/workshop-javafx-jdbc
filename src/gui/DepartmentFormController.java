package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
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
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {   //configura��es do controller
	
	private Department entity;  //depend�ncia c departamento

	private DepartmentService service;  //criar depend�ncia
	
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
	
	public void setDepartment(Department entity) {  //o controlador passa a ter uma int�ncia do departamento
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
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
			service.saveOrUpdate(entity);  //salva, mas tem que injetar l� no DepartmentListController antes
			Utils.currentStage(event).close();  //manda fechar a janela
			//falta atualizar
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private Department getFormData() {
		Department obj = new Department();  //cria umobjeto vazio

		obj.setId(Utils.tryParseToInt(txtId.getText())); //chama a fun��o que pega a string da caixa e converte num inteiro
		obj.setName(txtName.getText());  //pega o nome digitado

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
	
	public void updateFormData() {  //pega os dados do departamento e coloca nas caixas do formul�rios
		if (entity == null) {		//programa��o defensiva caso o programador esque�a de colocar os valores
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));  //converte o id que � integer em string e joga na caixinha id
		txtName.setText(entity.getName());  //pega o name e joga na caixinha name
	}

	private void initializeNodes() {  //Restri��es
		Constraints.setTextFieldInteger(txtId);  //S� aceitar inteiro
		Constraints.setTextFieldMaxLength(txtName, 30);  //Tamanho m�ximo
	}

}
