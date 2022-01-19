import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;

public class SheetsHelper {
    private File excelFile;

    public SheetsHelper(File file){
        this.excelFile = file;
    }

    public SheetsHelper(){

    }

    public String getFilePath(){
        return excelFile.getAbsolutePath();
    }

    public void setFile(File file){
        this.excelFile = file;
    }

    public void appendValue(String cellValue, String sheetName, String timeStamp) throws IOException {
        Sheet firstSheet = null;
        FileInputStream inputStream = new FileInputStream(new File(getFilePath()));
        Workbook workbook = new XSSFWorkbook(inputStream);

        if (workbook.getSheet(sheetName) == null){
            firstSheet = workbook.createSheet(sheetName);
        } else {
            firstSheet = workbook.getSheet(sheetName);
        }

        Row row = firstSheet.createRow(firstSheet.getLastRowNum()+1);
        Cell nameCell = row.createCell(0);
        nameCell.setCellValue(cellValue);
        Cell timeCell = row.createCell(1);
        timeCell.setCellValue(timeStamp);
        System.out.println("Cell value is " + nameCell.getStringCellValue());

        inputStream.close();

        try (FileOutputStream outputStream = new FileOutputStream(new File(getFilePath()))) {
            workbook.write(outputStream);
            System.out.println("Succesfully Appended");
        } catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "Close the attendance excel file if it is open, or make sure that it exists.");
            System.out.println("File not found.");
        }

        workbook.close();

    }

    public boolean isExisting(String cellValue, String sheetName) throws IOException {
        FileInputStream inputStream = new FileInputStream(new File(getFilePath()));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet firstSheet = null;
        if (workbook.getSheet(sheetName) == null){
            firstSheet = workbook.createSheet(sheetName);
        } else {
            firstSheet = workbook.getSheet(sheetName);
        }
        for (int i=0; i<=firstSheet.getLastRowNum(); i++){
            Cell cellToCheck = firstSheet.getRow(i).getCell(0);
            if (cellToCheck.getStringCellValue().compareTo(cellValue) == 0){
                System.out.println("Value \'" + cellValue + "\" is a duplicate. Skipping.");
                return true;
            }
        }
        return false;
    }
}
