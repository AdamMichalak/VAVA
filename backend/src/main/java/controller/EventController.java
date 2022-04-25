package controller;

import io.jsonwebtoken.Claims;
import middleware.DB;
import model.EventDetail;
import model.Interest;
import model.MyUserSecurity;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import request.CreateEvent;
import request.MakeParticipation;
import request.BrowseEvents;
import util.JwtUtil;
import util.LoggingUtil;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;


@RestController
@RequestMapping("/api/event")
public class EventController {

	@Autowired
	private JwtUtil jwtTokenUtil;

	@GetMapping("/browse")
	public ResponseEntity<Object> browse(@RequestParam(required = false, defaultValue = "_") String name,
										 @RequestParam(required = false, defaultValue = "01.01.2100") String exp_date,
										 @RequestParam(required = false) String[] interests_id,
										 @RequestParam Integer page)
	{
		//if(!result) return Response.exception();
		Object result = DB.get_events(name, exp_date, interests_id, page);
		if(result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return ResponseEntity.ok(result.toString());
	}

	@GetMapping("/page_count")
	public ResponseEntity<Object> page_count(@RequestParam(required = false, defaultValue = "_") String name,
										 @RequestParam(required = false, defaultValue = "01.01.2100") String exp_date,
										 @RequestParam(required = false) String[] interests_id)
	{
		Object result = DB.get_page_count(name, exp_date, interests_id);
		if(result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		return ResponseEntity.ok(result.toString());
	}

	@GetMapping("/interests")
	public ResponseEntity<Object> get_interests(){
		ArrayList<Interest> interests = DB.get_interest();
		if(interests == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(interests);
	}

	@PostMapping("/create")
	public ResponseEntity<Object> create_event(@RequestBody @Valid CreateEvent request,
											   @RequestHeader("Authorization") String token){
		request.setCreator_id(jwtTokenUtil.extractId(token.substring(7)));
		if(!DB.create_event(request)) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}

	@PostMapping("/participate")
	public ResponseEntity<Object> make_participation(@RequestBody @Valid MakeParticipation request,
											   @RequestHeader("Authorization") String token){
		request.setUser_id(jwtTokenUtil.extractId(token.substring(7)));
		if(!DB.make_participation(request)) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}

	@GetMapping("/detail")
	public ResponseEntity<Object> get_event_detail(@RequestParam Integer event_id, @RequestHeader("Authorization") String token){
		Integer user_id = (jwtTokenUtil.extractId(token.substring(7)));
		EventDetail model = DB.get_event_detail(event_id, user_id);
		if(model==null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(model);
	}

	@GetMapping("/messages")
	public ResponseEntity<Object> get_messages(@RequestParam Integer event_id){
		Object response = DB.get_messages(event_id);
		if(response==null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping()
	public ResponseEntity<Object> delete_event(@RequestParam(value = "id") Integer id,
											   @RequestHeader("Authorization") String token){
		Integer user_id = (jwtTokenUtil.extractId(token.substring(7)));
		String authorities = (jwtTokenUtil.extract_authorities(token.substring(7)));
		String filters = null;
		if(authorities.contains("ROLE_ADMIN")) filters = String.format("id=%d", id);
		else filters = String.format("id=%d and creator_id=%d;", id, user_id);
		int result = DB.remove_event(filters);
		if (result==0) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		else if(result>0) return new ResponseEntity<>(null, HttpStatus.OK);
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PutMapping()
	public ResponseEntity<Object> update_event(@RequestBody @Valid CreateEvent request,
											   @RequestHeader("Authorization") String token){
		Integer user_id = (jwtTokenUtil.extractId(token.substring(7)));
		String authorities = (jwtTokenUtil.extract_authorities(token.substring(7)));
		String filters = null;
		if(authorities.contains("ROLE_ADMIN")) filters = String.format("id=%d", request.getId());
		else filters = String.format("id=%d and creator_id=%d;", request.getId(), user_id);
		int result = DB.update_event(request, filters);
		if (result==0) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		else if(result>0) return new ResponseEntity<>(null, HttpStatus.OK);
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

