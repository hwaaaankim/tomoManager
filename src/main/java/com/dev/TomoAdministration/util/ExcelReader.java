package com.dev.TomoAdministration.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;


public class ExcelReader {

    // ✅ record 대신 POJO 사용 (정적 내부 클래스)
    public static class RowData {
        private String username;
        private String email;

        public RowData(String username, String email) {
            this.username = username;
            this.email = email;
        }
        public String getUsername() { return username; }
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return "RowData{username='" + username + "', email='" + email + "'}";
        }
    }

    /**
     * 0열=username, 1열=email
     * 첫 행이 헤더라면 for문을 i=1부터 시작하세요.
     */
    public static List<RowData> readUsernameEmail(MultipartFile file) throws Exception {
        List<RowData> list = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {

            Sheet sheet = wb.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String username = getStringCell(row.getCell(0));
                String email = getStringCell(row.getCell(1));

                if (email == null || email.trim().isEmpty()) continue;

                list.add(new RowData(
                        username == null ? "" : username.trim(),
                        email.trim()
                ));
            }
        }
        return list;
    }

    private static String getStringCell(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue();
            case NUMERIC:
                // 이메일 컬럼이 숫자로 들어올 일은 거의 없지만 username이 숫자인 경우 대비
                double v = cell.getNumericCellValue();
                long asLong = (long) v;
                if (asLong == v) return String.valueOf(asLong);
                return String.valueOf(v);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return null;
        }
    }
}