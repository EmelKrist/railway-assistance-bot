package ru.emelkrist.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Timetable {
    private String trainNumber;
    private String trainTitle;
    private String trainUid;
    private String fromStationTitle;
    private String fromStationCode;
    private String toStationTitle;
    private String toStationCode;
    private String departure;
    private String arrival;
    private String startDate;
}
