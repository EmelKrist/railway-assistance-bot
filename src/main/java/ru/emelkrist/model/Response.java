package ru.emelkrist.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "response")
@ToString(exclude = "id")
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long chatId;
    private int messageId;
    private String date;
    private int page = 0;
    @OneToOne(mappedBy = "response")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Request request;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "response_timetable",
            joinColumns = @JoinColumn(name = "response_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "timetable_id", referencedColumnName = "id"))
    private List<Timetable> timetables;
}
