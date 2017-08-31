package com.openfog.condo.dsign.watermeter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.watermeter.model.WaterMeter;

public interface WaterMeterRepository extends CrudRepository<WaterMeter, Long>, PagingAndSortingRepository<WaterMeter, Long> {

	public List<WaterMeter> findAll();

	public WaterMeter findByNo(String no);

	public Page<WaterMeter> findAll(Pageable pageable);

}
