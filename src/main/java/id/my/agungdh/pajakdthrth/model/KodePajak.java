package id.my.agungdh.pajakdthrth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kode_pajak")
@Getter
@Setter
@NoArgsConstructor
public class KodePajak extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nama;
}
