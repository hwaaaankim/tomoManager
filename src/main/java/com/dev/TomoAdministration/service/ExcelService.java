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
import com.dev.TomoAdministration.dto.HiddenExcelDTO;

@Service
public class ExcelService {

	
	public void makeHiddenExcel(MultipartFile file, HttpServletResponse res) throws IOException {
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		List<HiddenExcelDTO> allList = new ArrayList<HiddenExcelDTO>();

		try {
			Sheet productSheet = workbook.getSheetAt(2);
			
			for (int i = 0; i < productSheet.getPhysicalNumberOfRows(); i++) {
				Row row = productSheet.getRow(i);
				if (row != null) {
					if(!(row.getCell(9) + "").equals("NULL")) {
						HiddenExcelDTO dto = new HiddenExcelDTO();
						dto.setProductCode(row.getCell(1) + "");
						String[] colors = {"1"};
						if((row.getCell(9) + "").indexOf(",") > 0) {
							colors = (row.getCell(9) + "").split(",");
						}else {
							colors[0] = row.getCell(9) + "";
						}
						dto.setColorCode(colors);
						allList.add(dto);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		

		Workbook downloadWorkbook = new XSSFWorkbook();
		Sheet colorsExcel = downloadWorkbook.createSheet("color");
		int rowCount = 0; // 데이터가 저장될 행
		int cellNumber = 0;
		Row bodyRow = null;
		Cell bodyCell = null;
		for (int i = 0; i < allList.size(); i++) {
			for (int x = 0; x < allList.get(i).getColorCode().length; x++) {
				bodyRow = colorsExcel.createRow(rowCount++);
				bodyCell = bodyRow.createCell(cellNumber);
				bodyCell.setCellValue(allList.get(i).getProductCode());

				bodyCell = bodyRow.createCell(cellNumber + 1);
				bodyCell.setCellValue(allList.get(i).getColorCode()[x]);
				
			}
		}

		String fileName = "색상";
		res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
		ServletOutputStream servletOutputStream = res.getOutputStream();

		downloadWorkbook.write(servletOutputStream);
		downloadWorkbook.close();
		servletOutputStream.flush();
		servletOutputStream.close();
	}
	
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
			System.out.println(e);
		}
		// 9, 15

		try {
			Sheet dajSheet = workbook.getSheetAt(1);
			for (int i = 0; i < dajSheet.getPhysicalNumberOfRows(); i++) {
				Row row = dajSheet.getRow(i);
				if (row != null) {
					ExcelDTO dto = new ExcelDTO();
					dto.setUsername(row.getCell(0) + "");
//					dto.setEmail(row.getCell(1) + "");
					dajMember.add(dto);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		try {
			Sheet muraSheet = workbook.getSheetAt(2);
			for (int i = 0; i < muraSheet.getPhysicalNumberOfRows(); i++) {
				Row row = muraSheet.getRow(i);
				if (row != null) {
					ExcelDTO dto = new ExcelDTO();
					dto.setUsername(row.getCell(0) + "");
//					dto.setEmail(row.getCell(1) + "");
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
	    List<CalculDTO> allList = new ArrayList<>();
	    List<CalculDTO> dajMember = new ArrayList<>();
	    List<CalculDTO> dajCalcul = new ArrayList<>();
	    List<CalculDTO> murachMember = new ArrayList<>();
	    List<CalculDTO> murachCalcul = new ArrayList<>();

	    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
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
	            Sheet murachSheet = workbook.getSheetAt(2);
	            for (int i = 0; i < murachSheet.getPhysicalNumberOfRows(); i++) {
	                Row row = murachSheet.getRow(i);
	                if (row != null) {
	                    CalculDTO dto = new CalculDTO();
	                    dto.setUsername(row.getCell(0) + "");
	                    murachMember.add(dto);
	                }
	            }
	        } catch (Exception e) {
	            System.out.println(e);
	        }
	    } // <-- 이 블록이 끝나면 workbook이 자동으로 닫히고, InputStream도 닫혀서 임시파일 삭제 에러 발생 안 함

	    // 분류 작업
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

	    // daj 시트 생성
	    Sheet dajExcel = downloadWorkbook.createSheet("daj.gppg.payment");
	    int rowCount = 0;
	    for (CalculDTO dto : dajCalcul) {
	        Row row = dajExcel.createRow(rowCount++);
	        row.createCell(0).setCellValue(dto.getUsername());
	        row.createCell(1).setCellValue(dto.getBalance());
	        row.createCell(2).setCellValue(dto.getAmount());
	        row.createCell(3).setCellValue(dto.getMethod());
	        row.createCell(4).setCellValue(dto.getStatus());
	        row.createCell(5).setCellValue(dto.getMemo());
	        row.createCell(6).setCellValue(dto.getStartDate());
	        row.createCell(7).setCellValue(dto.getEndDate());
	    }

	    // murach 시트 생성
	    Sheet murachExcel = downloadWorkbook.createSheet("murach.payment");
	    rowCount = 0;
	    for (CalculDTO dto : murachCalcul) {
	        Row row = murachExcel.createRow(rowCount++);
	        row.createCell(0).setCellValue(dto.getUsername());
	        row.createCell(1).setCellValue(dto.getBalance());
	        row.createCell(2).setCellValue(dto.getAmount());
	        row.createCell(3).setCellValue(dto.getMethod());
	        row.createCell(4).setCellValue(dto.getStatus());
	        row.createCell(5).setCellValue(dto.getMemo());
	        row.createCell(6).setCellValue(dto.getStartDate());
	        row.createCell(7).setCellValue(dto.getEndDate());
	    }

	    String fileName = "snstomo";
	    res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

	    try (ServletOutputStream out = res.getOutputStream()) {
	        downloadWorkbook.write(out);
	        downloadWorkbook.close();
	        out.flush();
	    }
	}
}
