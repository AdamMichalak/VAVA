package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class User{
	@JsonIgnore
	private Integer id;
	private String first_name;
	private String last_name;
	@JsonIgnore
	private String password;
	private String email;
	private Date date_of_birth;
	private String location;
	private Integer gender_id;
	private Date registered_at;
	private String isic_number;
	private String gender_name;
}
