package ca.sheridancollege.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
	
	private String title;
	private String author;
	private Long id;

}
