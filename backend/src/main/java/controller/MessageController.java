package controller;

import middleware.DB;
import model.Interest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.CreateMessage;
import util.JwtUtil;

import javax.validation.Valid;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/message")
public class MessageController {

	@Autowired
	private JwtUtil jwtTokenUtil;


	@PostMapping("/create")
	public ResponseEntity<Object> create_event(@RequestBody @Valid CreateMessage request,
											   @RequestHeader("Authorization") String token){
		request.setCreator_id(jwtTokenUtil.extractId(token.substring(7)));
		if(!DB.create_message(request)) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}

}

