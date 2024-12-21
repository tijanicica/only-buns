package com.project.onlybuns.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
public class ImageCompression {

    private static final String COMPRESSED_LOG_FILE = "compressed_images.log";

    private static Set<String> loadCompressedImagesLog() {
        Set<String> compressedImages = new HashSet<>();
        File logFile = new File(COMPRESSED_LOG_FILE);

        if (logFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    compressedImages.add(line.trim());
                }
            } catch (IOException e) {
                System.err.println("Failed to read compressed images log.");
                e.printStackTrace();
            }
        }

        return compressedImages;
    }

    private static void saveCompressedImageToLog(String imageName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMPRESSED_LOG_FILE, true))) {
            writer.write(imageName);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save compressed image to log.");
            e.printStackTrace();
        }
    }

    public static File[] findOldImages(String directoryPath) {
        File folder = new File(directoryPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        File[] oldImages = folder.listFiles(file -> {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".jpg")) {
                try {
                    Date lastModified = new Date(file.lastModified());
                    LocalDate fileDate = lastModified.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return fileDate.isBefore(LocalDate.now().minusMonths(1));
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        });

        return oldImages;
    }

    public static void compressImage(File inputFile, String outputDirectory) throws IOException {
        BufferedImage image = ImageIO.read(inputFile);

        File outputFile = new File(outputDirectory + File.separator + inputFile.getName());
        if (!outputFile.getParentFile().exists()) {
            Files.createDirectories(outputFile.getParentFile().toPath());
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputFile)) {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.5f); // Postavi nivo kompresije (0.0 - 1.0)
            }

            writer.write(null, new IIOImage(image, null, null), param);
            writer.dispose();
        }
    }

    public static void compressOldImages(String directoryPath, String outputDirectory) {
        Set<String> compressedImagesLog = loadCompressedImagesLog();
        File[] oldImages = findOldImages(directoryPath);

        if (oldImages == null || oldImages.length == 0) {
            System.out.println("No images older than one month found in the directory.");
            return;
        }

        for (File image : oldImages) {
            if (compressedImagesLog.contains(image.getName())) {
                System.out.println("Skipping already compressed image: " + image.getName());
                continue;
            }

            try {
                compressImage(image, outputDirectory);
                saveCompressedImageToLog(image.getName());
                System.out.println("Compressed: " + image.getName());
            } catch (IOException e) {
                System.err.println("Failed to compress image: " + image.getName());
                e.printStackTrace();
            }
        }
    }
}
