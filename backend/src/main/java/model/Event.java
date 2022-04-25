package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Event{
	private Integer id;
	private Integer creator_id;
	private String name;
	private Integer interest_id;
	private String description;
	private String title_photo;
	private Integer max_participate;
	private String last_name;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp expiration_date;
}
