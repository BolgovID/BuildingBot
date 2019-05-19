
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;


public class ExcelParser {




    public static void readFromExcel() throws IOException {
        XSSFWorkbook book = new XSSFWorkbook(new FileInputStream(Settings.ExcelLink));
        XSSFSheet sheet = book.getSheet("HousesTable");
        XSSFRow row;
        for (int i = 1; i < 6; i++) {
            row = sheet.getRow(i);
            for (int j = 1; j < 12; j++) {
                if (!isCellEmpty(row.getCell(j))) {
                    House.CreateHouse(row.getCell(j).getStringCellValue(),
                            row.getCell(0).getStringCellValue(),
                            sheet.getRow(0).getCell(j).getStringCellValue());
                }
            }
        }
        book.close();
    }

    private static boolean isCellEmpty(XSSFCell cell) {
        if (cell == null) return true;
        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) return true;
        if (cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().isEmpty()) return true;
        return false;
    }

}
