package com.openfog.condo.dsign.utility.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.utility.model.TextConstant;

public interface TextConstantRepository extends CrudRepository<TextConstant, Long>, PagingAndSortingRepository<TextConstant, Long> {

	public TextConstant findByConstantKey(String key);

}
