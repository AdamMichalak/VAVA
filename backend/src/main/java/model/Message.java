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
public class Message {
	private Integer id;
	private Integer creator_id;
	private String text;
	private Date created_at;
	private Integer event_id;
	private String first_name;
	private String last_name;
}

