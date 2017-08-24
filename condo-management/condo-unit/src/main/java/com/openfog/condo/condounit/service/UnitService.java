package com.openfog.condo.condounit.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.condounit.model.Unit;
import com.openfog.condo.condounit.repository.UnitRepository;
import com.openfog.condo.condounit.service.UnitCriteria.SearchBy;

@Service
@Transactional
public class UnitService {
	@Autowired
	private UnitRepository unitRepository;

	public Unit get(Long id) {
		return unitRepository.findOne(id);
	}

	public Page<Unit> search(UnitCriteria criteria) throws Exception {

		if (criteria.getSearchBy().equals(SearchBy.no)) {
			return unitRepository.findAllByNo(criteria.getSearchText(), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.roomno)) {
			return unitRepository.findAllByRoomNo(criteria.getSearchText(), createPageRequest(criteria));
		}
		if (criteria.getSearchBy().equals(SearchBy.floor)) {
			return unitRepository.findAllByFloor(Integer.parseInt(criteria.getSearchText()),
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

	private Pageable createPageRequest(UnitCriteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
