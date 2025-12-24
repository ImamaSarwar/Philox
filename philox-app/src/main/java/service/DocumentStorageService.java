package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DocumentStorageService {

    private static final String BASE_UPLOAD_DIR = "uploads/organisations/";

    public static String saveFile(File sourceFile, int organisationId) {
        if (sourceFile == null ) return null;
        try {
            // Create organisation-specific directory
            File orgDir = new File(BASE_UPLOAD_DIR + organisationId + "/");
            if (!orgDir.exists()) orgDir.mkdirs();

            // Create unique file name
            String fileName = System.currentTimeMillis() + "_" + sourceFile.getName();
            Path targetPath = new File(orgDir, fileName).toPath();

            // Copy file to target location
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for database
            return BASE_UPLOAD_DIR + organisationId + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveFile(String sourceFile, int organisationId) {
        if (sourceFile == null ) return null;
        try {
            // Create organisation-specific directory
            File orgDir = new File(BASE_UPLOAD_DIR + organisationId + "/");
            if (!orgDir.exists()) orgDir.mkdirs();

            // Create unique file name
            String fileName = new File(sourceFile).getName();
            Path targetPath = new File(orgDir, fileName).toPath();

            // Copy file to target location
            Files.copy(java.nio.file.Paths.get(sourceFile), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return relative path for database
            return BASE_UPLOAD_DIR + organisationId + "/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
