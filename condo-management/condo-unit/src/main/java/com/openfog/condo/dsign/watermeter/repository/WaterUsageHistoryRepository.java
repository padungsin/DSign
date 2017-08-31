package com.openfog.condo.dsign.watermeter.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.watermeter.model.WaterUsageHistory;

public interface WaterUsageHistoryRepository extends CrudRepository<WaterUsageHistory, Long>, PagingAndSortingRepository<WaterUsageHistory, Long> {

	public List<WaterUsageHistory> findAll();

	public List<WaterUsageHistory> findAllByMeterId(Long meterId);

	public Page<WaterUsageHistory> findAllByMeterId(Long meterId, Pageable pageable);

}
