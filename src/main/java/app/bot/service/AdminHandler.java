package app.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Service
public class AdminHandler {
    public File renameAndCopyFile(LocalDate date) {
        File originalFile = new File("data/workbook.xlsm");
        String newFileName = "Отчет_" + date.getDayOfMonth() + "." +date.getMonthValue() + "." + date.getYear() + "_";

        try {
            File tempDir = new File("data/temp");
            tempDir.mkdirs();
            File tempFile = File.createTempFile(newFileName, ".xlsm", tempDir);
            Files.copy(originalFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteAllFilesInTempFolder() {
        File tempDir = new File("data/temp");

        if (tempDir.exists() && tempDir.isDirectory()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    public boolean saveFile(org.telegram.telegrambots.meta.api.objects.File file, String token) {
        if (file == null) return false;

        try {
            String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + file.getFilePath();
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            try (FileOutputStream outputStream = new FileOutputStream("data/workbook.xlsm")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            inputStream.close();
            return true;
        } catch (Exception e) {
           return false;
        }
    }
}
