package com.openfog.condo.dsign.account.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.account.model.Account;
import com.openfog.condo.dsign.account.repository.AccountRepository;

@Service
@Transactional
public class AccountService {
	@Autowired
	private AccountRepository accountRepository;

	public Account get(Long id) {
		return accountRepository.findOne(id);
	}

	public List<Account> list() {
		return accountRepository.findAll();
	}

	public Account save(Account account) {
		return accountRepository.save(account);
	}

	public void delete(Long id) {
		Account account = accountRepository.findOne(id);
		accountRepository.delete(account);
	}

}
