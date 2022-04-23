package controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import io.jsonwebtoken.Claims;
import model.MyUserSecurity;
import model.User;
import netscape.javascript.JSObject;
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
import response.Response;
import request.RegisterUser;
import javax.validation.Valid;
import middleware.DB;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.ParseException;
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
	public Response<Object> register(@Valid @RequestBody RegisterUser register_user) {
		int strength = 10; // work factor of bcrypt
		BCryptPasswordEncoder bCryptPasswordEncoder =
				new BCryptPasswordEncoder(strength, new SecureRandom());
		String encodedPassword = bCryptPasswordEncoder.encode(register_user.getPassword());
		register_user.setPassword(encodedPassword);
		boolean result = DB.create_user(register_user);
		if(!result) return Response.exception();
		return Response.ok();
	}

	@GetMapping("/hello")
	public String hello(){
		return "Hello";
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
				.put("jwt", jwt).toString();
		return ResponseEntity.ok(jwt_str);
	}

	@GetMapping("/info/token")
	public ResponseEntity<Object> get_user_info_by_token(@RequestHeader("Authorization") String token){
		System.out.println("token = " + token);
		Integer user_id = jwtTokenUtil.extractId(token.substring(7));
		User user_model = DB.get_user_by_id(user_id);
		return ResponseEntity.ok(user_model);
	}

}
