package ru.emelkrist.dto;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = {"current", "isInputting"})
public class RequestDTO {
    private String from;
    private String codeFrom;
    private String to;
    private String codeTo;
    private String date;
    private int current;
    private boolean isInputting = false;
}
