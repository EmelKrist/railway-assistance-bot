package ru.emelkrist.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request")
@ToString(exclude = "id")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long telegramUserId;
    private String fromCity;
    private String codeFrom;
    private String toCity;
    private String codeTo;
    private String date;
    private boolean successfully = false;
    @OneToOne
    @JoinColumn(name = "response_id", referencedColumnName = "id")
    private Response response;
}
