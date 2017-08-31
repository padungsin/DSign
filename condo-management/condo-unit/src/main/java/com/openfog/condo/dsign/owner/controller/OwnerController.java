package com.openfog.condo.dsign.owner.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.owner.criteria.OwnerCriteria;
import com.openfog.condo.dsign.owner.model.Owner;
import com.openfog.condo.dsign.owner.service.OwnerService;

@RestController
@RequestMapping("/owner")
public class OwnerController {

	@Autowired
	private OwnerService ownerService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Owner> list() {
		try {
			return ownerService.list();
		} catch (Exception e) {
			return new ArrayList<Owner>();
		}
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public Page<Owner> search(@RequestBody OwnerCriteria criteria) {
		try {
			return ownerService.search(criteria);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/{ownerId}", method = RequestMethod.GET)
	public Owner get(@PathVariable Long ownerId) {
		try {
			return ownerService.get(ownerId);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public Owner search(@RequestBody Owner owner) {
		try {
			return ownerService.save(owner);
		} catch (Exception e) {
			return null;
		}
	}

}
