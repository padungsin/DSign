package com.openfog.condo.dsign.account.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.account.model.Account;
import com.openfog.condo.dsign.account.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<Account> list() {
		try {
			return accountService.list();
		} catch (Exception e) {
			return new ArrayList<Account>();
		}
	}

	@RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
	public Account get(@PathVariable Long accountId) {
		try {
			return accountService.get(accountId);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.PUT)
	public Account search(@RequestBody Account account) {
		try {
			return accountService.save(account);
		} catch (Exception e) {
			return null;
		}
	}

	@RequestMapping(value = "/delete/{accountId}", method = RequestMethod.DELETE)
	public void delete(@PathVariable Long accountId) {
		try {
			accountService.delete(accountId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
