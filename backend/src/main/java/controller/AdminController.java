package controller;

import middleware.DB;
import model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.CreateMessage;
import util.JwtUtil;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private JwtUtil jwtTokenUtil;


	@GetMapping("/roles")
	public ResponseEntity<Object> get_roles(){
		List<UserRole> roles = DB.get_all_roles();
		if(roles==null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.ok(roles);
	}

}

