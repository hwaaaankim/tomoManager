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

import com.dev.TomoAdministration.dto.CalculDTO;
import com.dev.TomoAdministration.dto.ExcelDTO;

@Service
public class ExcelService {

	public void makeExcelProcess(MultipartFile file, HttpServletResponse res) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		List<ExcelDTO> allList = new ArrayList<ExcelDTO>();

		List<ExcelDTO> dajMember = new ArrayList<ExcelDTO>();
		List<ExcelDTO> dajCalcul = new ArrayList<ExcelDTO>();

		List<ExcelDTO> murachMember = new ArrayList<ExcelDTO>();
		List<ExcelDTO> murachCalcul = new ArrayList<ExcelDTO>();
//	
//		try {
//			Sheet userSheet = workbook.getSheetAt(0);
//			for(int i=0; i<userSheet.getPhysicalNumberOfRows(); i++) {
//				Row row = userSheet.getRow(i);
//				ExcelDTO dto = new ExcelDTO();
//				dto.setUsername(row.getCell(0) + "");
//				dto.setDealerUsername(row.getCell(4) + "");
//				userIdList.add(dto);
//			}
//		}catch(Exception e) {
//			System.out.println(e);
//		}

		try {
			Sheet dealSheet = workbook.getSheetAt(1);
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
			System.out.println(e);
		}

		try {
			Sheet dajSheet = workbook.getSheetAt(2);
			for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
				Row row = dajSheet.getRow(i);
				if (row != null) {
					ExcelDTO dto = new ExcelDTO();
					dto.setUsername(row.getCell(0) + "");
					dto.setEmail(row.getCell(1) + "");
					dajMember.add(dto);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			Sheet dajSheet = workbook.getSheetAt(3);
			for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
				Row row = dajSheet.getRow(i);
				if (row != null) {
					ExcelDTO dto = new ExcelDTO();
					dto.setUsername(row.getCell(0) + "");
					dto.setEmail(row.getCell(1) + "");
					murachMember.add(dto);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		for (ExcelDTO user : dajMember) {
			for (ExcelDTO dto : allList) {
				if (dto.getUsername().equals(user.getUsername())) {
					dto.setDealerUsername(user.getDealerUsername());
					dajCalcul.add(dto);
				}
			}
		}

		for (ExcelDTO user : murachMember) {
			for (ExcelDTO dto : allList) {
				if (dto.getUsername().equals(user.getUsername())) {
					dto.setDealerUsername(user.getDealerUsername());
					murachCalcul.add(dto);
				}
			}
		}

		Workbook downloadWorkbook = new XSSFWorkbook();
		Sheet dajExcel = downloadWorkbook.createSheet("daj.gppg.calculation");
		int rowCount = 0; // 데이터가 저장될 행
		int cellNumber = 0;
		Row bodyRow = null;
		Cell bodyCell = null;
		for (int i = 0; i < dajCalcul.size(); i++) {
			bodyRow = dajExcel.createRow(rowCount++);
			for (int x = 0; x < 4; x++) {
				bodyCell = bodyRow.createCell(cellNumber);
				bodyCell.setCellValue(dajCalcul.get(i).getUsername());

				bodyCell = bodyRow.createCell(cellNumber + 1);
				bodyCell.setCellValue(dajCalcul.get(i).getEmail());

				bodyCell = bodyRow.createCell(cellNumber + 2);
				bodyCell.setCellValue(dajCalcul.get(i).getCharge());

				bodyCell = bodyRow.createCell(cellNumber + 3);
				bodyCell.setCellValue(dajCalcul.get(i).getQuantity());

				bodyCell = bodyRow.createCell(cellNumber + 4);
				bodyCell.setCellValue(dajCalcul.get(i).getService());

				bodyCell = bodyRow.createCell(cellNumber + 5);
				bodyCell.setCellValue(dajCalcul.get(i).getStatus());

				bodyCell = bodyRow.createCell(cellNumber + 6);
				bodyCell.setCellValue(dajCalcul.get(i).getCreateDate());
			}
		}
		Sheet murachExcel = downloadWorkbook.createSheet("murach.calculation");
		rowCount = 0; // 데이터가 저장될 행
		cellNumber = 0;
		bodyRow = null;
		bodyCell = null;
		for (int i = 0; i < murachCalcul.size(); i++) {
			bodyRow = murachExcel.createRow(rowCount++);
			for (int x = 0; x < 4; x++) {
				bodyCell = bodyRow.createCell(cellNumber);
				bodyCell.setCellValue(murachCalcul.get(i).getUsername());

				bodyCell = bodyRow.createCell(cellNumber + 1);
				bodyCell.setCellValue(murachCalcul.get(i).getEmail());

				bodyCell = bodyRow.createCell(cellNumber + 2);
				bodyCell.setCellValue(murachCalcul.get(i).getCharge());

				bodyCell = bodyRow.createCell(cellNumber + 3);
				bodyCell.setCellValue(murachCalcul.get(i).getQuantity());

				bodyCell = bodyRow.createCell(cellNumber + 4);
				bodyCell.setCellValue(murachCalcul.get(i).getService());

				bodyCell = bodyRow.createCell(cellNumber + 5);
				bodyCell.setCellValue(murachCalcul.get(i).getStatus());

				bodyCell = bodyRow.createCell(cellNumber + 6);
				bodyCell.setCellValue(murachCalcul.get(i).getCreateDate());
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

	public void makeCalculationProcess(MultipartFile file, HttpServletResponse res) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		List<CalculDTO> allList = new ArrayList<CalculDTO>();

		List<CalculDTO> dajMember = new ArrayList<CalculDTO>();
		List<CalculDTO> dajCalcul = new ArrayList<CalculDTO>();

		List<CalculDTO> murachMember = new ArrayList<CalculDTO>();
		List<CalculDTO> murachCalcul = new ArrayList<CalculDTO>();
//	
//		try {
//			Sheet userSheet = workbook.getSheetAt(0);
//			for(int i=0; i<userSheet.getPhysicalNumberOfRows(); i++) {
//				Row row = userSheet.getRow(i);
//				ExcelDTO dto = new ExcelDTO();
//				dto.setUsername(row.getCell(0) + "");
//				dto.setDealerUsername(row.getCell(4) + "");
//				userIdList.add(dto);
//			}
//		}catch(Exception e) {
//			System.out.println(e);
//		}

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
			System.out.println(e);
		}
		
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
			System.out.println(e);
		}
		
		try {
			Sheet dajSheet = workbook.getSheetAt(2);
			for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
				Row row = dajSheet.getRow(i);
				if (row != null) {
					CalculDTO dto = new CalculDTO();
					dto.setUsername(row.getCell(0) + "");
					murachMember.add(dto);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		for (CalculDTO user : dajMember) {
			for (CalculDTO dto : allList) {
				if (dto.getUsername().equals(user.getUsername())) {
					dajCalcul.add(dto);
				}
			}
		}

		for (CalculDTO user : murachMember) {
			for (CalculDTO dto : allList) {
				if (dto.getUsername().equals(user.getUsername())) {
					murachCalcul.add(dto);
				}
			}
		}

		Workbook downloadWorkbook = new XSSFWorkbook();
		Sheet dajExcel = downloadWorkbook.createSheet("daj.gppg.payment");
		int rowCount = 0; // 데이터가 저장될 행
		int cellNumber = 0;
		Row bodyRow = null;
		Cell bodyCell = null;
		
		for (int i = 0; i < dajCalcul.size(); i++) {
			bodyRow = dajExcel.createRow(rowCount++);
			bodyCell = bodyRow.createCell(cellNumber);
			bodyCell.setCellValue(dajCalcul.get(i).getUsername());

			bodyCell = bodyRow.createCell(cellNumber + 1);
			bodyCell.setCellValue(dajCalcul.get(i).getBalance());

			bodyCell = bodyRow.createCell(cellNumber + 2);
			bodyCell.setCellValue(dajCalcul.get(i).getAmount());

			bodyCell = bodyRow.createCell(cellNumber + 3);
			bodyCell.setCellValue(dajCalcul.get(i).getMethod());

			bodyCell = bodyRow.createCell(cellNumber + 4);
			bodyCell.setCellValue(dajCalcul.get(i).getStatus());

			bodyCell = bodyRow.createCell(cellNumber + 5);
			bodyCell.setCellValue(dajCalcul.get(i).getMemo());

			bodyCell = bodyRow.createCell(cellNumber + 6);
			bodyCell.setCellValue(dajCalcul.get(i).getStartDate());

			bodyCell = bodyRow.createCell(cellNumber + 7);
			bodyCell.setCellValue(dajCalcul.get(i).getEndDate());
		}
		
		Sheet murachExcel = downloadWorkbook.createSheet("murach.payment");
		rowCount = 0; // 데이터가 저장될 행
		cellNumber = 0;
		bodyRow = null;
		bodyCell = null;
		for (int i = 0; i < murachCalcul.size(); i++) {
			bodyRow = murachExcel.createRow(rowCount++);
			bodyCell = bodyRow.createCell(cellNumber);
			bodyCell.setCellValue(murachCalcul.get(i).getUsername());

			bodyCell = bodyRow.createCell(cellNumber + 1);
			bodyCell.setCellValue(murachCalcul.get(i).getBalance());

			bodyCell = bodyRow.createCell(cellNumber + 2);
			bodyCell.setCellValue(murachCalcul.get(i).getAmount());

			bodyCell = bodyRow.createCell(cellNumber + 3);
			bodyCell.setCellValue(murachCalcul.get(i).getMethod());

			bodyCell = bodyRow.createCell(cellNumber + 4);
			bodyCell.setCellValue(murachCalcul.get(i).getStatus());

			bodyCell = bodyRow.createCell(cellNumber + 5);
			bodyCell.setCellValue(murachCalcul.get(i).getMemo());

			bodyCell = bodyRow.createCell(cellNumber + 6);
			bodyCell.setCellValue(murachCalcul.get(i).getStartDate());

			bodyCell = bodyRow.createCell(cellNumber + 7);
			bodyCell.setCellValue(murachCalcul.get(i).getEndDate());
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
