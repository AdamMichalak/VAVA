package com.application.frontend_;

import com.application.frontend_.LoginController;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;

import static com.application.frontend_.Validation.validateText;


public class CreateEventController extends SwitchScenes {
    @FXML
    private VBox system;

    @FXML private TextField name;
    @FXML private TextField maxParticipants;
    @FXML private TextField time;
    @FXML private DatePicker date;
    @FXML private TextField description;
    @FXML private Button createEventButton, addPhotoButton;
    @FXML private ImageView eventImage;
    final FileChooser fileChooser = new FileChooser();
    private String base64 = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BooleanBinding booleanBind = name.textProperty().isEmpty()
                .or(maxParticipants.textProperty().isEmpty())
                .or(time.textProperty().isEmpty())
                .or(date.valueProperty().isNull())
                .or(description.textProperty().isEmpty());

        createEventButton.disableProperty().bind(booleanBind);
        validateText(time,"^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$");

        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG);

        addPhotoButton.setOnAction((event) -> {
            File file = fileChooser.showOpenDialog(null);

            if (file != null) {
                Image img = new Image(file.toURI().toString());
                eventImage.setImage(img);

                try {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    base64 = Base64.getEncoder().encodeToString(fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        createEventButton.setOnAction((event) -> {
            try {
                createNewEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createNewEvent() throws IOException {
        // pripojenie
        String url = "http://localhost:8080/api/event/create";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // definovanie typu POST / PUT / GET a jazyka JSON
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        // json objekt, ktory sa posiela
        JSONObject mainObject = new JSONObject();
        mainObject.put("name", name.getText());
        mainObject.put("description", description.getText());
        String str = date.getValue() + " " + time.getText();
        mainObject.put("expiration_date", str);
        mainObject.put("max_participate", Integer.parseInt(maxParticipants.getText()));
        mainObject.put("interest_id", 1);
        mainObject.put("title_photo", base64);

        JSONObject tokenObject = new JSONObject();
        tokenObject.put("jwt", LoginController.getToken());

        con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(mainObject.toString());
        wr.flush();
        wr.close();


        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }


        in.close();
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray nameArray = jsonObject.names();
            JSONArray tokenJSON = jsonObject.toJSONArray(nameArray);
            String token = tokenJSON.toString();
        } catch (JSONException e) {
            System.out.println("");
        }

        switchToHomeScreen();
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
