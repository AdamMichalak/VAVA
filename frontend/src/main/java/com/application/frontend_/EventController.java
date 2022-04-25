package com.application.frontend_;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class EventController extends SwitchScenes {
	@FXML private TextArea messageArea;
	@FXML
	private ScrollPane messages_pane;
	@FXML
    private VBox system;

	@FXML private Button sendMessage;

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


	public void sendMessage() throws IOException {
		// pripojenie
		String url = "http://localhost:8080/api/message/create";
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
		mainObject.put("text", messageArea.getText().replaceAll("\n", System.getProperty("line.separator")));

		// poslanie json objektu pomocou WR
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(mainObject.toString());
		wr.flush();
		wr.close();

		// ziskanie response code 2xx 3xx 4xx a vypisanie spravy
		int responseCode = con.getResponseCode();

		if (responseCode == 201) {
			messageArea.clear();
			getEventMessages();
		} else {
			showMessageDialog(null, "Niečo sa pokazilo");
		}

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

	public void getEventMessages() throws IOException {
		String url = "http://localhost:8080/api/event/messages?event_id="+currentEventId;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection)obj.openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Content-Type","application/json");
		con.setUseCaches(false);
		con.setAllowUserInteraction(false);
		con.setRequestProperty ("Authorization", "Bearer " + LoginController.getToken());
		con.connect();

		if(con.getResponseCode() == 200) {
			System.out.println("con.getResponseCode() = " + con.getResponseCode());
			Scanner scan = new Scanner(con.getInputStream());
			if(scan.hasNext()) {
				String temp = scan.nextLine();
				JSONArray arr = new JSONArray(temp.toString());
				VBox message_list = new VBox();
				arr.forEach(item -> {
					JSONObject message = (JSONObject) item;
					HBox message_box = new HBox();
					VBox message_info_box = new VBox();
					Text message_author = new Text("Napísal: "+message.getString("first_name")+" "+message.getString("last_name"));
					LocalDateTime datetime = ParseDate.parseDateFromDBToLocalDateTime(message.getString("created_at"));
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
					Text message_created_at = new Text("Vytvorené: " + formatter.format(datetime));
					TextFlow textFlow = new TextFlow();
					textFlow.setStyle("-fx-padding: 5px;");
					Text message_content = new Text(message.getString("text"));
					textFlow.getChildren().add(message_content);
					message_box.setMinHeight(58.00);
					message_box.setMinWidth(710);
					message_box.setStyle("-fx-border-color: #000066; -fx-border-width: 0 0 1 0;");
					message_info_box.setAlignment(Pos.CENTER_LEFT);
					message_info_box.setMinWidth(250);
					message_info_box.setPadding(new Insets(5, 5, 5, 5));
					message_info_box.setStyle("-fx-border-color: #000066; -fx-border-width: 0 1 0 0;");
					message_info_box.getChildren().addAll(message_author, message_created_at);
					message_box.getChildren().addAll(message_info_box, textFlow);
					message_list.getChildren().add(message_box);
				});
				messages_pane.setContent(message_list);
				messages_pane.setVvalue(1);
			}
		}

//		VBox eventsList = new VBox();
//		eventsList.setSpacing(10);
//		eventsList.setPadding(new Insets(20, 20, 20, 20));
//		for (Object js : list) {
//			JSONObject event = (JSONObject) js;
//
//			HBox eventBox = new HBox();
//			Label eventName = new Label();
//			HBox eventNameContainer = new HBox();
//			eventNameContainer.setMinWidth(510);
//			eventName.setText(event.getString("name"));
//			eventName.setPadding(new Insets(5));
//			eventName.setStyle("-fx-font-size: 14; -fx-font-weight: 700; -fx-background-color: #cdd0d4; -fx-background-radius: 4px;");
//			eventNameContainer.getChildren().add(eventName);
//
//			Button eventBtn = new Button("Zobraziť detaily udalosti");
//			eventBtn.setPadding(new Insets(5, 25, 5, 25));
//			eventBtn.setStyle("-fx-background-color: #000066; -fx-font-size: 13px; -fx-font-weight: bold; -fx-cursor: hand");
//			eventBtn.setTextFill(Paint.valueOf("#fff"));
//			eventBtn.setOnAction((e) -> {
//				currentEventId = event.getInt("id");
//				showInfoEvent();
//			});
//
//			eventBox.getChildren().addAll(eventNameContainer, eventBtn);
//			eventsList.getChildren().add(eventBox);
//		}
//
//		myEvents.setContent(eventsList);
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
		sendMessage.setOnAction((event -> {
			try {
				sendMessage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}));
        try{
            getItems();
        }catch (IOException e) {
            e.printStackTrace();
        }
		try{
			getEventMessages();
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
