package ru.emelkrist.model;

import lombok.*;


@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = {"current", "isInputting"})
public class RequestData {
    private String from;
    private String to;
    private String date;
    private int current;
    private boolean isInputting = false;
}
