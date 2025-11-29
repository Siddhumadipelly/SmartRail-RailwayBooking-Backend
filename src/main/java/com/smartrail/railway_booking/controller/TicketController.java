package com.smartrail.railway_booking.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.smartrail.railway_booking.model.Booking;
import com.smartrail.railway_booking.repository.BookingRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final BookingRepository bookingRepository;

    public TicketController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // ---------- GET /api/tickets/{pnr}/download ----------
    @GetMapping("/{pnr}/download")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable String pnr) {
        // 1) Find booking by PNR
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new RuntimeException("Booking not found for PNR: " + pnr));

        // 2) Generate PDF bytes
        byte[] pdfBytes = generateTicketPdf(booking);

        // 3) Build HTTP response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", pnr + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // ---------- INTERNAL: Build PDF from Booking ----------
    private byte[] generateTicketPdf(Booking booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

            // Title
            Paragraph title = new Paragraph("SmartRail – E-Ticket", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15f);
            document.add(title);

            // === PNR & STATUS ===
            PdfPTable topTable = new PdfPTable(2);
            topTable.setWidthPercentage(100);
            topTable.setSpacingAfter(10f);

            addCell(topTable, "PNR", labelFont);
            addCell(topTable, booking.getPnr(), valueFont);

            addCell(topTable, "Status", labelFont);
            addCell(topTable, booking.getStatus().name(), valueFont);

            document.add(topTable);

            // === TRAIN DETAILS ===
            PdfPTable trainTable = new PdfPTable(2);
            trainTable.setWidthPercentage(100);
            trainTable.setSpacingAfter(10f);

            String trainText = booking.getSchedule().getTrain().getName()
                    + " (" + booking.getSchedule().getTrain().getTrainNumber() + ")";

            addCell(trainTable, "Train", labelFont);
            addCell(trainTable, trainText, valueFont);

            addCell(trainTable, "From", labelFont);
            addCell(trainTable, booking.getSchedule().getSourceStation().getCode(), valueFont);

            addCell(trainTable, "To", labelFont);
            addCell(trainTable, booking.getSchedule().getDestinationStation().getCode(), valueFont);

            addCell(trainTable, "Travel Date", labelFont);
            addCell(trainTable, booking.getSchedule().getTravelDate().toString(), valueFont);

            addCell(trainTable, "Departure", labelFont);
            addCell(trainTable, booking.getSchedule().getDepartureTime().toString(), valueFont);

            addCell(trainTable, "Arrival", labelFont);
            addCell(trainTable, booking.getSchedule().getArrivalTime().toString(), valueFont);

            document.add(trainTable);

            // === PASSENGER & SEAT ===
            PdfPTable paxTable = new PdfPTable(2);
            paxTable.setWidthPercentage(100);
            paxTable.setSpacingAfter(10f);

            String paxText = booking.getPassenger().getName()
                    + " (" + booking.getPassenger().getAge()
                    + ", " + booking.getPassenger().getGender() + ")";

            addCell(paxTable, "Passenger", labelFont);
            addCell(paxTable, paxText, valueFont);

            addCell(paxTable, "Phone", labelFont);
            addCell(paxTable, booking.getPassenger().getPhone(), valueFont);

            addCell(paxTable, "Email", labelFont);
            addCell(paxTable, booking.getPassenger().getEmail(), valueFont);

            addCell(paxTable, "Seat", labelFont);
            addCell(paxTable, booking.getSeatNumber(), valueFont);

            String cls = booking.getTravelClass() != null ? booking.getTravelClass() : "N/A";
            addCell(paxTable, "Class", labelFont);
            addCell(paxTable, cls, valueFont);

            String fareText = booking.getFare() != null
                    ? "₹" + String.format("%.2f", booking.getFare())
                    : "N/A";
            addCell(paxTable, "Fare", labelFont);
            addCell(paxTable, fareText, valueFont);

            document.add(paxTable);

            // === FOOTER: Booking time ===
            if (booking.getBookingTime() != null) {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                Paragraph footer = new Paragraph(
                        "Booked At: " + booking.getBookingTime().format(fmt),
                        smallFont
                );
                footer.setSpacingBefore(20f);
                document.add(footer);
            }

            document.close();
            return baos.toByteArray();

        } catch (DocumentException | IOException e) {
            // This will turn into HTTP 500 and your React will show “Failed to download ticket PDF”
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    // helper for borderless cells
    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);
        table.addCell(cell);
    }
}
