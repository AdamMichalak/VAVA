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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;

public class EventController extends SwitchScenes {
    @FXML
    private VBox system;

    @FXML private Button backToHome;

    @FXML private Button editButton;

    @FXML private Button joinEvent;

    @FXML private Label eventName;

    @FXML private Label description;

    @FXML private Label date;

    @FXML private Label time;

    @FXML private Label participants;

    @FXML private ImageView eventImage;

    public void participateInEvent() throws IOException {
        // pripojenie
        String url = "http://localhost:8080/api/event/participate";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // definovanie typu POST / PUT / GET a jazyka JSON
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());

        // json objekt, ktory sa posiela
        JSONObject mainObject = new JSONObject();
        mainObject.put("event_id", currentEventId);

        // poslanie json objektu pomocou WR
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(mainObject.toString());
        wr.flush();
        wr.close();

        // ziskanie response code 2xx 3xx 4xx a vypisanie spravy
        int responseCode = con.getResponseCode();

        if (responseCode == 201) {
            joinEvent.setDisable(true);
            joinEvent.setText("Zúčastním sa");
            showMessageDialog(null, "Pridali ste sa k udalosti.");
        } else {
            showMessageDialog(null, "Niečo sa pokazilo");
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
    }

    public void getItems() throws IOException {
        String url = "http://localhost:8080/api/event/detail?event_id=" + currentEventId;
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

        editButton.setVisible(LoginController.getIsAdmin() || jsonObject.getBoolean("me_owner"));

        if (jsonObject.get("title_photo").toString().isEmpty()) {
            eventImage.setImage(new Image("logo.png"));
        } else {
            String base64 = jsonObject.get("title_photo").toString();
            byte[] fileContent = Base64.getDecoder().decode(base64);

            ByteArrayInputStream x = new ByteArrayInputStream(fileContent);
            eventImage.setImage(new Image(x));
        }

        LocalDateTime dateTime = ParseDate.parseDateFromDBToLocalDateTime(jsonObject.get("expiration_date").toString());
        eventName.setText(jsonObject.get("name").toString());
        time.setText(ParseDate.getTimeFromDateTime(dateTime));
        date.setText(ParseDate.parseDateFromDBToLocalDateString(jsonObject.get("expiration_date").toString()));
        participants.setText(jsonObject.get("max_participate").toString());
        description.setText(jsonObject.get("description").toString());
        System.out.println(jsonObject.getInt("me_participate"));
        if (jsonObject.getInt("me_participate") > 0) {
            joinEvent.setDisable(true);
            joinEvent.setText("Zúčastním sa");
        }

        int responseCode = con.getResponseCode();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        editButton.setOnAction((e) -> switchToUpdateScreen());
        backToHome.setOnAction((event) -> back());
        joinEvent.setOnAction((event) -> {
            try {
                participateInEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try{
            getItems();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToUpdateScreen() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("updateEvent.fxml")));
        } catch (IOException e) {
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
}
