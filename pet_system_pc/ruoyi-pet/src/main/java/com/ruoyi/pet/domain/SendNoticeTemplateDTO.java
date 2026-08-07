package com.ruoyi.pet.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNoticeTemplateDTO {

    @JsonProperty(required = false)
    private Long templateId;

    @JsonProperty(required = false)
    private String title;

    @JsonProperty(required = false)
    private String content;

    @JsonProperty(required = true)
    private Long userId;

    @JsonProperty(required = false)
    private Long petId;

    @JsonProperty(required = false)
    private Long boardingId;

    @JsonProperty(required = false)
    private Long rescueId;


}
