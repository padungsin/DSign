package com.openfog.condo.dsign.owner.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.openfog.condo.dsign.owner.model.Owner;

public interface OwnerRepository extends CrudRepository<Owner, Long>, PagingAndSortingRepository<Owner, Long> {

	public List<Owner> findAll();

	public Page<Owner> findAllByActive(boolean active, Pageable pageable);

	public Page<Owner> findAllByUnitId(Long unitId, Pageable pageable);

	public Page<Owner> findByUnitIdAndActive(Long unitId, boolean active, Pageable pageable);

	@Query("Select o from Owner o where o.name like %:name%")
	public Page<Owner> findAllByNameLike(@Param("name")String name, Pageable pageable);

}
