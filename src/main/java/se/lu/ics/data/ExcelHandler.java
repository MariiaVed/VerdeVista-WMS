package se.lu.ics.data;

import java.io.File;
import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;



public class ExcelHandler{

    private static String path = "src/main/resources/excel/VerdeVistaWMSExport.xlsx";

    public static void openExcelFile()throws Exception {

        try {

            File file = new File(path);

            File tempDir = new File(System.getProperty("java.io.tmpdir"));

            File tempFile = new File(tempDir, file.getName());

            Files.copy(file.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            
            Desktop.getDesktop().open(tempFile);
    
        } catch (Exception e) {
            throw e;
        } 
    

    }
    
}
