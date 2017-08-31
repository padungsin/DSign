package com.openfog.condo.dsign.unit.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openfog.condo.dsign.unit.criteria.UnitCriteria;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.service.UnitService;

@RestController
@RequestMapping("/unit")
public class UnitController {

	@Autowired
	private UnitService unitService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Unit> list() {
		try {
			return unitService.list();
		} catch (Exception e) {
			return new ArrayList<Unit>();
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public Page<Unit> search(@RequestBody UnitCriteria criteria) {
		try {
			return unitService.search(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/{unitId}", method = RequestMethod.GET)
	public Unit get(@PathVariable Long unitId) {
		try {
			return unitService.get(unitId);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public Unit search(@RequestBody Unit unit) {
		try {
			return unitService.save(unit);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/delete/{unitId}", method = RequestMethod.DELETE)
	public Page<Unit> delete(@RequestBody UnitCriteria criteria) {
		try {
			return unitService.search(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public List<Unit> upload(@RequestParam("unitFile") MultipartFile file) {
		
		
		String name = "unitFile";
		if (!file.isEmpty()) {
			
			try {
				
				
				File unitFile = new File(name + "-uploaded");

				FileUtils.writeByteArrayToFile(unitFile, file.getBytes());
				unitService.bulkImport(unitFile);

			} catch (Exception e) {
				e.printStackTrace();
				return new ArrayList<Unit>();
			}
		}
		return new ArrayList<Unit>();
	}

}
