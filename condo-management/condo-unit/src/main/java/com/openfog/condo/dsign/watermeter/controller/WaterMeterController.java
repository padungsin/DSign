package com.openfog.condo.dsign.watermeter.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openfog.condo.dsign.criteria.Criteria;
import com.openfog.condo.dsign.watermeter.model.WaterMeter;
import com.openfog.condo.dsign.watermeter.service.ImportTwiceException;
import com.openfog.condo.dsign.watermeter.service.WaterMeterService;

@RestController
@RequestMapping("/waterMeter")
public class WaterMeterController {

	
	@Value("${condomgt.downloadform.tempdir}")
	private String tempDir;
	
	@Autowired
	private WaterMeterService waterMeterService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<WaterMeter> list() {
		try {
			return waterMeterService.list();
		} catch (Exception e) {
			return new ArrayList<WaterMeter>();
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Page<WaterMeter> search(@RequestBody Criteria criteria) {
		try {
			return waterMeterService.list(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/{waterMeterId}", method = RequestMethod.GET)
	public WaterMeter get(@PathVariable Long waterMeterId) {
		try {
			return waterMeterService.get(waterMeterId);
		} catch (Exception e) {
			return null;
		}
	}
	
	@RequestMapping(value = "/{waterMeterNo}", method = RequestMethod.GET)
	public WaterMeter get(@PathVariable String waterMeterNo) {
		try {
			return waterMeterService.getByNo(waterMeterNo);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public WaterMeter search(@RequestBody WaterMeter waterMeter) {
		try {
			return waterMeterService.save(waterMeter);
		} catch (Exception e) {
			return null;
		}
	}


	@RequestMapping(value = "/uploadWaterUsage", method = RequestMethod.POST)
	public void uploadWaterUsage(@RequestParam("waterFile") MultipartFile file) {

		if (!file.isEmpty()) {

			try {
				
				File waterFile = new File(tempDir + "/" + file.getName());
				FileUtils.writeByteArrayToFile(waterFile, file.getBytes());
				
				waterMeterService.importWaterForm(waterFile);

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	@RequestMapping(value = "/downloadMergeWater/{month}", method = RequestMethod.GET)
	public void downloadWaterForm(HttpServletRequest request, HttpServletResponse response, @PathVariable String month) {
		File output;
		try {
			output = waterMeterService.mergeWaterInvoice(month);

			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment; filename=" + output.getName());

			response.getOutputStream().write(FileUtils.readFileToByteArray(output));
			response.getOutputStream().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	


}
