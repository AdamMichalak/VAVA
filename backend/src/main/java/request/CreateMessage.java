package request;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.PackagePrivate;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateMessage {
	@NotEmpty(message = "text of the message has to be presented")
	private String text;

	@NotNull(message = "event_id has to be presented")
	@Digits(integer = 10, fraction = 0)
	private Integer event_id;

	@PackagePrivate
	private Integer creator_id;

}

