package com.application.frontend_;

import com.application.frontend_.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.json.JSONObject;
import org.w3c.dom.events.Event;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController extends SwitchScenes {
    @FXML
    private VBox system;

    @FXML private Button backToHome;

    @FXML private Button createEvent;

    @FXML private Label NameSecondName;

    @FXML private Label email;

    public void getItems() throws IOException {
        String url = "http://localhost:8080/api/user/info/token";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // definovanie typu POST / PUT / GET a jazyka JSON
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());
        con.connect();

        System.out.println(con.getContent().toString());
        System.out.println(new InputStreamReader(con.getInputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());
        NameSecondName.setText(jsonObject.get("first_name").toString() + " " + jsonObject.get("last_name").toString());
        email.setText(jsonObject.get("email").toString());
        int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);
    }

    public void getJoinedEvents() throws IOException {
        String url = "";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());
        con.connect();


    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        backToHome.setOnAction((event) -> back());
        //signUpHome.setOnAction((event) -> register());
        //signInHome.setOnAction((event) -> switchToLoginScreen());
        createEvent.setOnAction((event) -> createNewEvent());
        try{
            getItems();
        }catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try{
            getJoinedEvents();
        }catch (IOException e) {
            e.printStackTrace();
        }*/

        // json objekt, ktory sa posiela
        //JSONObject mainObject = new JSONObject();

        //System.out.println(emailTemp);
        //System.out.println(passTemp);
        //mainObject.put("email", email.getText());
        //mainObject.put("password", password.getText());

        // poslanie json objektu pomocou WR
        //con.setDoOutput(true);
        //DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        //wr.writeBytes(mainObject.toString());
        //wr.flush();
        //wr.close();

        // ziskanie response code 2xx 3xx 4xx a vypisanie spravy
        //int responseCode = con.getResponseCode();
        //System.out.println("Sending 'POST' request to URL : " + url);
        //System.out.println("Post Data : " + mainObject);
        //System.out.println("Response Code : " + responseCode);

        //BufferedReader in = new BufferedReader(
        //        new InputStreamReader(con.getInputStream()));
        //String output;
        //StringBuffer response = new StringBuffer();

        //while ((output = in.readLine()) != null) {
        //    response.append(output);
        //}
        //in.close();

        //JSONObject jsonObject = new JSONObject(response.toString());
        //JSONArray nameArray = jsonObject.names();
        //JSONArray tokenJSON = jsonObject.toJSONArray(nameArray);
        //System.out.println("Token :) -> " + tokenJSON.toString());
        //this.token = tokenJSON.toString();
        //setToken(this.token);
        //switchToHomeScreen();
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

    public void createNewEvent() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("createEvent.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
