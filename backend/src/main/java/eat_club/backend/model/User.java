package eat_club.backend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    private String name;
    private String email;
    private String phoneNumber;
    private String address;
}