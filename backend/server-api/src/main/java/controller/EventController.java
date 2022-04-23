package controller;

import middleware.DB;
import model.EventDetail;
import model.Interest;
import model.MyUserSecurity;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import request.CreateEvent;
import request.MakeParticipation;
import request.BrowseEvents;
import util.JwtUtil;

import javax.validation.Valid;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/event")
public class EventController {

	@Autowired
	private JwtUtil jwtTokenUtil;

	@GetMapping("/browse")
	public ResponseEntity<Object> browse(@Valid @RequestBody BrowseEvents event) {
		//if(!result) return Response.exception();
		ResultSet result = DB.get_events(event);
		if(result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		try {
			if(result.next())
			{
				return ResponseEntity.ok(result.getObject(1));
			}
		}
		catch(SQLException e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return ResponseEntity.ok(true);
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
		Object response = DB.get_event_detail(event_id, user_id);
		if(response==null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/messages")
	public ResponseEntity<Object> get_messages(@RequestParam Integer event_id){
		Object response = DB.get_messages(event_id);
		if(response==null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(response);
	}
}

