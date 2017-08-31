package com.openfog.condo.dsign.transaction.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.openfog.condo.dsign.transaction.model.Transaction;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionType;

public interface TransactionRepository extends CrudRepository<Transaction, Long>, PagingAndSortingRepository<Transaction, Long> {

	public List<Transaction> findAll();

	//DATE_FORMAT(transaction_Date, "%Y-%m-%d")
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y-%m-%d') = :transactionDate")
	public Page<Transaction> findAllByDate(@Param("transactionDate") String transactionDate, Pageable pageable);
	
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y-%m') = :transactionMonth")
	public Page<Transaction> findAllByMonth(@Param("transactionMonth") String transactionMonth, Pageable pageable);
	
	
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y') = :transactionYear")
	public Page<Transaction> findAllByYear(@Param("transactionYear") String transactionYear, Pageable pageable);
	
	
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y-%m-%d') = :transactionDate and t.transactionType = :transactionType")
	public Page<Transaction> findAllByDateAndType(@Param("transactionDate") String transactionDate, @Param("transactionType")TransactionType transactionType, Pageable pageable);
	
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y-%m') = :transactionMonth and t.transactionType = :transactionType")
	public Page<Transaction> findAllByMonthAndType(@Param("transactionMonth") String transactionMonth, @Param("transactionType")TransactionType transactionType, Pageable pageable);
	
	
	@Query("Select t from Transaction t where DATE_FORMAT(t.transactionDate,'%Y') = :transactionYear and t.transactionType = :transactionType")
	public Page<Transaction> findAllByYearAndType(@Param("transactionYear") String transactionYear, @Param("transactionType")TransactionType transactionType, Pageable pageable);
	
}
