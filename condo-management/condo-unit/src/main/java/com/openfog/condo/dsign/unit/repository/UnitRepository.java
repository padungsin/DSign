package com.openfog.condo.dsign.unit.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.unit.model.Unit;

public interface UnitRepository extends CrudRepository<Unit, Long>, PagingAndSortingRepository<Unit, Long> {
	
	public Page<Unit> findAll(Pageable pageable);
	
	public Page<Unit> findAllByNo(String no, Pageable pageable);
	
	public Page<Unit> findAllByRoomNo(String roomNo, Pageable pageable);
	
	public Page<Unit> findAllByFloor(int floor, Pageable pageable);
	
	public List<Unit> findAll();
	
	public List<Unit> save(List<Unit> units);
	

}
