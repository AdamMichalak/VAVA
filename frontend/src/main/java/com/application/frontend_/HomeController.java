package com.application.frontend_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
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

    @FXML private Label name1;
    @FXML private Label name2;
    @FXML private Label name3;
    @FXML private Label name4;

    @FXML private Label date1;
    @FXML private Label date2;
    @FXML private Label date3;
    @FXML private Label date4;

    @FXML private Label desc1;
    @FXML private Label desc2;
    @FXML private Label desc3;
    @FXML private Label desc4;

    public void getItems() throws IOException {
        String url = "http://localhost:8080/api/event/browse?page=1";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        LoginController trieda = new LoginController();
        String token1 = trieda.getToken();
        token1 = token1.substring(2);
        token1 = token1.substring(0, token1.length() - 1);
        token1 = token1.substring(0, token1.length() - 1);
        con.setRequestProperty ("Authorization", "Bearer "+ token1.toString());
        con.connect();

        System.out.println(con.getContent().toString());
        System.out.println(new InputStreamReader(con.getInputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }
        System.out.println(result.toString());
        JSONObject jsonObject = new JSONObject(result.toString());
        JSONArray list = new JSONArray();
        list = (JSONArray) jsonObject.get("events");
        for (int i = 0; i < 4; i++) {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2 = (JSONObject) list.get(i);
            System.out.println(jsonObject2.get("name").toString());
            System.out.println(jsonObject2.get("expiration_date").toString());
            System.out.println(jsonObject2.get("description").toString());
            if(i==0){
                name1.setText(jsonObject2.get("name").toString());
                date1.setText("Dátum: " + jsonObject2.get("expiration_date").toString());
                desc1.setText(jsonObject2.get("description").toString());
            }
            else if(i==1){
                name2.setText(jsonObject2.get("name").toString());
                date2.setText("Dátum: " + jsonObject2.get("expiration_date").toString());
                desc2.setText(jsonObject2.get("description").toString());
            }
            else if(i==2){
                name3.setText(jsonObject2.get("name").toString());
                date3.setText("Dátum: " +  jsonObject2.get("expiration_date").toString());
                desc3.setText(jsonObject2.get("description").toString());
            }
            else if(i==3){
                name4.setText(jsonObject2.get("name").toString());
                date4.setText("Dátum: " + jsonObject2.get("expiration_date").toString());
                desc4.setText(jsonObject2.get("description").toString());
            }
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        showEvent.setOnAction((event) -> showInfoEvent());
        showProfile.setOnAction((event) -> showProfile());
        try{
            getItems();
        }catch (IOException e) {
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
