package com.openfog.condo.dsign.watermeter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.criteria.Criteria;
import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.repository.UnitRepository;
import com.openfog.condo.dsign.util.DateUtil;
import com.openfog.condo.dsign.util.ExcelUtil;
import com.openfog.condo.dsign.watermeter.model.WaterMeter;
import com.openfog.condo.dsign.watermeter.model.WaterUsageHistory;
import com.openfog.condo.dsign.watermeter.repository.WaterMeterRepository;
import com.openfog.condo.dsign.watermeter.repository.WaterUsageHistoryRepository;

@Service
@Transactional
public class WaterMeterService {

	@Autowired
	private WaterMeterRepository waterMeterRepository;
	@Autowired
	private UnitRepository unitRepository;
	
	@Autowired
	private WaterUsageHistoryRepository waterUsageHistoryRepository;

	@Value("${condomgt.downloadform.template}")
	private String downloadFormFileName;

	@Value("${condomgt.downloadform.tempdir}")
	private String tempDir;

	private static int lastNoteDateRownum = 2;
	private static int lastNoteDateCellnum = 0;

	private static int currentNoteDateRownum = 2;
	private static int currentNoteDateCellnum = 4;

	private static int startRowNum = 5;
	private static int noCellnum = 0;
	private static int roomNoCellnum = 1;
	private static int meterNoCellnum = 2;
	private static int meterUnitCellnum = 3;
	private static int currentUnitCellnum = 4;

	private static String sheetName = "Water";

	public WaterMeter get(Long id) {
		return waterMeterRepository.findOne(id);
	}

	public WaterMeter getByNo(String no) {
		return waterMeterRepository.findByNo(no);
	}

	public List<WaterMeter> list() {
		return waterMeterRepository.findAll();
	}

	public Page<WaterMeter> list(Criteria criteria) throws Exception {

		return waterMeterRepository.findAll(createPageRequest(criteria));

	}

	public WaterMeter save(WaterMeter WaterMeter) {
		return waterMeterRepository.save(WaterMeter);
	}

	public List<WaterMeter> save(List<WaterMeter> WaterMeters) {

		for (WaterMeter WaterMeter : WaterMeters) {
			waterMeterRepository.save(WaterMeter);
		}
		return WaterMeters;
	}

	public File generateWaterForm() throws IOException, ParseException {

		FileInputStream excelFile = new FileInputStream(new File(downloadFormFileName));
		XSSFWorkbook templateWorkbook = new XSSFWorkbook(excelFile);
		XSSFWorkbook workbook = new XSSFWorkbook();

		ExcelUtil.copySheet(templateWorkbook, workbook, sheetName);

		XSSFSheet sheet = workbook.getSheet(sheetName);

		List<WaterMeter> waterMeterList = waterMeterRepository.findAll();
		ExcelUtil.createCellValue(sheet, lastNoteDateRownum, lastNoteDateCellnum, waterMeterList.get(0).getNoteDate());

		// all border style
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);

		// newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
		// newCell.setCellStyle(newCellStyle);

		int rownum = startRowNum;
		for (WaterMeter waterMeter : waterMeterList) {
			Unit unit = unitRepository.findOne(waterMeter.getUnitId());
			sheet.createRow(rownum);

			ExcelUtil.createCellValue(sheet, rownum, noCellnum, unit.getNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, roomNoCellnum, unit.getRoomNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, meterNoCellnum, waterMeter.getNo(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, meterUnitCellnum, waterMeter.getCurrentUnit(), cellStyle);
			ExcelUtil.createCellValue(sheet, rownum, currentUnitCellnum, "", cellStyle);

			rownum++;

		}

		String outputFileName = "Water-" + DateUtil.formatForFileName(new Date()) + ".xlsx";

		// Write over to the new file
		FileOutputStream fileOut = new FileOutputStream(tempDir + "/" + outputFileName);
		workbook.write(fileOut);
		templateWorkbook.close();
		workbook.close();
		fileOut.close();

		return new File(tempDir + "/" + outputFileName);

	}

	public File importWaterForm(File waterUsageFile) throws IOException, ParseException, ImportTwiceException {

		FileInputStream excelFile = null;
		XSSFWorkbook workbook = null;
		try {
			excelFile = new FileInputStream(waterUsageFile);
			workbook = new XSSFWorkbook(excelFile);

			XSSFSheet sheet = workbook.getSheet(sheetName);

			Date noteDate = DateUtil.parseDate((String) ExcelUtil.getCellValue(sheet, currentNoteDateRownum, currentNoteDateCellnum), "dd/MM/yyyy");

			WaterMeter tempMeter = waterMeterRepository.findOne(1l);
			if (DateUtil.isSameMonth(noteDate, tempMeter.getNoteDate())) {
				throw new ImportTwiceException();

			}

			// Copy content
			for (int rowNumber = startRowNum; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
				WaterMeter waterMeter = waterMeterRepository.findByNo((String) ExcelUtil.getCellValue(sheet, rowNumber, meterNoCellnum));
				WaterUsageHistory history = new WaterUsageHistory();
				history.setMeterId(waterMeter.getId());
				history.setNoteDate(noteDate);
				history.setPreviousUnit(waterMeter.getPreviousUnit());
				
				double currentUnit = (double) ExcelUtil.getCellValue(sheet, rowNumber, currentUnitCellnum);
				history.setCurrentUnit((int)currentUnit);
				history.setWaterUsage(history.getCurrentUnit() - history.getPreviousUnit());

				waterMeter.setPreviousUnit(history.getPreviousUnit());
				waterMeter.setCurrentUnit(history.getPreviousUnit());
				waterMeter.setNoteDate(noteDate);
				
				waterMeterRepository.save(waterMeter);
				waterUsageHistoryRepository.save(history);
			}

			return null;

			// generate invoice
		} catch (Exception e) {
			throw e;
		} finally {
			workbook.close();
			excelFile.close();
		}

	}

	private Pageable createPageRequest(Criteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
