package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CreateEventController extends SwitchScenes {
    @FXML
    private VBox system;

    @FXML private TextField nazov;
    @FXML private TextField email;
    @FXML private TextField timePicker;
    @FXML private DatePicker datePicker;
    @FXML private TextField eventAddress;
    @FXML private TextField description;

    @FXML private Button createEventButton;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        //System.out.print(nazov.getText());
        mainObject.put("name", nazov.getText());
        mainObject.put("description", description.getText());
        //mainObject.put("email", email.getText());
        //mainObject.put("expiration_date2", timePicker.getText());
        mainObject.put("expiration_date", datePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        //mainObject.put("address", eventAddress.getText());
        mainObject.put("max_participate", 5);
        mainObject.put("interest_id", 1);
        LoginController trieda = new LoginController();
        String token1 = trieda.getToken();
        // [" token "]
        token1 = token1.substring(2);
        token1 = token1.substring(0, token1.length() - 1);
        token1 = token1.substring(0, token1.length() - 1);
        System.out.print("tokeeeen: " + token1);
        JSONObject tokenObject = new JSONObject();
        tokenObject.put("jwt", token1);
        //con.setRequestProperty ("Authorization", "Bearer " + token1.toString());
        con.setRequestProperty ("Authorization", "Bearer "+token1.toString());
        // Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbm5hLnlAZ21haWwuY29tIiwiaWQiOjksImV4cCI6MTY1MDUwMDQyMCwiaWF0IjoxNjUwNDY0NDIwfQ.pEMCiL3oyCujjltAR5AbixNFZIvGx3qMbbahnNIFXnA
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
        try {
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONArray nameArray = jsonObject.names();
            JSONArray tokenJSON = jsonObject.toJSONArray(nameArray);
            String token = tokenJSON.toString();
        } catch (JSONException e) {
            System.out.println("");
            //e.printStackTrace();
        }

        switchToHomeScreen();
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
