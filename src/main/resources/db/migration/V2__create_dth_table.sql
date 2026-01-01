CREATE TABLE dth (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(255) NOT NULL,
    
    no_spm VARCHAR(255),
    tgl_spm DATE,
    nilai_belanja_spm DECIMAL(19, 2),
    
    no_sp2d VARCHAR(255),
    tgl_sp2d DATE,
    nilai_belanja_sp2d DECIMAL(19, 2),
    
    kode_akun VARCHAR(255),
    
    pajak_ppn DECIMAL(19, 2),
    pajak_pph21 DECIMAL(19, 2),
    pajak_pph22 DECIMAL(19, 2),
    pajak_pph23 DECIMAL(19, 2),
    pajak_pph4_ayat2 DECIMAL(19, 2),
    
    jumlah_pajak DECIMAL(19, 2),
    
    npwp VARCHAR(255),
    nama_rekanan VARCHAR(255),
    
    kode_billing VARCHAR(255),
    ntpn VARCHAR(255),
    
    keterangan TEXT,
    
    skpd_id BIGINT REFERENCES skpd(id),
    
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted_by BIGINT
);

CREATE INDEX idx_dth_uuid_hash ON dth USING HASH (uuid);
CREATE INDEX idx_dth_skpd_id ON dth (skpd_id);
