package service;

import middleware.DB;
import model.MyUserSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		model.User user_model = DB.get_user_by_email(email);
		if(user_model == null){
			throw new UsernameNotFoundException("User not found");
		}
        return new MyUserSecurity(user_model.getEmail(), user_model.getPassword(), user_model.getId(),
              getRoles(user_model));
    }

	private Set<? extends GrantedAuthority> getRoles(model.User user){
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
		(Objects.requireNonNull(DB.get_roles(user.getId()))).forEach(role -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRole_name()));
		});
		return authorities;
	}
}
