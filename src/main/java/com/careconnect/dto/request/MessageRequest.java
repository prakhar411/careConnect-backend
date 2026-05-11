package com.careconnect.dto.request;

import lombok.Data;

@Data
public class MessageRequest {
    private Long receiverId;
    private String receiverRole;
    private String content;
}
