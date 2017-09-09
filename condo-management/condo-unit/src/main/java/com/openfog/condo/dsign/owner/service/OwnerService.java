package com.openfog.condo.dsign.owner.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.owner.criteria.OwnerCriteria;
import com.openfog.condo.dsign.owner.criteria.OwnerCriteria.SearchBy;
import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.owner.repository.OwnerRepository;

@Service
@Transactional
public class OwnerService {

	@Autowired
	private OwnerRepository ownerRepository;

	public Owner get(Long id) {
		return ownerRepository.findOne(id);
	}

	public List<Owner> list() {
		return ownerRepository.findAll();
	}

	public Page<Owner> search(OwnerCriteria criteria) throws Exception {

		if (criteria.getSearchBy().equals(SearchBy.active)) {
			return ownerRepository.findAllByActive(Boolean.getBoolean((String)criteria.getParameters().get("active")),
					createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.name)) {
			return ownerRepository.findAllByNameLike((String)criteria.getParameters().get("name"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.unit)) {
			return ownerRepository.findAllByUnitId(Long.parseLong((String)criteria.getParameters().get("unitId")),
					createPageRequest(criteria));
		} 

		return null;
	}

	public Owner save(Owner Owner) {
		return ownerRepository.save(Owner);
	}

	public List<Owner> save(List<Owner> Owners) {

		for (Owner Owner : Owners) {
			ownerRepository.save(Owner);
		}
		return Owners;
	}

	private Pageable createPageRequest(OwnerCriteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
