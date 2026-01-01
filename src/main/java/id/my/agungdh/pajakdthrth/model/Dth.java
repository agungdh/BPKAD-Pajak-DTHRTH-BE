package id.my.agungdh.pajakdthrth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "dth")
@SQLRestriction("deleted_at IS NULL")
public class Dth extends BaseEntity {

    // SPM Details
    @Column(name = "no_spm")
    private String noSpm;

    @Column(name = "tgl_spm")
    private LocalDate tglSpm;

    @Column(name = "nilai_belanja_spm", precision = 19, scale = 2)
    private BigDecimal nilaiBelanjaSpm;

    // SP2D Details
    @Column(name = "no_sp2d")
    private String noSp2d;

    @Column(name = "tgl_sp2d")
    private LocalDate tglSp2d;

    @Column(name = "nilai_belanja_sp2d", precision = 19, scale = 2)
    private BigDecimal nilaiBelanjaSp2d;

    // Account Details
    @Column(name = "kode_akun")
    private String kodeAkun;

    // Tax Details
    @Column(name = "pajak_ppn", precision = 19, scale = 2)
    private BigDecimal pajakPpn;

    @Column(name = "pajak_pph21", precision = 19, scale = 2)
    private BigDecimal pajakPph21;

    @Column(name = "pajak_pph22", precision = 19, scale = 2)
    private BigDecimal pajakPph22;

    @Column(name = "pajak_pph23", precision = 19, scale = 2)
    private BigDecimal pajakPph23;

    @Column(name = "pajak_pph4_ayat2", precision = 19, scale = 2)
    private BigDecimal pajakPph4Ayat2;

    @Column(name = "jumlah_pajak", precision = 19, scale = 2)
    private BigDecimal jumlahPajak;

    // Payee/Rekanan Details
    @Column(name = "npwp")
    private String npwp;

    @Column(name = "nama_rekanan")
    private String namaRekanan;

    // Verification
    @Column(name = "kode_billing")
    private String kodeBilling;

    @Column(name = "ntpn")
    private String ntpn;

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skpd_id")
    private SKPD skpd;
}
