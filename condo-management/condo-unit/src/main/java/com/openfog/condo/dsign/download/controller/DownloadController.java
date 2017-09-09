package com.openfog.condo.dsign.download.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.invoice.model.Invoice;
import com.openfog.condo.dsign.invoice.service.InvoiceService;
import com.openfog.condo.dsign.payment.model.Payment;
import com.openfog.condo.dsign.payment.service.PaymentService;
import com.openfog.condo.dsign.watermeter.service.WaterMeterService;

@RestController
@RequestMapping("/download")
public class DownloadController {

	private enum DownloadType {
		invoice, receipt
	}

	@Value("${condomgt.downloadform.tempdir}")
	private String tempDir;

	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private WaterMeterService waterMeterService;
	

	@RequestMapping(value = "/{downloadType}/{id}", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "downloadType") DownloadType downloadType, @PathVariable(name = "id") Long id) {
		File output = null;
		try {
			if (downloadType.equals(DownloadType.receipt)) {
				Payment payment = paymentService.get(id);
				output = new File(payment.getReciept());

			}else if(downloadType.equals(DownloadType.invoice)){
				Invoice invoice = invoiceService.get(id);
				output = new File(invoice.getPdf());
			}

			if (output == null) {
				return;
			}

			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment; filename=" + output.getName());

			response.getOutputStream().write(FileUtils.readFileToByteArray(output));
			response.getOutputStream().flush();

		} catch (Exception e) {
			return;
		}
	}
	

	@RequestMapping(value = "/downloadWaterForm", method = RequestMethod.GET)
	public void downloadWaterForm(HttpServletRequest request, HttpServletResponse response) {
		File output;
		try {
			output = waterMeterService.generateWaterForm();

			response.setContentType("application/pdf");
			response.addHeader("Content-Disposition", "attachment; filename=" + output.getName());

			response.getOutputStream().write(FileUtils.readFileToByteArray(output));
			response.getOutputStream().flush();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}
	

}
