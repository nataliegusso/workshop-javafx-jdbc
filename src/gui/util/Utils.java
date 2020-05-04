package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {

	public static Stage currentStage(ActionEvent event) {    //retorna o stage atual (action Event � o qu o bot�o recebe)
		return (Stage) ((Node) event.getSource()).getScene().getWindow();  //Node � downcast do getSourse e o Stage � um downcast do getWindow
	}
	
	public static Integer tryParseToInt(String str) {  //converter o valor da caixinha de string p inteiro
		try {
			return Integer.parseInt(str);
		}
		catch (NumberFormatException e) {  //se � um n�mero inv�lido, retorna nulo
			return null;
		}
	}
}