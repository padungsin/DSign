package com.openfog.condo.dsign.account.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.openfog.condo.dsign.account.model.Account;

public interface AccountRepository extends CrudRepository<Account, Long>, PagingAndSortingRepository<Account, Long> {

	public Account findByName(String name);

	public List<Account> findAll();

}
