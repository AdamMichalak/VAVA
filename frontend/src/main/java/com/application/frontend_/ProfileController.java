package com.application.frontend_;

import com.application.frontend_.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.json.JSONArray;
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

    @FXML private ScrollPane myEvents;

    @FXML private Button backToHome;

    @FXML private Button createEvent;

    @FXML private Label name;

    @FXML private Label email;

    @FXML private Label isic;

    @FXML private Label location;

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

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());
        name.setText(jsonObject.get("first_name").toString() + " " + jsonObject.get("last_name").toString());
        email.setText("E-mail: " + jsonObject.get("email").toString());
        isic.setText("ISIC: " + jsonObject.get("isic_number").toString());
        location.setText("Lokalita: " + jsonObject.get("location").toString());
    }

    public void getJoinedEvents() throws IOException {
        String url = "http://localhost:8080/api/user/participations";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());
        JSONArray list = (JSONArray) jsonObject.get("events");

        VBox eventsList = new VBox();
        eventsList.setSpacing(10);
        eventsList.setPadding(new Insets(20, 20, 20, 20));
        for (Object js : list) {
            JSONObject event = (JSONObject) js;

            HBox eventBox = new HBox();
            Label eventName = new Label();
            HBox eventNameContainer = new HBox();
            eventNameContainer.setMinWidth(510);
            eventName.setText(event.getString("name"));
            eventName.setPadding(new Insets(5));
            eventName.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-background-color: #cdd0d4; -fx-background-radius: 4px;");
            eventNameContainer.getChildren().add(eventName);

            Button eventBtn = new Button("Zobraziť detaily udalosti");
            eventBtn.setPadding(new Insets(5, 25, 5, 25));
            eventBtn.setStyle("-fx-background-color: #000066; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand");
            eventBtn.setTextFill(Paint.valueOf("#fff"));
            eventBtn.setOnAction((e) -> {
                currentEventId = event.getInt("id");
                showInfoEvent();
            });

            eventBox.getChildren().addAll(eventNameContainer, eventBtn);
            eventsList.getChildren().add(eventBox);
        }

        myEvents.setContent(eventsList);
    }

    public void showInfoEvent() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("event.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        backToHome.setOnAction((event) -> back());
        createEvent.setOnAction((event) -> createNewEvent());
        try{
            getJoinedEvents();
            getItems();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void back() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("home.fxml")));
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
