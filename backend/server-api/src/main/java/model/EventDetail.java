package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

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
	private Date created_at;
	private Date updated_at;
	private Date expiration_date;
	private String interest_name;
	private String first_name;
	private String last_name;
	private Integer participation_count;
	private Integer me_participate;
}
