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

public class HomeController extends SwitchScenes{
    Image img = new Image("file:logo.png");
    ImageView imageView = new ImageView(img);

    @FXML private VBox system;

    @FXML private Button signUpHome;

    @FXML private Button signInHome;

    @FXML private Button showEvent;

    @FXML private Button showProfile;


    public void initialize(URL url, ResourceBundle resourceBundl) {
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        showEvent.setOnAction((event) -> showInfoEvent());
        showProfile.setOnAction((event) -> showProfile());
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

    public void showInfoEvent() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("event.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showProfile() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("profile.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
