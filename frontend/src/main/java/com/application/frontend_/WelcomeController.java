package com.application.frontend_;

import com.application.frontend_.SwitchScenes;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;


public class WelcomeController extends SwitchScenes {
    @FXML private VBox system;
    @FXML private Button login, register;

    public void initialize(URL url, ResourceBundle resourceBundl) {
        register.setOnAction((event) -> switchToRegister());
        login.setOnAction((event) -> switchToLogin());
    }

    public void switchToRegister() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("register.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLogin() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("login.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
