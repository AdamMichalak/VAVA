package model;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;

public class MyUserSecurity extends User {
	private final Integer id;


	public MyUserSecurity(String username, String password, Integer id, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}
}
