package com.empmgmt.service.impl;

import com.empmgmt.model.Application;
import com.empmgmt.model.OfferLetter;
import com.empmgmt.repository.ApplicationRepository;
import com.empmgmt.repository.OfferLetterRepository;
import com.empmgmt.service.OfferLetterService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OfferLetterServiceImpl implements OfferLetterService {

    private final ApplicationRepository appRepo;
    private final OfferLetterRepository offerRepo;

    @Override
    public OfferLetter generateOffer(Long appId, String position, Double salary, String joiningDate) {

        Application app = appRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Ensure folder exists
        String folder = "uploads/offers/";
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        // Output PDF path
        String filePath = folder + "offer-" + appId + ".pdf";

        try {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, new FileOutputStream(filePath));

            doc.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph title = new Paragraph("Offer Letter", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            doc.add(title);

            // Body content
            doc.add(new Paragraph("Date: " + LocalDate.now()));
            doc.add(new Paragraph("Candidate: " + app.getFullName()));
            doc.add(new Paragraph("Email: " + app.getEmail()));
            doc.add(new Paragraph("\nDear " + app.getFullName() + ",\n"));

            doc.add(new Paragraph(
                    "We are pleased to offer you the position of " + position +
                            " at our organization. Your expected annual salary will be " +
                            salary + "."
            ));

            doc.add(new Paragraph(
                    "\nYour joining date has been scheduled for: " + joiningDate
            ));

            doc.add(new Paragraph(
                    "\nPlease sign and return the acceptance copy of this offer letter."
                            + "\n\nCongratulations, and welcome aboard!"
            ));

            doc.add(new Paragraph("\n\nRegards,\nHR Team\nEmployee Management System"));

            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create offer letter PDF", e);
        }

        // Save in DB
        OfferLetter offer = OfferLetter.builder()
                .applicationId(appId)
                .position(position)
                .salary(salary)
                .joiningDate(LocalDate.parse(joiningDate))
                .filePath(filePath)
                .build();

        return offerRepo.save(offer);
    }
}
