package model;

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
public class EventDetail{
	private Integer id;
	private String name;
	private String description;
	private String title_photo;
	private Integer max_participate;
	private Timestamp created_at;
	private Timestamp updated_at;
	private Timestamp expiration_date;
	private String interest_name;
	private String first_name;
	private String last_name;
	private Integer participation_count;
	private Integer me_participate;
	private boolean me_owner;
}
