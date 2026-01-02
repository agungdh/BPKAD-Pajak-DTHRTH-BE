package id.my.agungdh.pajakdthrth.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DthResponse {
    private String uuid;
    private String noSpm;
    private LocalDate tglSpm;
    private BigDecimal nilaiBelanjaSpm;
    private String noSp2d;
    private LocalDate tglSp2d;
    private BigDecimal nilaiBelanjaSp2d;
    private KodePajakResponse kodePajak;
    private BigDecimal jumlahPajak;
    private String npwp;
    private String namaRekanan;
    private String kodeBilling;
    private String ntpn;
    private String keterangan;

    // Nested SKPD info
    private SkpdResponse skpd;
}
