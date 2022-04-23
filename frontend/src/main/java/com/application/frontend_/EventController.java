package com.application.frontend_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;

public class EventController extends SwitchScenes {
    Image img = new Image("file:logo.png");
    ImageView imageView = new ImageView(img);

    @FXML
    private VBox system;

    @FXML private Button backToHome;

    @FXML private Button signUpHome;

    @FXML private Button signInHome;

    @FXML private Button joinEvent;

    @FXML private Button addToWishlist;


    public void initialize(URL url, ResourceBundle resourceBundl) {
        backToHome.setOnAction((event) -> back());
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        joinEvent.setOnAction((event) -> showAlert());
        addToWishlist.setOnAction((event) -> addToList());
    }

    public void back() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("home.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("register.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLoginScreen() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("login.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert() {
        showMessageDialog(null, "Pridali ste sa k udalosti.");
    }

    public void addToList() {
        //LoginController trieda = new LoginController();
        //String token1 = trieda.getToken();
        //System.out.print("tokeeeen: " + token1);
        showMessageDialog(null, "Pridali ste do zoznamu obľúbených.");
    }
}
