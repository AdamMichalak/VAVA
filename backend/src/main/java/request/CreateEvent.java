package request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateEvent {
	@NotEmpty(message = "name has to be presented")
	private String name;

	@NotNull(message = "interest has to be presented")
	@Digits(integer = 10, fraction = 0)
	private Integer interest_id;

	@NotEmpty(message = "description has to be presented")
	private String description;

	private String title_photo;

	private Integer max_participate;

	private String expiration_date;

	@PackagePrivate
	private Integer creator_id;

	@PackagePrivate
	private Integer id;

}

