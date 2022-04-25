package com.application.frontend_;

import javafx.beans.binding.BooleanBinding;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static com.application.frontend_.Validation.passwordMatch;
import static com.application.frontend_.Validation.validateText;


public class RegisterController extends SwitchScenes{
    @FXML private VBox system;

    @FXML private TextField firstname;
    @FXML private TextField surname;
    @FXML private TextField email;
    @FXML private TextField isic;
    @FXML private TextField address;
    @FXML private TextField password;
    @FXML private TextField password2;
    @FXML private DatePicker birthDate;
    @FXML private VBox gender;
    @FXML private Button signUpButton, backButton;

    @FXML
    private ComboBox<String>
            genderComboBox;

    ObservableList<String>
            genders = FXCollections.observableArrayList(
            "Žena", "Muž", "Xor");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        genderComboBox.setItems(genders);

        BooleanBinding booleanBind = firstname.textProperty().isEmpty()
                .or(surname.textProperty().isEmpty())
                .or(address.textProperty().isEmpty())
                .or(address.textProperty().isEmpty())
                .or(isic.textProperty().isEmpty())
                .or(password.textProperty().isEmpty())
                .or(password2.textProperty().isEmpty())
                .or(birthDate.valueProperty().isNull())
                .or(genderComboBox.valueProperty().isNull());

        signUpButton.disableProperty().bind(booleanBind);

        validateText(isic,  "[\\D][0-9]{12}[\\D]");
        validateText(email,  "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        passwordMatch(password, password2);

        backButton.setOnAction((e) -> goBack());
        signUpButton.setOnAction((event) -> {
            try {
                register();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void register() throws IOException {
        // pripojenie
        String url = "http://localhost:8080/api/user/register";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // definovanie typu POST / PUT / GET a jazyka JSON
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        // json objekt, ktory sa posiela
        JSONObject mainObject = new JSONObject();
        System.out.print(firstname.getText());
        mainObject.put("first_name", firstname.getText());
        mainObject.put("last_name", surname.getText());
        mainObject.put("email", email.getText());
        mainObject.put("isic_number", isic.getText());
        mainObject.put("location", address.getText());
        mainObject.put("password", password.getText());
        //mainObject.put("password2", password2.getText());

        mainObject.put("date_of_birth", birthDate.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        if (genderComboBox.getValue() == "Muž"){
            mainObject.put("gender_id", 1);
        } else {
            mainObject.put("gender_id", 2);
        }

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
        String token = tokenJSON.toString();

        switchToLoginScreen();
    }

    public void goBack() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("welcome.fxml")));
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

}
