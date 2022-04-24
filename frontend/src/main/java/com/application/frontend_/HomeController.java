package com.application.frontend_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

public class HomeController extends SwitchScenes{
    @FXML Pagination pagination;
    @FXML private VBox system;
    @FXML private Button logoutButton;
    @FXML private Button showProfile;


    public FlowPane createPage(int pageIndex) throws IOException {
        FlowPane box = new FlowPane();
        box.setMinHeight(450);
        box.setStyle("-fx-padding: 20 20 20 20;");
        box.setVgap(20);
        box.setHgap(20);

        String url = "http://localhost:8080/api/event/browse?page=" + pageIndex;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty ("Authorization", "Bearer "+ LoginController.getToken());
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());
        JSONArray list = (JSONArray) jsonObject.get("events");

        for (Object js : list) {
            JSONObject event = (JSONObject) js;
            HBox eventBox = new HBox();
            eventBox.setPadding(new Insets(20));
            eventBox.setMinWidth(370);
            eventBox.setMinHeight(180);
            eventBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#eaedf0"), new CornerRadii(8), Insets.EMPTY)));

            VBox left = new VBox();

            left.setMinWidth(120);
            left.setMaxWidth(120);
            left.setSpacing(10);
            Label eventName = new Label();
            eventName.setMinWidth(370);
            eventName.setStyle("-fx-font-size: 20; -fx-font-weight: 700;");
            eventName.setText(event.get("name").toString());

            ImageView eventImage = new ImageView();
            eventImage.setFitHeight(120);
            eventImage.setFitWidth(120);
            eventImage.setStyle("-fx-background-radius: 5;");

            if (event.get("title_photo").toString().isEmpty()) {
                eventImage.setImage(new Image("logo.png"));
            } else {
                String base64 = event.get("title_photo").toString();
                byte[] fileContent = Base64.getDecoder().decode(base64);

                ByteArrayInputStream x = new ByteArrayInputStream(fileContent);
                eventImage.setImage(new Image(x));
            }

            VBox right = new VBox();
            right.setSpacing(10);
            right.setAlignment(Pos.CENTER);
            right.setPadding(new Insets(0, 20, 0, 20));
            right.setMinWidth(210);
            right.setMaxWidth(210);
            Label eventExpDate = new Label();
            eventExpDate.setMinWidth(170);
            eventExpDate.setStyle("-fx-font-size: 14; -fx-font-weight: 700;");
            eventExpDate.setText("Dátum: " + event.get("expiration_date").toString());
            Text eventDesc = new Text();
            eventDesc.setStyle("-fx-font-size: 14;");
            eventDesc.setWrappingWidth(170);
            eventDesc.setText(event.get("description").toString());

            Button eventBtn = new Button("Zobraziť");
            eventBtn.setOnAction((e) -> {
                currentEventId = event.getInt("event_id");
                showInfoEvent();
            });

            left.getChildren().addAll(eventName, eventImage);
            right.getChildren().addAll(eventExpDate, eventDesc, eventBtn);
            eventBox.getChildren().addAll(left, right);

            box.getChildren().addAll(eventBox);
        }

        return box;
    }

    public void getItems() throws IOException {
        String url = "http://localhost:8080/api/event/page_count";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty ("Authorization", "Bearer "+ LoginController.getToken());
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());

        pagination = new Pagination(jsonObject.getInt("page_count"));
        pagination.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        pagination.setPageFactory((Integer pageIndex) -> {
            try {
                return createPage(pageIndex + 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        system.getChildren().add(pagination);
    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        logoutButton.setOnAction((e) -> logout());
        showProfile.setOnAction((event) -> showProfile());
        try{
            getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        LoginController.logout();
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("welcome.fxml")));
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
