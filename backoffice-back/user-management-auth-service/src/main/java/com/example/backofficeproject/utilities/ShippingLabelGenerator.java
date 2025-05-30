package com.example.backofficeproject.utilities;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class ShippingLabelGenerator {

    public String generateLabel(String trackingNumber, String clientName, String address) throws IOException {
        String fileName = "etiquette_" + trackingNumber + ".pdf";
        String filePath = "src/main/resources/static/labels/" + fileName;

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.beginText();
            content.newLineAtOffset(50, 750);

            // Simulated layout
            content.showText("ARAMEX SHIPPING LABEL");
            content.newLineAtOffset(0, -30);
            content.setFont(PDType1Font.HELVETICA, 12);
            content.showText("Tracking Number: " + trackingNumber);
            content.newLineAtOffset(0, -20);
            content.showText("Client: " + clientName);
            content.newLineAtOffset(0, -20);
            content.showText("Destination: " + address);
            content.newLineAtOffset(0, -20);
            content.showText("Weight: 0.36 KG");
            content.newLineAtOffset(0, -20);
            content.showText("Pieces: 1");
            content.newLineAtOffset(0, -20);
            content.showText("Date: " + java.time.LocalDate.now());

            content.endText();
            content.close();

            File dir = new File("src/main/resources/static/labels");
            if (!dir.exists()) dir.mkdirs();

            document.save(filePath);
        }

        return "/static/labels/" + fileName;
    }
}