package id.my.agungdh.pajakdthrth.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import id.my.agungdh.pajakdthrth.dto.DthResponse;
import id.my.agungdh.pajakdthrth.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Service
public class PdfServiceImpl implements PdfService {

    private static final Logger logger = LoggerFactory.getLogger(PdfServiceImpl.class);

    @Override
    public ByteArrayInputStream generateDthReport(List<DthResponse> dthList) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Title
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Paragraph title = new Paragraph("Laporan Daftar Transaksi Harian (DTH)", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 1, 4, 3, 3, 3, 3, 3 });

            // Headers
            Stream.of("No", "Nama Rekanan", "No SP2D", "Tgl SP2D", "Nilai Belanja", "Kode Pajak", "Jumlah Pajak")
                    .forEach(headerTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPadding(5);
                        header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                        table.addCell(header);
                    });

            // Data
            int i = 1;
            Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (DthResponse dth : dthList) {
                addCell(table, String.valueOf(i++), fontData);
                addCell(table, dth.getNamaRekanan(), fontData);
                addCell(table, dth.getNoSp2d(), fontData);
                addCell(table, dth.getTglSp2d().toString(), fontData);
                addCell(table, "Rp " + dth.getNilaiBelanjaSp2d().toPlainString(), fontData);
                addCell(table, dth.getKodePajak() != null ? dth.getKodePajak().nama() : "-", fontData);
                addCell(table, "Rp " + dth.getJumlahPajak().toPlainString(), fontData);
            }

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            logger.error("Error generating DTH PDF Report", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(4);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}
