package ru.emelkrist.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "timetable")
@EqualsAndHashCode
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String trainNumber;
    private String trainTitle;
    private String trainUid;
    private String fromStationTitle;
    private String fromStationCode;
    private String toStationTitle;
    private String toStationCode;
    private String departure;
    private String arrival;
    private String days;
    @ManyToOne
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    private Response response;
}
