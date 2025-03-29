package models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "apartments")
public class Apartment extends BaseModel{

    @Column(nullable = false)
    private int floor;

    @Size(max = 20, message = "số phòng")
    @Column(nullable = false)
    private String roomNumber;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private float area;

}