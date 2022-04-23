package model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class UserRole {
	private Integer user_id;
	private Integer role_id;
	private String role_name;
	private String email;
	private String first_name;
	private String last_name;
}
