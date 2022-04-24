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


public class LoginController<url, params> extends SwitchScenes {
    
    @FXML private VBox system;

    @FXML private TextField email;

    @FXML private PasswordField password;

    @FXML private Button loginButton;

    @FXML private Button backButton;

    @FXML private Label loginError;

    private static String token;
    private static boolean isAdmin;

    public void initialize(URL url, ResourceBundle resourceBundl) {
        if (token != null) {
            switchToHomeScreen();
        }

        BooleanBinding booleanBind = email.textProperty().isEmpty()
                .or(password.textProperty().isEmpty());

        loginButton.disableProperty().bind(booleanBind);

        loginButton.setOnAction((event) -> {
            try {
                login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        backButton.setOnAction((event) -> goBack());
    }

    public void goBack() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("welcome.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEmail() {
        return email.getText();
    }

    public String getPassword() {
        return password.getText();
    }

    public void login() throws IOException {
        // pripojenie
        String url = "http://localhost:8080/api/user/login";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // definovanie typu POST / PUT / GET a jazyka JSON
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        // json objekt, ktory sa posiela
        JSONObject mainObject = new JSONObject();

        //System.out.println(emailTemp);
        //System.out.println(passTemp);
        mainObject.put("email", email.getText());
        mainObject.put("password", password.getText());

        // poslanie json objektu pomocou WR
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(mainObject.toString());
        wr.flush();
        wr.close();

        // ziskanie response code 2xx 3xx 4xx a vypisanie spravy
        int responseCode = con.getResponseCode();
        if (responseCode == 401) {
            loginError.setText("Zadané údaje sú nesprávne");
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        JSONArray auth = new JSONArray(jsonObject.get("authorities").toString());

        if (auth.length() > 0) {
            JSONObject role = (JSONObject) auth.get(0);
            isAdmin = role.get("authority").toString().equals("ROLE_ADMIN");
            token = jsonObject.get("jwt").toString();
        } else {
            isAdmin = false;
        }

        switchToHomeScreen();
    }

    public static String getToken(){
        return token;
    }

    public static void logout() { token = null; }

    public static boolean getIsAdmin(){
        return isAdmin;
    }

    public void switchToHomeScreen() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("home.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
