package home.jakartasubmit;

import com.ironsoftware.ironpdf.PdfDocument;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PDFPreview extends JFrame {
    private List<String> imagePaths = new ArrayList<>();

    public List<String> ConvertToImages(String fileName, String filePath) {
        try (PdfDocument pdfDocument = PdfDocument.fromFile(Paths.get(filePath))) {
            // Convert each PDF page into an image
            List<BufferedImage> extractedImages = pdfDocument.toBufferedImages();
            int i = 1;

            for (BufferedImage extractedImage : extractedImages) {
                String outputImagePath = "assets/images/" + i + ".png";
                ImageIO.write(extractedImage, "PNG", new File(outputImagePath));
                imagePaths.add(outputImagePath);
                i++;
            }
            return imagePaths;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list if an error occurs
        }
    }
}