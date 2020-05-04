package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	public static Stage currentStage(ActionEvent event) {    //retorna o stage atual (action Event é o qu o botão recebe)
		return (Stage) ((Node) event.getSource()).getScene().getWindow();  //Node é downcast do getSourse e o Stage é um downcast do getWindow
	}
	
	public static Integer tryParseToInt(String str) {  //converter o valor da caixinha de string p inteiro
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {  //se é um número inválido, retorna nulo
			return null;
		}
	}
}