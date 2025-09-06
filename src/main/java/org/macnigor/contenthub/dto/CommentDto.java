package org.macnigor.contenthub.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentDto {
    private String username;
    private String message;
}
