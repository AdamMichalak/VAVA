package middleware;

import com.sun.jdi.IntegerType;
import com.sun.tools.javac.Main;
import model.*;
import request.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static ResultSet get_events(BrowseEvents event)
	{
		try{
			if(connection==null) connect();
		} catch (SQLException e){
			return null;
		}
		String sql =	"SELECT json_build_object( 'current_page', ?, 'page_count', ceil, 'events', json_agg(event_objects))" +
						"FROM ( SELECT ceil(COUNT(*) over(PARTITION BY filler)/4.0), row_to_json event_objects FROM(" +
						"SELECT row_to_json(events), 1 as filler FROM events" +
						" JOIN interests i on interest_id = i.id" +
						" JOIN users u on creator_id = u.id" +
						" WHERE expiration_date > NOW() AND expiration_date < ?" +
						" AND NAME ILIKE ?";
		if(event.getInterest_id() != null) {
			String sql_array = " AND interest_id  IN (";
			for (Integer interest : event.getInterest_id()) {
				sql_array += "?,";
			}
			sql_array = sql_array.substring(0, sql_array.length() - 1) + ")";
			sql += sql_array;
		}
		sql +=	" ORDER BY expiration_date" +
				") json_objects OFFSET ? LIMIT 4) count_limit GROUP BY ceil";
		PreparedStatement statement;
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			statement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			return null;
		}
		try {
			statement.setInt(1,event.getPage());
			statement.setDate(2,new java.sql.Date(formatter.parse(event.getExpiration_date()).getTime()));
			statement.setString(3,"%"+event.getName()+"%");
			int count = 4;
			if(event.getInterest_id() != null)
			{
				for(Integer interest : event.getInterest_id())
				{
					statement.setInt(count,interest);
					count++;
				}
			}
			statement.setInt(count,4*(event.getPage()-1));
		} catch (SQLException | ParseException e) {
			return null;
		}
		try {
			ResultSet result = statement.executeQuery();
			return result;
		} catch (SQLException e) {
			System.out.println(e);
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
					e.printStackTrace();
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
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			String sql = "INSERT INTO events (creator_id, name, interest_id, description, title_photo, max_participate, created_at, updated_at, expiration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, request.getCreator_id());
			statement.setString(2, request.getName());
			statement.setInt(3, request.getInterest_id());
			statement.setString(4, request.getDescription());
			statement.setString(5, request.getTitle_photo());
			statement.setInt(6, request.getMax_participate());
			statement.setDate(7, new java.sql.Date(new java.util.Date().getTime()));
			statement.setDate(8, new java.sql.Date(new java.util.Date().getTime()));
			statement.setDate(9, new java.sql.Date(formatter.parse(request.getExpiration_date()).getTime()));
			statement.execute();
			return true;
		}
		catch (SQLException | ParseException e){
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

	public static Object get_event_detail(Integer event_id, Integer user_id){
		try {
			if (connection == null) connect();
		} catch (SQLException e) {
			return null;
		}
		String sql = "SELECT events.id, creator_id, name, description, title_photo, max_participate, created_at, updated_at, expiration_date,\n" +
				"i.interest_name, u.email, u.first_name, u.last_name, (SELECT count(id) FROM participation p where p.event_id=?) participation_count,\n" +
				"(SELECT count(id) FROM participation p where p.user_id=?) me_participate\n" +
				"FROM events JOIN interests i on events.interest_id = i.id\n" +
				"JOIN users u on events.creator_id = u.id\n" +
				"WHERE events.id=?";
		try{
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, event_id);
			statement.setInt(2, user_id);
			statement.setInt(3, event_id);
			ResultSet rs =  statement.executeQuery();
			return resultset_to_model(rs, EventDetail.class).get(0);


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



}
