package com.openfog.condo.dsign.unit.service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.unit.criteria.UnitCriteria;
import com.openfog.condo.dsign.unit.criteria.UnitCriteria.SearchBy;
import com.openfog.condo.dsign.unit.model.Unit;
import com.openfog.condo.dsign.unit.repository.UnitRepository;

@Service
@Transactional
public class UnitService {
	@Autowired
	private UnitRepository unitRepository;

	public Unit get(Long id) {
		return unitRepository.findOne(id);
	}

	public List<Unit> list() {
		return unitRepository.findAll();
	}

	public Page<Unit> search(UnitCriteria criteria) throws Exception {

		if (criteria.getSearchBy().equals(SearchBy.no)) {
			return unitRepository.findAllByNo((String)criteria.getParameters().get("no"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.roomNo)) {
			return unitRepository.findAllByRoomNo((String)criteria.getParameters().get("roomNo"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.floor)) {
			return unitRepository.findAllByFloor(Integer.parseInt((String)criteria.getParameters().get("floor")),
					createPageRequest(criteria));
		}

		return null;
	}

	public Unit save(Unit unit) {
		return unitRepository.save(unit);
	}

	public List<Unit> save(List<Unit> units) {

		for (Unit unit : units) {
			unitRepository.save(unit);
		}
		return units;
	}

	public List<Unit> bulkImport(File unitFile) throws IOException, ParseException {

		int no = 0;
		int roomNo = 1;
		int floor = 2;
		int area = 3;
		int firstTransferDate = 4;

		SimpleDateFormat sf = new SimpleDateFormat("MM/dd/yy");

		List<Unit> units = new ArrayList<Unit>();
		List<String> lines = FileUtils.readLines(unitFile, "UTF-8");

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
			units.add(unit);

		}

		return units;

	}

	private Pageable createPageRequest(UnitCriteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
