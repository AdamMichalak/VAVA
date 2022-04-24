package com.application.frontend_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;

public class EventController extends SwitchScenes {
    Image img = new Image("file:logo.png");
    ImageView imageView = new ImageView(img);

    @FXML
    private VBox system;

    @FXML private Button backToHome;

    @FXML private Button signUpHome;

    @FXML private Button signInHome;

    @FXML private Button joinEvent;

    @FXML private Label eventName;

    @FXML private Label description;

    @FXML private Label date;

    @FXML private Label time;

    @FXML private Label participants;

    @FXML private ImageView eventImage;

    public void getItems() throws IOException {
        String id = "23";
        String url = "http://localhost:8080/api/event/detail?event_id=" + id;
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

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        JSONObject jsonObject = new JSONObject(result.toString());

        if (jsonObject.get("title_photo").toString().isEmpty()) {
            eventImage.setImage(new Image("logo.png"));
        } else {
            String base64 = jsonObject.get("title_photo").toString();
            byte[] fileContent = Base64.getDecoder().decode(base64);

            ByteArrayInputStream x = new ByteArrayInputStream(fileContent);
            eventImage.setImage(new Image(x));
        }

        eventName.setText(jsonObject.get("name").toString());
        date.setText(jsonObject.get("expiration_date").toString());
        participants.setText(jsonObject.get("max_participate").toString());
        description.setText(jsonObject.get("description").toString());

        int responseCode = con.getResponseCode();
    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        backToHome.setOnAction((event) -> back());
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        joinEvent.setOnAction((event) -> showAlert());
        try{
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

    public void showAlert() {
        showMessageDialog(null, "Pridali ste sa k udalosti.");
    }

    public void addToList() {
        //LoginController trieda = new LoginController();
        //String token1 = trieda.getToken();
        //System.out.print("tokeeeen: " + token1);
        showMessageDialog(null, "Pridali ste do zoznamu obľúbených.");
    }
}
