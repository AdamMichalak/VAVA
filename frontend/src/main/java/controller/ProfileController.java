package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController extends SwitchScenes {
    @FXML
    private VBox system;

    @FXML private Button backToHome;

    @FXML private Button signUpHome;

    @FXML private Button signInHome;

    @FXML private Button createEvent;

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
        LoginController trieda = new LoginController();
        String token1 = trieda.getToken();
        // [" token "]
        token1 = token1.substring(2);
        token1 = token1.substring(0, token1.length() - 1);
        token1 = token1.substring(0, token1.length() - 1);
        //con.setRequestProperty ("Authorization", "Bearer " + token1.toString());
        con.setRequestProperty ("Authorization", "Bearer "+ token1.toString());
        con.connect();
        //con.setDoOutput(true);
        //DataOutputStream wr = new DataOutputStream(con.getOutputStream());

        //JSONObject mainObject = new JSONObject();
        ///wr.writeBytes(tokenObject.toString());
        //wr.flush();
        //wr.close();
        //System.out.println(con.getResponseMessage());
        System.out.println(con.getContent());
        //System.out.println(con.getOutputStream());
        int responseCode = con.getResponseCode();
        //System.out.println("Sending 'POST' request to URL : " + url);
        //System.out.println("GET Data : " + mainObject);
        System.out.println("Response Code : " + responseCode);
    }

    public void initialize(URL url, ResourceBundle resourceBundl) {
        backToHome.setOnAction((event) -> back());
        signUpHome.setOnAction((event) -> register());
        signInHome.setOnAction((event) -> switchToLoginScreen());
        createEvent.setOnAction((event) -> createNewEvent());
        try{
            getItems();
        }catch (IOException e) {
            e.printStackTrace();
        }

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
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/home.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/register.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLoginScreen() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/login.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNewEvent() {
        try {
            system.getChildren().clear();
            system.getChildren().add(FXMLLoader.load(getClass().getResource("../view/createEvent.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}