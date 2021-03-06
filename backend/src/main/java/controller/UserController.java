package controller;

import model.MyUserSecurity;
import model.User;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import request.RegisterUser;
import javax.validation.Valid;
import middleware.DB;

import java.security.SecureRandom;

import util.JwtUtil;
import service.MyUserDetailsService;
import request.LoginUser;


@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@PostMapping("/register")
	public ResponseEntity<Object> register(@Valid @RequestBody RegisterUser register_user) {
		int strength = 10; // work factor of bcrypt
		BCryptPasswordEncoder bCryptPasswordEncoder =
				new BCryptPasswordEncoder(strength, new SecureRandom());
		String encodedPassword = bCryptPasswordEncoder.encode(register_user.getPassword());
		register_user.setPassword(encodedPassword);
		User user = DB.get_user_by_email(register_user.getEmail());
		if(user!=null){
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		boolean result = DB.create_user(register_user);
		if(!result) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}


	@PostMapping("/login")
	public ResponseEntity<Object> createAuthenticationToken(@RequestBody LoginUser loginUser) throws Exception {
		System.out.println("result = " + loginUser.getEmail());
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginUser.getEmail(), loginUser.getPassword())
			);
		}
		catch (BadCredentialsException e) {
			return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
		}
		MyUserSecurity user = (MyUserSecurity) userDetailsService
				.loadUserByUsername(loginUser.getEmail());
		final String jwt = jwtTokenUtil.generateToken((UserDetails) user, user.getId());
		String jwt_str = new JSONObject()
				.put("jwt", jwt).put("authorities", user.getAuthorities()).toString();


		return ResponseEntity.ok(jwt_str);
	}

	@GetMapping("/info/token")
	public ResponseEntity<Object> get_user_info_by_token(@RequestHeader("Authorization") String token){
		System.out.println("token = " + token);
		Integer user_id = jwtTokenUtil.extractId(token.substring(7));
		User user_model = DB.get_user_by_id(user_id);
		return ResponseEntity.ok(user_model);
	}

	@GetMapping("/participations")
	public ResponseEntity<Object> get_user_event_participation(@RequestHeader("Authorization") String token){
		Integer user_id = jwtTokenUtil.extractId(token.substring(7));
		JSONObject user_model = DB.get_events_by_user_participation(user_id);
		return ResponseEntity.ok(user_model.toString());
	}
}
