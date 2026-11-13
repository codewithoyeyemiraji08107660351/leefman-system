package utils;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import models.SpecialProject;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class ProjectPDFGenerator {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private static final String DEFAULT_SAVE_PATH = System.getProperty("user.home") + 
            File.separator + "Documents" + File.separator + "leefman-report" + File.separator;

    public static String generateProjectPDF(SpecialProject project) {
        try {
           
            File directory = new File(DEFAULT_SAVE_PATH);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            String fileName = project.getProjectCode() + "_Report_"
                    + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf";
            String filePath = DEFAULT_SAVE_PATH + fileName;

            // Create PDF writer
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(50, 50, 50, 50);

            
            PdfFont boldFont = PdfFontFactory.createFont("Helvetica-Bold");
            PdfFont normalFont = PdfFontFactory.createFont("Helvetica");
            PdfFont titleFont = PdfFontFactory.createFont("Helvetica-Bold");

           
            Paragraph header = new Paragraph("LEEFMAN RECORD SYSTEM")
                    .setFont(titleFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GREEN);
            document.add(header);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Generated on: " + DATE_TIME_FORMAT.format(new java.util.Date()))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            
            addSectionTitle(document, "PROJECT INFORMATION", boldFont);
            Table infoTable = createInfoTable(2);

            addInfoRow(infoTable, "Project Code:", project.getProjectCode());
            addInfoRow(infoTable, "Project Name:", project.getProjectName());
            addInfoRow(infoTable, "Category:", project.getCategoryName());
            addInfoRow(infoTable, "Client Name:", project.getClientName());
            addInfoRow(infoTable, "Client Phone:", project.getClientPhone());
            addInfoRow(infoTable, "Client Address:", project.getClientAddress());
            addInfoRow(infoTable, "Person In Charge:", project.getPersonInCharge());
            addInfoRow(infoTable, "Event Date:", project.getEventDate() != null ? DATE_FORMAT.format(project.getEventDate()) : "N/A");
            addInfoRow(infoTable, "Start Date:", project.getStartDate() != null ? DATE_FORMAT.format(project.getStartDate()) : "N/A");
            addInfoRow(infoTable, "Completion Date:", project.getCompletionDate() != null ? DATE_FORMAT.format(project.getCompletionDate()) : "N/A");

            document.add(infoTable);

            
            addSectionTitle(document, "ORDER DETAILS", boldFont);
            Table orderTable = createInfoTable(2);
            addInfoRow(orderTable, "Quantity:", String.valueOf(project.getQuantity()));
            addInfoRow(orderTable, "Product Type:", project.getProductType());
            addInfoRow(orderTable, "Design Name:", project.getDesignName());
            addInfoRow(orderTable, "Description:", project.getDescription());
            addInfoRow(orderTable, "Embroidery Details:", project.getEmbroideryDetails());
            addInfoRow(orderTable, "Special Requirements:", project.getSpecialRequirements());
            document.add(orderTable);

           
            addSectionTitle(document, "FINANCIAL RECORD", boldFont);
            Table financialTable = createInfoTable(2);
            addInfoRow(financialTable, "Total Price:", String.format("₦%.2f", project.getTotalPrice()));
            addInfoRow(financialTable, "Amount Paid:", String.format("₦%.2f", project.getAmountPaid()));
            addInfoRow(financialTable, "Balance Remaining:", String.format("₦%.2f", project.getBalanceRemaining()));
            addInfoRow(financialTable, "Payment Status:", project.getPaymentStatus());
            addInfoRow(financialTable, "Payment Method:", project.getPaymentMethod());
            document.add(financialTable);

            // ===== COST TRACKING =====
            addSectionTitle(document, "CAPITAL / COST TRACKING", boldFont);

            // Materials Cost
            Table materialTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            materialTable.setWidth(UnitValue.createPercentValue(100));

            Cell matHeader = new Cell(1, 2)
                    .add(new Paragraph("MATERIALS COST").setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            materialTable.addCell(matHeader);

            addInfoRow(materialTable, "Fabric Cost:", String.format("₦%.2f", project.getFabricCost()));
            addInfoRow(materialTable, "Thread Cost:", String.format("₦%.2f", project.getThreadCost()));
            addInfoRow(materialTable, "Accessories Cost:", String.format("₦%.2f", project.getAccessoriesCost()));
            addInfoRow(materialTable, "Packaging Cost:", String.format("₦%.2f", project.getPackagingCost()));
            addInfoRow(materialTable, "Other Materials:", String.format("₦%.2f", project.getOtherMaterialsCost()));

            Cell totalMat = new Cell(1, 2)
                    .add(new Paragraph("TOTAL MATERIAL COST: " + String.format("₦%.2f", project.getTotalMaterialCost()))
                            .setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT);
            materialTable.addCell(totalMat);
            document.add(materialTable);

            document.add(new Paragraph(" "));

           
            Table productionTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            productionTable.setWidth(UnitValue.createPercentValue(100));

            Cell prodHeader = new Cell(1, 2)
                    .add(new Paragraph("PRODUCTION COST").setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            productionTable.addCell(prodHeader);

            addInfoRow(productionTable, "Tailoring Labour:", String.format("₦%.2f", project.getTailoringLabourCost()));
            addInfoRow(productionTable, "Embroidery Machine:", String.format("₦%.2f", project.getEmbroideryMachineCost()));
            addInfoRow(productionTable, "Workers Payment:", String.format("₦%.2f", project.getWorkersPayment()));
            addInfoRow(productionTable, "Transportation:", String.format("₦%.2f", project.getTransportationCost()));
            addInfoRow(productionTable, "Other Expenses:", String.format("₦%.2f", project.getOtherExpenses()));

            Cell totalProd = new Cell(1, 2)
                    .add(new Paragraph("TOTAL PRODUCTION COST: " + String.format("₦%.2f", project.getTotalProductionCost()))
                            .setFont(boldFont).setFontSize(12))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.RIGHT);
            productionTable.addCell(totalProd);
            document.add(productionTable);
            
            addSectionTitle(document, "PROFIT CALCULATION", boldFont);
            Table profitTable = createInfoTable(2);
            addInfoRow(profitTable, "Total Income:", String.format("₦%.2f", project.getTotalIncome()));
            addInfoRow(profitTable, "Total Expenses:", String.format("₦%.2f", project.getTotalExpenses()));

            Cell profitCell = new Cell(1, 2)
                    .add(new Paragraph("NET PROFIT: " + String.format("₦%.2f", project.getNetProfit()))
                            .setFont(boldFont).setFontSize(14))
                    .setTextAlignment(TextAlignment.RIGHT);
            if (project.getNetProfit() > 0) {
                profitCell.setBackgroundColor(ColorConstants.GREEN);
            } else if (project.getNetProfit() < 0) {
                profitCell.setBackgroundColor(ColorConstants.RED);
            } else {
                profitCell.setBackgroundColor(ColorConstants.YELLOW);
            }
            profitTable.addCell(profitCell);
            document.add(profitTable);

            addSectionTitle(document, "PROJECT STATUS", boldFont);
            Table statusTable = createInfoTable(2);
            addInfoRow(statusTable, "Project Status:", project.getProjectStatus());
            addInfoRow(statusTable, "Completion Percentage:", project.getCompletionPercentage() + "%");
            addInfoRow(statusTable, "Notes:", project.getNotes());
            document.add(statusTable);

           
            addSectionTitle(document, "TIMESTAMPS", boldFont);
            Table timestampTable = createInfoTable(2);
            addInfoRow(timestampTable, "Created:", project.getCreatedAt() != null ? DATE_TIME_FORMAT.format(project.getCreatedAt()) : "N/A");
            addInfoRow(timestampTable, "Last Updated:", project.getUpdatedAt() != null ? DATE_TIME_FORMAT.format(project.getUpdatedAt()) : "N/A");
            document.add(timestampTable);

            // ===== FOOTER =====
            document.add(new Paragraph(" "));
            Paragraph footer = new Paragraph("Generated on: " + DATE_TIME_FORMAT.format(new java.util.Date()))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            document.add(footer);

            document.close();
            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addSectionTitle(Document document, String title, PdfFont boldFont) {
        Paragraph sectionTitle = new Paragraph(title)
                .setFont(boldFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setBorderBottom(new SolidBorder(ColorConstants.BLACK, 1));
        document.add(sectionTitle);
        document.add(new Paragraph(" "));
    }

    private static Table createInfoTable(int columns) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setBorder(Border.NO_BORDER);
        return table;
    }

    private static void addInfoRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(labelCell);

        Cell valueCell = new Cell()
                .add(new Paragraph(value != null && !value.isEmpty() ? value : "N/A").setFontSize(10))
                .setBorder(Border.NO_BORDER)
                .setPadding(3);
        table.addCell(valueCell);
    }
}