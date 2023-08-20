package ru.emelkrist.dto;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = {"current", "isInputting"})
public class RequestDTO {
    private String fromCity;
    private String codeFrom;
    private String toCity;
    private String codeTo;
    private String date;
    private int current;
    private boolean isInputting = false;
}
