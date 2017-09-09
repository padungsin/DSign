package com.openfog.condo.dsign.utility.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.utility.model.NumericConstant;

public interface NumericConstantRepository extends CrudRepository<NumericConstant, Long>, PagingAndSortingRepository<NumericConstant, Long> {

	public NumericConstant findByConstantKey(String key);

}
