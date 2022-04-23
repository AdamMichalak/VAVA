package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;


public class LoginController<url, params> extends SwitchScenes{
    
    @FXML private VBox system;

    @FXML private TextField email;

    @FXML private PasswordField password;

    @FXML private Button loginButton;

    @FXML private Button signUp;

    private String token;

    private static String tokenObject = new String();
    /**
     * After clicking gui objects calls another procedures
     */
    public void initialize(URL url, ResourceBundle resourceBundl) {
        loginButton.setOnAction((event) -> {
            try {
                login();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        signUp.setOnAction((event) -> register());
    }

    public void register() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/register.fxml")));
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
        //System.out.println("Sending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + mainObject);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        JSONObject jsonObject = new JSONObject(response.toString());
        JSONArray nameArray = jsonObject.names();
        JSONArray tokenJSON = jsonObject.toJSONArray(nameArray);
        System.out.println("Token :) -> " + tokenJSON.toString());
        this.token = tokenJSON.toString();
        setToken(this.token);
        switchToHomeScreen();
    }

    public void setToken(String token){
        this.tokenObject = token;
        System.out.println("Token2 :) -> " + tokenObject);
        getToken();
    }

    public static String getToken(){
        System.out.println("Token3 :) -> " + tokenObject);
        return tokenObject;
    }

    public void switchToHomeScreen() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/home.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
