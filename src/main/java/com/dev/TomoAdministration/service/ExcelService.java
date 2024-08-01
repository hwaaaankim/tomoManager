package com.dev.TomoAdministration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dev.TomoAdministration.dto.CalculDTO;
import com.dev.TomoAdministration.dto.ExcelDTO;
import com.dev.TomoAdministration.dto.HiddenExcelDTO;

@Service
public class ExcelService {

	
	public void makeHiddenExcel(MultipartFile file, HttpServletResponse res) throws IOException {
        List<HiddenExcelDTO> allList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet productSheet = workbook.getSheetAt(2);

            for (int i = 0; i < productSheet.getPhysicalNumberOfRows(); i++) {
                Row row = productSheet.getRow(i);
                if (row != null && !(row.getCell(9) + "").equals("NULL")) {
                    HiddenExcelDTO dto = new HiddenExcelDTO();
                    dto.setProductCode(row.getCell(1) + "");
                    String[] colors = (row.getCell(9) + "").split(",");
                    dto.setColorCode(colors);
                    allList.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to process the Excel file", e);
        }

        try (Workbook downloadWorkbook = new XSSFWorkbook()) {
            Sheet colorsExcel = downloadWorkbook.createSheet("color");
            int rowCount = 0;

            for (HiddenExcelDTO dto : allList) {
                for (String color : dto.getColorCode()) {
                    Row bodyRow = colorsExcel.createRow(rowCount++);
                    bodyRow.createCell(0).setCellValue(dto.getProductCode());
                    bodyRow.createCell(1).setCellValue(color);
                }
            }

            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment;filename=색상.xlsx");
            try (ServletOutputStream servletOutputStream = res.getOutputStream()) {
                downloadWorkbook.write(servletOutputStream);
                servletOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to create the download Excel file", e);
        }
    }

    public void makeExcelProcess(MultipartFile file, HttpServletResponse res) throws IOException {
        List<ExcelDTO> allList = new ArrayList<>();
        List<ExcelDTO> dajMember = new ArrayList<>();
        List<ExcelDTO> dajCalcul = new ArrayList<>();
        List<ExcelDTO> murachMember = new ArrayList<>();
        List<ExcelDTO> murachCalcul = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            processExcelDealSheet(workbook, allList);
            processExcelDajSheet(workbook, dajMember);
            processExcelMurachSheet(workbook, murachMember);

            matchUsers(allList, dajMember, dajCalcul);
            matchUsers(allList, murachMember, murachCalcul);

            createExcelDownload(res, dajCalcul, murachCalcul);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to process the Excel file", e);
        }
    }

    private void processExcelDealSheet(Workbook workbook, List<ExcelDTO> allList) {
        try {
            Sheet dealSheet = workbook.getSheetAt(0);
            for (int i = 0; i < dealSheet.getPhysicalNumberOfRows(); i++) {
                Row row = dealSheet.getRow(i);
                if (row != null) {
                    ExcelDTO dto = new ExcelDTO();
                    dto.setUsername(row.getCell(0) + "");
                    dto.setCharge(row.getCell(1) + "");
                    dto.setQuantity(row.getCell(2) + "");
                    dto.setService(row.getCell(3) + "");
                    dto.setStatus(row.getCell(4) + "");
                    dto.setCreateDate(row.getCell(5) + "");
                    allList.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processExcelDajSheet(Workbook workbook, List<ExcelDTO> dajMember) {
        try {
            Sheet dajSheet = workbook.getSheetAt(1);
            for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
                Row row = dajSheet.getRow(i);
                if (row != null) {
                    ExcelDTO dto = new ExcelDTO();
                    dto.setUsername(row.getCell(0) + "");
                    dajMember.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processExcelMurachSheet(Workbook workbook, List<ExcelDTO> murachMember) {
        try {
            Sheet muraSheet = workbook.getSheetAt(2);
            for (int i = 0; i < muraSheet.getPhysicalNumberOfRows(); i++) {
                Row row = muraSheet.getRow(i);
                if (row != null) {
                    ExcelDTO dto = new ExcelDTO();
                    dto.setUsername(row.getCell(0) + "");
                    murachMember.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void matchUsers(List<ExcelDTO> allList, List<ExcelDTO> members, List<ExcelDTO> calculations) {
        for (ExcelDTO user : members) {
            for (ExcelDTO dto : allList) {
                if (dto.getUsername().equals(user.getUsername())) {
                    dto.setDealerUsername(user.getDealerUsername());
                    calculations.add(dto);
                }
            }
        }
    }

    private void createExcelDownload(HttpServletResponse res, List<ExcelDTO> dajCalcul, List<ExcelDTO> murachCalcul) throws IOException {
        try (Workbook downloadWorkbook = new XSSFWorkbook()) {
            createSheet(downloadWorkbook, "daj.gppg.calculation", dajCalcul);
            createSheet(downloadWorkbook, "murach.calculation", murachCalcul);

            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment;filename=snstomo.xlsx");
            try (ServletOutputStream servletOutputStream = res.getOutputStream()) {
                downloadWorkbook.write(servletOutputStream);
                servletOutputStream.flush();
            }
        }
    }

    private void createSheet(Workbook workbook, String sheetName, List<ExcelDTO> calculations) {
        Sheet sheet = workbook.createSheet(sheetName);
        int rowCount = 0;

        for (ExcelDTO dto : calculations) {
            Row bodyRow = sheet.createRow(rowCount++);
            bodyRow.createCell(0).setCellValue(dto.getUsername());
            bodyRow.createCell(1).setCellValue(dto.getEmail());
            bodyRow.createCell(2).setCellValue(dto.getCharge());
            bodyRow.createCell(3).setCellValue(dto.getQuantity());
            bodyRow.createCell(4).setCellValue(dto.getService());
            bodyRow.createCell(5).setCellValue(dto.getStatus());
            bodyRow.createCell(6).setCellValue(dto.getCreateDate());
        }
    }

    public void makeCalculationProcess(MultipartFile file, HttpServletResponse res) throws IOException {
        List<CalculDTO> allList = new ArrayList<>();
        List<CalculDTO> dajMember = new ArrayList<>();
        List<CalculDTO> dajCalcul = new ArrayList<>();
        List<CalculDTO> murachMember = new ArrayList<>();
        List<CalculDTO> murachCalcul = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            processCalculDealSheet(workbook, allList);
            processCalculDajSheet(workbook, dajMember);
            processCalculMurachSheet(workbook, murachMember);

            matchCalculUsers(allList, dajMember, dajCalcul);
            matchCalculUsers(allList, murachMember, murachCalcul);

            createCalculExcelDownload(res, dajCalcul, murachCalcul);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to process the Excel file", e);
        }
    }

    private void processCalculDealSheet(Workbook workbook, List<CalculDTO> allList) {
        try {
            Sheet dealSheet = workbook.getSheetAt(0);
            for (int i = 0; i < dealSheet.getPhysicalNumberOfRows(); i++) {
                Row row = dealSheet.getRow(i);
                if (row != null) {
                    CalculDTO dto = new CalculDTO();
                    dto.setUsername(row.getCell(0) + "");
                    dto.setBalance(row.getCell(1) + "");
                    dto.setAmount(row.getCell(2) + "");
                    dto.setMethod(row.getCell(3) + "");
                    dto.setStatus(row.getCell(4) + "");
                    dto.setMemo(row.getCell(5) + "");
                    dto.setStartDate(row.getCell(6) + "");
                    dto.setEndDate(row.getCell(7) + "");
                    allList.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCalculDajSheet(Workbook workbook, List<CalculDTO> dajMember) {
        try {
            Sheet dajSheet = workbook.getSheetAt(1);
            for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
                Row row = dajSheet.getRow(i);
                if (row != null) {
                    CalculDTO dto = new CalculDTO();
                    dto.setUsername(row.getCell(0) + "");
                    dajMember.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processCalculMurachSheet(Workbook workbook, List<CalculDTO> murachMember) {
        try {
            Sheet muraSheet = workbook.getSheetAt(2);
            for (int i = 0; i < muraSheet.getPhysicalNumberOfRows(); i++) {
                Row row = muraSheet.getRow(i);
                if (row != null) {
                    CalculDTO dto = new CalculDTO();
                    dto.setUsername(row.getCell(0) + "");
                    murachMember.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void matchCalculUsers(List<CalculDTO> allList, List<CalculDTO> members, List<CalculDTO> calculations) {
        for (CalculDTO user : members) {
            for (CalculDTO dto : allList) {
                if (dto.getUsername().equals(user.getUsername())) {
                    calculations.add(dto);
                }
            }
        }
    }

    private void createCalculExcelDownload(HttpServletResponse res, List<CalculDTO> dajCalcul, List<CalculDTO> murachCalcul) throws IOException {
        try (Workbook downloadWorkbook = new XSSFWorkbook()) {
            createCalculSheet(downloadWorkbook, "daj.gppg.payment", dajCalcul);
            createCalculSheet(downloadWorkbook, "murach.payment", murachCalcul);

            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment;filename=snstomo.xlsx");
            try (ServletOutputStream servletOutputStream = res.getOutputStream()) {
                downloadWorkbook.write(servletOutputStream);
                servletOutputStream.flush();
            }
        }
    }

    private void createCalculSheet(Workbook workbook, String sheetName, List<CalculDTO> calculations) {
        Sheet sheet = workbook.createSheet(sheetName);
        int rowCount = 0;

        for (CalculDTO dto : calculations) {
            Row bodyRow = sheet.createRow(rowCount++);
            bodyRow.createCell(0).setCellValue(dto.getUsername());
            bodyRow.createCell(1).setCellValue(dto.getBalance());
            bodyRow.createCell(2).setCellValue(dto.getAmount());
            bodyRow.createCell(3).setCellValue(dto.getMethod());
            bodyRow.createCell(4).setCellValue(dto.getStatus());
            bodyRow.createCell(5).setCellValue(dto.getMemo());
            bodyRow.createCell(6).setCellValue(dto.getStartDate());
            bodyRow.createCell(7).setCellValue(dto.getEndDate());
        }
    }
}
