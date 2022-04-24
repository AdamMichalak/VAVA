package middleware;

import com.sun.jdi.IntegerType;
import com.sun.tools.javac.Main;
import model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import request.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class DB {
	private static Connection connection;

	public static void connect() throws SQLException{
		String connection_string = "jdbc:postgresql://vava-do-user-7525330-0.b.db.ondigitalocean.com:25060/vava?sslmode=require";
		String username = "doadmin";
		String password = "AVNS_p5fwdjoerbwW_3P";
		connection = DriverManager.getConnection(connection_string, username, password);
	}

	private static <T> T convertInstanceOfObject(Object o, Class<T> clazz) {
		try {
			return clazz.cast(o);
		} catch(ClassCastException e) {
			if(o instanceof Long && clazz == java.lang.Integer.class){
				return clazz.cast(((Long) o).intValue());
			}

			return null;
		}
	}

	public static JSONObject get_page_count(String name, String exp_date, String[] interests_id)
	{
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return null;
		}
		String sql = 	"SELECT CEIL(COUNT(*)/4.0) as count FROM events" +
						" WHERE expiration_date > NOW() AND expiration_date < ?" +
						" AND NAME ILIKE ?";

		if(interests_id != null) {
			String sql_array;
			sql_array = " AND interest_id  IN (";
			for (String interest : interests_id) {
				sql_array += "?,";
			}
			sql_array = sql_array.substring(0, sql_array.length() - 1) + ")";
			sql += sql_array;
		}
		PreparedStatement statement;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return null;
		}
		try {
			statement.setTimestamp(1,new java.sql.Timestamp(formatter.parse(exp_date).getTime()));
			statement.setString(2,"%"+name+"%");
			int count = 3;
			if(interests_id != null)
			{
				for(String interest :interests_id)
				{
					statement.setInt(count,Integer.parseInt(interest));
					count++;
				}
			}
		} catch (SQLException | ParseException e) {
			return null;
		}
		try {
			ResultSet result = statement.executeQuery();
			JSONObject ret = new JSONObject();
			result.next();
			ret.put("page_count", result.getInt("count"));
			return ret;
		} catch (SQLException e) {
			return null;
		}

	}

	public static JSONObject get_events(String name, String exp_date, String[] interests_id, Integer page)
	{
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return null;
		}
		String sql = 	"SELECT * FROM events" +
				" JOIN interests i on interest_id = i.id" +
				" JOIN users u on creator_id = u.id" +
				" WHERE expiration_date > NOW() AND expiration_date < ?" +
				" AND NAME ILIKE ?";
		String sql_array = "";
		if(interests_id != null) {
			sql_array = " AND interest_id  IN (";
			for (String interest : interests_id) {
				sql_array += "?,";
			}
			sql_array = sql_array.substring(0, sql_array.length() - 1) + ")";
			sql += sql_array;
		}
		sql +=	" ORDER BY expiration_date OFFSET ? LIMIT 4";
		PreparedStatement statement;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return null;
		}
		try {
			statement.setTimestamp(1,new java.sql.Timestamp(formatter.parse(exp_date).getTime()));
			statement.setString(2,"%"+name+"%");
			int count = 3;
			if(interests_id != null)
			{
				for(String interest :interests_id)
				{
					statement.setInt(count,Integer.parseInt(interest));
					count++;
				}
			}
			statement.setInt(count,4*(page-1));
		} catch (SQLException | ParseException e) {
			return null;
		}
		JSONArray events = new JSONArray();
		try {
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				JSONObject tmp = new JSONObject();
				tmp.put("creator_first_name", result.getString("first_name"));
				tmp.put("creator_last_name", result.getString("last_name"));
				tmp.put("creator_id", result.getInt("creator_id"));
				tmp.put("interest_id", result.getInt("interest_id"));
				tmp.put("interest_name", result.getString("interest_name"));
				tmp.put("name", result.getString("name"));
				tmp.put("description", result.getString("description"));
				tmp.put("max_participants", result.getInt("max_participate"));
				System.out.println(result.getTimestamp("expiration_date"));
				tmp.put("expiration_date", result.getTimestamp("expiration_date"));
				tmp.put("created_at", result.getTimestamp("created_at"));
				tmp.put("event_id", result.getInt("id"));

				if (result.getBytes("title_photo") == null) {
					tmp.put("title_photo", "");
				} else {
					tmp.put("title_photo", Base64.getEncoder().encodeToString(result.getBytes("title_photo")));
				}
				events.put(tmp);
			}
		}
		catch (SQLException e) {
			return null;
		}
		sql = 	"SELECT CEIL(COUNT(*)/4.0) as count FROM events" +
				" WHERE expiration_date > NOW() AND expiration_date < ?" +
				" AND NAME ILIKE ?";
		if(interests_id != null) {
			sql += sql_array;
		}
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return null;
		}
		try {
			statement.setTimestamp(1,new java.sql.Timestamp(formatter.parse(exp_date).getTime()));
			statement.setString(2,"%"+name+"%");
			int count = 3;
			if(interests_id != null)
			{
				for(String interest :interests_id)
				{
					statement.setInt(count,Integer.parseInt(interest));
					count++;
				}
			}
		} catch (SQLException | ParseException e) {
			return null;
		}
		try {
			ResultSet result = statement.executeQuery();
			JSONObject ret = new JSONObject();
			result.next();
			ret.put("current_page", page);
			ret.put("page_count", result.getInt("count"));
			ret.put("events", events);
			return ret;
		} catch (SQLException e) {
			return null;
		}
	}

	private static List<Object> resultset_to_model(ResultSet rs, Class model) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Field[] fields = model.getDeclaredFields();
		for(Field field: fields) {
			field.setAccessible(true);
		}
		List<Object> list = new ArrayList<Object>();
		while (rs.next()){
			Object model_instance = model.getConstructor().newInstance();
			for(Field field : fields){
				String name = field.getName();
				try{
					Object value = rs.getObject(name);
					field.set(model_instance, convertInstanceOfObject(value, field.getType()));
				} catch (Exception e) {
				}
			}
			list.add(model_instance);
		}
		return list;
	}

	public static boolean create_user(RegisterUser user){
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return false;
		}
		String sql = "INSERT INTO users (first_name, last_name, email, date_of_birth, password, location, gender_id, isic_number, registered_at, role_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return false;
		}
		try{
			statement.setString(1, user.getFirst_name());
			statement.setString(2, user.getLast_name());
			statement.setString(3, user.getEmail());
			statement.setDate(4, new java.sql.Date(formatter.parse(user.getDate_of_birth()).getTime()));
			statement.setString(5, user.getPassword());
			statement.setString(6, user.getLocation());
			statement.setInt(7, user.getGender_id());
			statement.setString(8, user.getIsic_number());
			statement.setDate(9, new java.sql.Date(new java.util.Date().getTime()));
		} catch (ParseException | SQLException e){
			return false;
		}
		try {
			statement.execute();
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	public static JSONObject get_events_by_user_participation(Integer id) {
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = 	"SELECT name, e.id FROM participation eu JOIN events e ON eu.event_id = e.id WHERE eu.user_id = ?";

		PreparedStatement statement;
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return null;
		}
		try {
			statement.setInt(1, id);
		} catch (SQLException e) {
			return null;
		}
		try {
			ResultSet result = statement.executeQuery();
			JSONObject ret = new JSONObject();
			JSONArray events = new JSONArray();
			while(result.next())
			{
				JSONObject tmp = new JSONObject();
				tmp.put("name", result.getString("name"));
				tmp.put("id", result.getInt("id"));
				events.put(tmp);
			}
			ret.put("events", events);
			return ret;
		} catch (SQLException e) {
			return null;
		}
	}

	public static User get_user_by_email(String email) {
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return null;
		}
		try{
			String sql = ("SELECT * FROM USERS WHERE email = ?");
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();
			User model = new User();
			rs.next();
			model.setId(rs.getInt("id"));
			model.setFirst_name(rs.getString("first_name"));
			model.setLast_name(rs.getString("last_name"));
			model.setEmail(rs.getString("email"));
			model.setPassword(rs.getString("password"));
			model.setDate_of_birth(new java.util.Date(rs.getTimestamp("date_of_birth").getTime()));
			model.setLocation(rs.getString("location"));
			model.setGender_id(rs.getInt("gender_id"));
			model.setIsic_number(rs.getString("isic_number"));
			model.setRegistered_at(new java.util.Date(rs.getTimestamp("registered_at").getTime()));
			return model;
		}
		catch (SQLException e){
			return null;
		}
	}

	public static User get_user_by_id(Integer id) {
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return null;
		}
		try{
			String sql = ("SELECT users.id, users.first_name, users.last_name, users.email,\n" +
					"       users.location, users.date_of_birth, users.password, users.gender_id, \n" +
					"       users.registered_at, users.isic_number, g.gender_name FROM users\n" +
					"    join genders g on g.id = users.gender_id where users.id = ?");
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, id);
			ResultSet rs = statement.executeQuery();
			User model = new User();
			rs.next();
			model.setId(rs.getInt("id"));
			model.setFirst_name(rs.getString("first_name"));
			model.setLast_name(rs.getString("last_name"));
			model.setEmail(rs.getString("email"));
			model.setPassword(rs.getString("password"));
			model.setDate_of_birth(new java.util.Date(rs.getTimestamp("date_of_birth").getTime()));
			model.setLocation(rs.getString("location"));
			model.setGender_id(rs.getInt("gender_id"));
			model.setIsic_number(rs.getString("isic_number"));
			model.setRegistered_at(new java.util.Date(rs.getTimestamp("registered_at").getTime()));
			model.setGender_name(rs.getString("gender_name"));
			return model;
		}
		catch (SQLException e){
			return null;
		}
	}


	public static ArrayList<Interest> get_interest() {
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		try {
			String sql = ("SELECT * FROM interests");
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			ArrayList<Interest> interests = new ArrayList<>();
			while (rs.next()) {
				Interest interest = new Interest();
				interest.setId(rs.getInt("id"));
				interest.setInterest_name(rs.getString("interest_name"));
				interests.add(interest);
			}
			return interests;
		} catch (SQLException e) {
			return null;
		}
	}

	public static boolean create_event(CreateEvent request){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return false;
		}
		try{
			String sql = "INSERT INTO events (creator_id, name, interest_id, description, title_photo, max_participate, created_at, updated_at, expiration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, request.getCreator_id());
			statement.setString(2, request.getName());
			statement.setInt(3, request.getInterest_id());
			statement.setString(4, request.getDescription());
			if (request.getTitle_photo() == null) {
				statement.setBytes(5,null);
			} else {
				statement.setBytes(5, Base64.getDecoder().decode(request.getTitle_photo()));
			}
			statement.setInt(6, request.getMax_participate());
			statement.setTimestamp(7, new java.sql.Timestamp(new java.util.Date().getTime()));
			statement.setTimestamp(8, new java.sql.Timestamp(new java.util.Date().getTime()));
			DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(request.getExpiration_date(), frm);
			statement.setTimestamp(9, java.sql.Timestamp.valueOf(dateTime));
			statement.execute();
			return true;
		}
		catch (SQLException e){
			return false;
		}
	}

	public static boolean create_message(CreateMessage request){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return false;
		}
		try{
			String sql = "INSERT INTO MESSAGES (creator_id, text, created_at, event_id) VALUES (?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, request.getCreator_id());
			statement.setString(2, request.getText());
			statement.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			statement.setInt(4, request.getEvent_id());
			statement.execute();
			return true;
		}
		catch (SQLException e){
			return false;
		}
	}
	public static boolean make_participation(MakeParticipation request){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return false;
		}
		try{
			String sql = "INSERT INTO PARTICIPATION (user_id, event_id) VALUES (?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, request.getUser_id());
			statement.setInt(2, request.getEvent_id());
			statement.execute();
			return true;
		}
		catch (SQLException e){
			return false;
		}

	}

	public static EventDetail get_event_detail(Integer event_id, Integer user_id){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = "SELECT events.id, creator_id, name, description, title_photo, max_participate, created_at, updated_at, expiration_date,\n" +
				"i.interest_name, u.email, u.first_name, u.last_name, (SELECT count(id) FROM participation p where p.event_id=?) participation_count,\n" +
				"(SELECT count(id) FROM participation p where p.user_id=? and p.event_id=?) me_participate,\n"+
				"(CASE WHEN (creator_id=?) THEN true ELSE false END) as me_owner\n"+
				"FROM events JOIN interests i on events.interest_id = i.id\n" +
				"JOIN users u on events.creator_id = u.id\n" +
				"WHERE events.id=?";
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, event_id);
			statement.setInt(2, event_id);
			statement.setInt(3, user_id);
			statement.setInt(4, user_id);
			statement.setInt(5, event_id);
			ResultSet rs =  statement.executeQuery();

			rs.next();
			EventDetail model = new EventDetail();
			model.setId(rs.getInt("id"));
			model.setName(rs.getString("name"));
			model.setDescription(rs.getString("description"));

			if (rs.getBytes("title_photo") == null) {
				model.setTitle_photo("");
			} else {
				model.setTitle_photo(Base64.getEncoder().encodeToString(rs.getBytes("title_photo")));
			}

			model.setMax_participate(rs.getInt("max_participate"));
			model.setCreated_at(rs.getTimestamp("created_at"));
			model.setUpdated_at(rs.getTimestamp("updated_at"));
			System.out.println(rs.getTimestamp("expiration_date"));
			model.setExpiration_date(rs.getTimestamp("expiration_date"));
			model.setInterest_name(rs.getString("interest_name"));
			model.setFirst_name(rs.getString("first_name"));
			model.setLast_name(rs.getString("last_name"));
			model.setParticipation_count(rs.getInt("participation_count"));
			model.setMe_participate(rs.getInt("me_participate"));
			model.setMe_owner(rs.getBoolean("me_owner"));

			return model;


		} catch (Exception e){
			return null;
		}
	}

	public static List<Message> get_messages(Integer event_id){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = "SELECT messages.id, messages.creator_id, messages.text, messages.event_id, messages.created_at, u.first_name, u.last_name FROM messages\n" +
				"JOIN users u on messages.creator_id = u.id WHERE event_id = ? ORDER BY messages.created_at;";
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, event_id);
			ResultSet rs =  statement.executeQuery();
			return (List<Message>)(Object)resultset_to_model(rs, Message.class);
		} catch (Exception e){
			return null;
		}
	}

	public static List<UserRole> get_roles(Integer user_id){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = "SELECT user_id, role_id, role_name FROM user_roles join role r on user_roles.role_id = r.id\n" +
				"join users u on user_roles.user_id = u.id\n" +
				"where user_id=?";
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, user_id);
			ResultSet rs =  statement.executeQuery();
			return (List<UserRole>)(Object)resultset_to_model(rs, UserRole.class);
		} catch (Exception e){
			return null;
		}
	}

	public static List<UserRole> get_all_roles(){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = "SELECT user_id, role_id, role_name FROM user_roles join role r on user_roles.role_id = r.id\n" +
				"join users u on user_roles.user_id = u.id;";
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs =  statement.executeQuery();
			return (List<UserRole>)(Object)resultset_to_model(rs, UserRole.class);
		} catch (Exception e){
			return null;
		}
	}

	public static int remove_event(String filters){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return -1;
		}
		String sql = "DELETE FROM events where "+filters;
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			return statement.executeUpdate();
		} catch (Exception e){
			return -1;
		}
	}

	public static int update_event(CreateEvent request, String filters){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return -1;
		}
		try{
			String sql = "UPDATE events SET name = ?, interest_id = ?, description = ?, title_photo = ?, max_participate = ?, updated_at = ?, expiration_date = ? WHERE "+filters;
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, request.getName());
			statement.setInt(2, request.getInterest_id());
			statement.setString(3, request.getDescription());
			if (request.getTitle_photo() == null) {
				statement.setBytes(4,null);
			} else {
				statement.setBytes(4, Base64.getDecoder().decode(request.getTitle_photo()));
			}
			statement.setInt(5, request.getMax_participate());
			statement.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
			DateTimeFormatter frm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(request.getExpiration_date(), frm);
			statement.setTimestamp(7, java.sql.Timestamp.valueOf(dateTime));
			return statement.executeUpdate();
		}
		catch (SQLException e){
			return -1;
		}
	}



}
