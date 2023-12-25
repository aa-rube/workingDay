package app.bot.service;

import app.factory.model.Person;
import app.factory.service.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminHandler {

    @Autowired
    private PeopleService peopleService;

    public File renameAndCopyFile(LocalDate date) {
        File originalFile = new File("/home/onixmore/workingDay/data");
        String newFileName = "Отчет_" + date.getDayOfMonth() + "." +date.getMonthValue() + "." + date.getYear() + "_";

        try {
            File tempDir = new File("/home/onixmore/workingDay/data/temp");
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
        File tempDir = new File("/home/onixmore/workingDay/data/temp");

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

            try (FileOutputStream outputStream = new FileOutputStream("/home/onixmore/workingDay/data")) {
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

    public List<String> getEmploysWithOutReport(Set<String> reports) {
        if (reports.isEmpty()) {
            return Collections.emptyList();
        }

        return peopleService.findAll().stream()
                .map(person -> person.getSecondName() + " " + person.getName() + " " + person.getThirdName())
                .filter(name -> reports.stream().noneMatch(report -> report.split(",")[0].trim().equals(name)))
                .collect(Collectors.toList());
    }

}
