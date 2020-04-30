package application;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	private static Scene mainScene;  //Guarda a tela principal no mainScene
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();  //Default: Parent parent = loader.load(); Mas eu sei que é uma ScrollPane, posso mudar
			
			scrollPane.setFitToHeight(true); //Ajustar o scrollPane à janela 
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Sample JavaFX application");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Scene getMainScene() { //como a tela principal é um atributo privado, preciso de um método public para chamá-la
		return mainScene;
	}	
	public static void main(String[] args) {
		launch(args);
	}
}
