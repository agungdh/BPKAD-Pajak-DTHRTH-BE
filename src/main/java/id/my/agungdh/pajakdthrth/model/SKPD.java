package id.my.agungdh.pajakdthrth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skpd")
public class SKPD extends BaseEntity {

    private String nama;
}
