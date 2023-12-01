package com.dev.TomoAdministration.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dev.TomoAdministration.dto.ExcelDTO;

@Service
public class ExcelService {

	public void makeExcelProcess(
			MultipartFile file,
			HttpServletResponse res) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		List<ExcelDTO> userIdList = new ArrayList<ExcelDTO>();
		List<ExcelDTO> dealLists = new ArrayList<ExcelDTO>();
		List<ExcelDTO> relatedUsers = new ArrayList<ExcelDTO>();
	
		try {
			Sheet userSheet = workbook.getSheetAt(0);
			for(int i=0; i<userSheet.getPhysicalNumberOfRows(); i++) {
				Row row = userSheet.getRow(i);
				ExcelDTO dto = new ExcelDTO();
				dto.setUsername(row.getCell(0) + "");
				dto.setDealerUsername(row.getCell(4) + "");
				userIdList.add(dto);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		try {
			Sheet dealSheet = workbook.getSheetAt(1);
			for(int i=0; i<dealSheet.getPhysicalNumberOfRows(); i++) {
				Row row = dealSheet.getRow(i);
				if(row!=null) {
					ExcelDTO dto = new ExcelDTO();
					dto.setUsername(row.getCell(0) + "");
					dto.setCharge(row.getCell(1) + "");
					dto.setService(row.getCell(2) + "");
					dto.setCreateDate(row.getCell(3) + "");
					dealLists.add(dto);
				}
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	
		for(ExcelDTO user : userIdList) {
			for(ExcelDTO dto : dealLists) {
				if(dto.getUsername().equals(user.getUsername())) {
					dto.setDealerUsername(user.getDealerUsername());
					relatedUsers.add(dto);
				}
			}
		}
		Workbook downloadWorkbook = new XSSFWorkbook();
		Sheet calculationExcel = downloadWorkbook.createSheet("정산");
		int rowCount = 0; // 데이터가 저장될 행
		int cellNumber = 0;
		Row bodyRow = null;
		Cell bodyCell = null;
		for(int i=0; i<relatedUsers.size();i++) {
			bodyRow = calculationExcel.createRow(rowCount++);
			for(int x=0;x<4;x++) {
				bodyCell = bodyRow.createCell(cellNumber);
				bodyCell.setCellValue(relatedUsers.get(i).getUsername());
				
				bodyCell = bodyRow.createCell(cellNumber+1);
				bodyCell.setCellValue(relatedUsers.get(i).getCharge());
				
				bodyCell = bodyRow.createCell(cellNumber+2);
				bodyCell.setCellValue(relatedUsers.get(i).getService());
				
				bodyCell = bodyRow.createCell(cellNumber+3);
				bodyCell.setCellValue(relatedUsers.get(i).getCreateDate());
				
				bodyCell = bodyRow.createCell(cellNumber+4);
				bodyCell.setCellValue(relatedUsers.get(i).getDealerUsername());
			}
		}
		String fileName = "snstomo";
		res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
		ServletOutputStream servletOutputStream = res.getOutputStream();

		downloadWorkbook.write(servletOutputStream);
		downloadWorkbook.close();
		servletOutputStream.flush();
		servletOutputStream.close();
	}
}
