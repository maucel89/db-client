package it.maucel89.dbclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("DB Client");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(
            new Image(getClass().getResourceAsStream("icon.png")));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
