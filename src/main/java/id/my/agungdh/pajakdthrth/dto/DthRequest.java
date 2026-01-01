package id.my.agungdh.pajakdthrth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DthRequest {

    @NotNull(message = "No SPM wajib diisi")
    private String noSpm;

    @NotNull(message = "Tanggal SPM wajib diisi")
    private LocalDate tglSpm;

    @PositiveOrZero(message = "Nilai Belanja SPM tidak boleh negatif")
    private BigDecimal nilaiBelanjaSpm;

    @NotNull(message = "No SP2D wajib diisi")
    private String noSp2d;

    @NotNull(message = "Tanggal SP2D wajib diisi")
    private LocalDate tglSp2d;

    @PositiveOrZero(message = "Nilai Belanja SP2D tidak boleh negatif")
    private BigDecimal nilaiBelanjaSp2d;

    @NotNull(message = "Kode Akun wajib diisi")
    private String kodeAkun;

    @PositiveOrZero
    private BigDecimal pajakPpn;

    @PositiveOrZero
    private BigDecimal pajakPph21;

    @PositiveOrZero
    private BigDecimal pajakPph22;

    @PositiveOrZero
    private BigDecimal pajakPph23;

    @PositiveOrZero
    private BigDecimal pajakPph4Ayat2;

    private String npwp;

    @NotNull(message = "Nama Rekanan wajib diisi")
    private String namaRekanan;

    private String kodeBilling;

    private String ntpn;

    private String keterangan;

    @NotNull(message = "SKPD wajib diisi")
    @JsonProperty("skpd_id")
    private String skpdId;
}
