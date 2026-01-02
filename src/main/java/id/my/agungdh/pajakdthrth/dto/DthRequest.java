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

    @NotNull(message = "Kode Pajak ID wajib diisi")
    @JsonProperty("kode_pajak_id")
    private String kodePajakId;

    @NotNull(message = "Jumlah Pajak wajib diisi")
    @PositiveOrZero(message = "Jumlah Pajak harus positif atau 0")
    @JsonProperty("jumlah_pajak")
    private BigDecimal jumlahPajak;

    private String npwp;

    @NotNull(message = "Nama Rekanan wajib diisi")
    private String namaRekanan;

    private String kodeBilling;

    private String ntpn;

    private String keterangan;

    @JsonProperty("skpd_id")
    private String skpdId;
}
