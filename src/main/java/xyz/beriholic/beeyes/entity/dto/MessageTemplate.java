package xyz.beriholic.beeyes.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class MessageTemplate {
    private String title;
    private String content;
    private Date createdAt;
}
