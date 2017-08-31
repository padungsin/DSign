package com.openfog.condo.dsign.test.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.watermeter.service.WaterMeterService;

@RestController
@RequestMapping("/download")
public class TestController {

	@Autowired
	private WaterMeterService watermeterService;

	@RequestMapping("/form/Water.xlsx")
	public void downloadWaterForm(HttpServletRequest request, HttpServletResponse response) {
		File output;
		try {
			output = watermeterService.generateWaterForm();

			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment; filename=" + output.getName());

			response.getOutputStream().write(FileUtils.readFileToByteArray(output));
			response.getOutputStream().flush();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
}
