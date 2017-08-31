package com.openfog.condo.dsign.admin.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.owner.repository.OwnerRepository;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.repository.UnitRepository;
import com.openfog.condo.dsign.watermeter.model.WaterMeter;
import com.openfog.condo.dsign.watermeter.repository.WaterMeterRepository;

@Service
@Transactional
public class AdminService {
	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private WaterMeterRepository waterMeterRepository;

	@SuppressWarnings("rawtypes")
	public HashMap<String, List> bulkImport(File ownerFile) throws IOException, ParseException {

		int no = 0;
		int roomNo = 1;
		int floor = 2;
		int area = 3;
		int firstTransferDate = 4;
		int ownerName = 5;

		int waterMeterNo = 6;
		int waterMeterCurrentUnit = 7;
		int waterMeterUnitDate = 8;

		SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yy");

		List<Owner> owners = new ArrayList<Owner>();
		List<Unit> units = new ArrayList<Unit>();
		List<String> lines = FileUtils.readLines(ownerFile, "UTF-8");

		boolean first = true;
		for (String line : lines) {
			if (first) {
				first = false;
				continue;
			}
			String[] elements = StringUtils.splitPreserveAllTokens(line, "|");

			Unit unit = new Unit();
			unit.setNo(elements[no]);
			unit.setRoomNo(elements[roomNo]);

			String[] floors = StringUtils.split(elements[floor], "-");
			unit.setFloor(Integer.parseInt(floors[0]));
			if (floors.length > 1) {
				unit.setFloor2(Integer.parseInt(floors[1]));
			}

			unit.setArea(Double.parseDouble(elements[area]));

			try {
				unit.setFirstTransferDate(sf.parse(elements[firstTransferDate]));
			} catch (Exception e) {
			}

			unitRepository.save(unit);

			Owner owner = new Owner();
			owner.setName(elements[ownerName]);
			owner.setUnitId(unit.getId());
			owner.setTransferDate(unit.getFirstTransferDate());
			owner.setActive(true);

			WaterMeter waterMeter = new WaterMeter();
			waterMeter.setNo(elements[waterMeterNo]);
			waterMeter.setCurrentUnit(Integer.parseInt(elements[waterMeterCurrentUnit]));
			waterMeter.setNoteDate(sf.parse(elements[waterMeterUnitDate]));
		
			waterMeter.setUnitId(unit.getId());
			
			
			waterMeterRepository.save(waterMeter);

			ownerRepository.save(owner);
			owners.add(owner);

			units.add(unit);

		}

		HashMap<String, List> result = new HashMap<String, List>();
		result.put("Units", units);
		result.put("Owners", owners);

		return result;

	}

}
