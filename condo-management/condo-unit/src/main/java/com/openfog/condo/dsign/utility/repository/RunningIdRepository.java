package com.openfog.condo.dsign.utility.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.utility.model.RunningId;
import com.openfog.condo.dsign.utility.model.RunningId.IdType;

public interface RunningIdRepository extends CrudRepository<RunningId, Long>, PagingAndSortingRepository<RunningId, Long> {

	public RunningId findByIdType(IdType idType);

}
