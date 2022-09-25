/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package qr2xlcereal;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;

import java.io.FileNotFoundException;

/**
 *
 * @author pjgal
 */
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

    public void appendValue(String cellValue, String sheetName, String timeStamp, String temperature) throws FileNotFoundException, IOException{
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
        Cell tempCell = row.createCell(2);
        tempCell.setCellValue(temperature);
        System.out.println("[SheetsHelper] Cell value is " + nameCell.getStringCellValue());

        inputStream.close();

        try (FileOutputStream outputStream = new FileOutputStream(new File(getFilePath()))) {
            workbook.write(outputStream);
            System.out.println("[SheetsHelper] Succesfully Appended");
        } catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "[SheetsHelper] Close the attendance excel file if it is open, or make sure that it exists.");
            System.out.println("[SheetsHelper] File not found.");
        }

        workbook.close();

    }

    public boolean isExisting(String cellValue, String sheetName) throws IOException {
        //FileInputStream inputStream = new FileInputStream(new File(getFilePath()));
        XSSFWorkbook workbook = new XSSFWorkbook(getFilePath());

        Sheet firstSheet = null;
        if (workbook.getSheet(sheetName) == null){
            firstSheet = workbook.createSheet(sheetName);
        } else {
            firstSheet = workbook.getSheet(sheetName);
        }
        for (int i=0; i<=firstSheet.getLastRowNum(); i++){
            Cell cellToCheck = firstSheet.getRow(i).getCell(0);
            if (cellToCheck.getStringCellValue().compareTo(cellValue) == 0){
                System.out.println("[SheetsHelper] Value \'" + cellValue + "\" is a duplicate. Skipping.");
                return true;
            }
        }
        return false;
    }
}
