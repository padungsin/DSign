package com.openfog.condo.dsign.admin.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openfog.condo.dsign.admin.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/uploadUnit", method = RequestMethod.POST)
	public HashMap<String, List> uploadUnit(@RequestParam("unitFile") MultipartFile file) {

		String name = "unitFile";
		if (!file.isEmpty()) {

			try {

				File unitFile = new File(name + "-uploaded");

				FileUtils.writeByteArrayToFile(unitFile, file.getBytes());
				return adminService.bulkImport(unitFile);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
