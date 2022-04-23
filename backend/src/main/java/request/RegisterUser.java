package request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterUser {
	@NotEmpty(message = "first name has to be presented")
	private String first_name;

	@NotEmpty(message = "last name has to be presented")
	private String last_name;

	@NotEmpty(message = "email has to be presented")
	@Email(message = "email in wrong format")
	private String email;

	@NotEmpty(message = "date of birth should be presented")
	private String date_of_birth;

	@NotEmpty(message = "password has to be presented")
	private String password;

	@NotEmpty(message = "location has to be presented")
	private String location;

	@NotNull(message = "gender id should be presented")
	private Integer gender_id;

	@NotEmpty(message = "isic has to be presented")
	private String isic_number;

}

