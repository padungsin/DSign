package com.openfog.condo.dsign.util;

import java.util.Date;

import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;

public class ExcelUtil {

	public static void copySheet(XSSFWorkbook oldWorkbook, XSSFWorkbook newWorkbook, String sheetName) {

		// Copy style source
		final StylesTable oldStylesSource = oldWorkbook.getStylesSource();
		final StylesTable newStylesSource = newWorkbook.getStylesSource();

		oldStylesSource.getFonts().forEach(font -> newStylesSource.putFont(font, true));
		oldStylesSource.getFills().forEach(fill -> newStylesSource.putFill(new XSSFCellFill(fill.getCTFill())));
		oldStylesSource.getBorders().forEach(border -> newStylesSource.putBorder(new XSSFCellBorder(border.getCTBorder())));

		final XSSFSheet oldSheet = oldWorkbook.getSheet(sheetName);
		final XSSFSheet newSheet = newWorkbook.createSheet(oldSheet.getSheetName());

		newSheet.setDefaultRowHeight(oldSheet.getDefaultRowHeight());
		newSheet.setDefaultColumnWidth(oldSheet.getDefaultColumnWidth());

		// Copy content
		for (int rowNumber = oldSheet.getFirstRowNum(); rowNumber <= oldSheet.getLastRowNum(); rowNumber++) {
			final XSSFRow oldRow = oldSheet.getRow(rowNumber);
			// System.out.println("Row " + rowNumber);
			if (oldRow != null) {

				final XSSFRow newRow = newSheet.createRow(rowNumber);
				newRow.setHeight(oldRow.getHeight());

				for (int columnNumber = oldRow.getFirstCellNum(); columnNumber <= oldRow.getLastCellNum(); columnNumber++) {

					// System.out.println("Column " + columnNumber);
					newSheet.setColumnWidth(columnNumber, oldSheet.getColumnWidth(columnNumber));

					final XSSFCell oldCell = oldRow.getCell(columnNumber);
					if (oldCell != null) {
						final XSSFCell newCell = newRow.createCell(columnNumber);

						// Copy value
						setCellValue(newCell, getCellValue(oldCell));

						// Copy style
						XSSFCellStyle newCellStyle = newWorkbook.createCellStyle();
						newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
						newCell.setCellStyle(newCellStyle);
					}
				}
			}
		}

	}

	public static void setCellValue(final XSSFCell cell, final Object value) {
		if (value instanceof Boolean) {
			cell.setCellValue((boolean) value);
		} else if (value instanceof Byte) {
			cell.setCellValue((byte) value);
		} else if (value instanceof Double) {
			cell.setCellValue((double) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((int) value);
		} else if (value instanceof Date) {
			cell.setCellValue(DateUtil.formatDate((Date) value, "dd/MM/yyyy"));
		} else if (value instanceof String) {
			if (((String) value).startsWith("=")) {
				// Formula String
				cell.setCellFormula(((String) value).substring(1));
			} else {
				cell.setCellValue((String) value);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public static Object getCellValue(final XSSFCell cell) {

		System.out.println(cell.getCellTypeEnum());
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			return cell.getBooleanCellValue(); // boolean
		case ERROR:
			return cell.getErrorCellValue(); // byte
		case NUMERIC:
			return cell.getNumericCellValue(); // double
		case STRING:
			return cell.getStringCellValue(); // String
		case FORMULA:
			return "=" + cell.getCellFormula(); // String for formula
		case BLANK:
			return cell.getStringCellValue(); // String
		default:
			throw new IllegalArgumentException();
		}
	}
	
	
	public static Object getCellValue(XSSFSheet sheet, int rownum, int cellnum) {
		//System.out.println("Cell: " + rownum + ", " + cellnum);
		XSSFRow row = sheet.getRow(rownum);

		XSSFCell cell = row.getCell(cellnum);
		
		return getCellValue(cell);
		
	}
	

	public static void createCellValue(XSSFSheet sheet, int rownum, int cellnum, final Object value, XSSFCellStyle cellStyle) {

		//System.out.println("Cell: " + rownum + ", " + cellnum);
		XSSFRow row = sheet.getRow(rownum);

		XSSFCell cell = row.createCell(cellnum);
		cell.setCellStyle(cellStyle);

		setCellValue(cell, value);

	}
	
	public static void createCellValue(XSSFSheet sheet, int rownum, int cellnum, final Object value) {

		//System.out.println("Cell: " + rownum + ", " + cellnum);
		XSSFRow row = sheet.getRow(rownum);

		XSSFCell cell = row.createCell(cellnum);

		setCellValue(cell, value);

	}

}
