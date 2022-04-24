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
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.text.Position;
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
    @FXML
    Pagination pagination;

    @FXML private VBox system;

    @FXML private Button signUpHome;

    @FXML private Button signInHome;

    @FXML private Button showEvent;

    @FXML private Button showProfile;


    public FlowPane createPage(int pageIndex) throws IOException {
        FlowPane box = new FlowPane();
        box.setMinHeight(420);
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

        JSONObject jsonObject = new JSONObject(result.toString());
        JSONArray list = (JSONArray) jsonObject.get("events");

        for (Object js : list) {
            JSONObject event = (JSONObject) js;
            HBox eventBox = new HBox();
            eventBox.setPadding(new Insets(20));
            eventBox.setMinWidth(370);
            eventBox.setMinHeight(180);
            eventBox.setStyle("-fx-background-color: #eaedf0;");

            VBox left = new VBox();
            left.setMinWidth(120);
            left.setMaxWidth(120);
            Label eventName = new Label();
            eventName.setFont(new Font(20));
            eventName.setStyle("-fx-font-weight: bold");
            eventName.setText("fsafsda fsda fasd fsda");
            // eventName.setText(event.get("name").toString());

            VBox right = new VBox();
            right.setSpacing(10);
            right.setAlignment(Pos.CENTER);
            right.setPadding(new Insets(0, 20, 0, 20));
            right.setMinWidth(210);
            right.setMaxWidth(210);
            Label eventExpDate = new Label();
            eventExpDate.setMinWidth(170);
            eventExpDate.setFont(new Font(14));
            eventExpDate.setStyle("-fx-font-weight: bold");
            eventExpDate.setText("Dátum: " + event.get("expiration_date").toString());
            Text eventDesc = new Text();
            eventDesc.setWrappingWidth(170);
            eventDesc.setText(event.get("description").toString());

            Button eventBtn = new Button("Zobraziť");
            eventBtn.setOnAction((e) -> {
                showInfoEvent();
            });

            left.getChildren().addAll(eventName);
            right.getChildren().addAll(eventExpDate, eventDesc, eventBtn);
            eventBox.getChildren().addAll(left, right);

            box.getChildren().addAll(eventBox);
        }

        return box;
    }

    public void getItems() throws IOException {
        pagination = new Pagination(10);
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
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        // showEvent.setOnAction((event) -> showInfoEvent());
        showProfile.setOnAction((event) -> showProfile());
        try{
            getItems();
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
