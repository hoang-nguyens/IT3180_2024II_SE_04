package models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fee_categories")
public class FeeCategory extends BaseModel{
    @Column(nullable = false, length = 255)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent")
    private FeeCategory parent;

    public FeeCategory() {}

    public FeeCategory(String name) {
        this.name = name;
        this.parent = null;
    }

    public FeeCategory(String name, FeeCategory parent) {
        this.name = name;
        this.parent = parent;
    }
}