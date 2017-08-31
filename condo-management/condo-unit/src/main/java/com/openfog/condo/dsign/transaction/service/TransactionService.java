package com.openfog.condo.dsign.transaction.service;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.account.model.Account;
import com.openfog.condo.dsign.account.repository.AccountRepository;
import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria;
import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria.SearchBy;
import com.openfog.condo.dsign.transaction.model.Transaction;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionStatus;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionType;
import com.openfog.condo.dsign.transaction.repository.TransactionRepository;

@Service
@Transactional
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	public Transaction get(Long id) {
		return transactionRepository.findOne(id);
	}

	public List<Transaction> list() {
		return transactionRepository.findAll();
	}

	public Page<Transaction> search(TransactionCriteria criteria) throws Exception {

		if (criteria.getSearchBy().equals(SearchBy.date)) {
			return transactionRepository.findAllByDate((String) criteria.getParameters().get("transactionDate"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.month)) {
			return transactionRepository.findAllByMonth((String) criteria.getParameters().get("transactionMonth"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.year)) {
			return transactionRepository.findAllByYear((String) criteria.getParameters().get("transactionYear"), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.dateAndtype)) {
			return transactionRepository.findAllByDateAndType((String) criteria.getParameters().get("transactionDate"), TransactionType.valueOf((String) criteria.getParameters().get("transactionType")), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.monthAndType)) {
			return transactionRepository.findAllByMonthAndType((String) criteria.getParameters().get("transactionMonth"), TransactionType.valueOf((String) criteria.getParameters().get("transactionType")), createPageRequest(criteria));
		} else if (criteria.getSearchBy().equals(SearchBy.yearAndType)) {
			return transactionRepository.findAllByYearAndType((String) criteria.getParameters().get("transactionMonth"), TransactionType.valueOf((String) criteria.getParameters().get("transactionType")), createPageRequest(criteria));
		}

		return null;
	}

	public Transaction save(Transaction transaction) {

		Account account = accountRepository.findOne(transaction.getAccountId());

		transaction.setBalanceBefore(account.getBalance());

		if (transaction.getTransactionType().equals(TransactionType.income)) {

			account.setBalance(account.getBalance() + transaction.getAmount());
		} else {

			account.setBalance(account.getBalance() - transaction.getAmount());
		}

		transaction.setBalanceAfter(account.getBalance());

		transaction.setTransactionStatus(TransactionStatus.normal);
		return transactionRepository.save(transaction);
	}

	public List<Transaction> save(List<Transaction> transactions) {

		for (Transaction transaction : transactions) {
			transactionRepository.save(transaction);
		}
		return transactions;
	}

	public void cancelTransaction(Long id) throws IncorrectTransactionStatusException {
		Transaction oldTransaction = transactionRepository.findOne(id);
		if (oldTransaction.getTransactionStatus().equals(TransactionStatus.voidTransaction)) {
			throw new IncorrectTransactionStatusException("Transaction " + oldTransaction.getId() + " is cancelled.");
		}

		oldTransaction.setTransactionStatus(TransactionStatus.voidTransaction);

		Account account = accountRepository.findOne(oldTransaction.getAccountId());

		Transaction voidTransaction = new Transaction();

		voidTransaction.setAccountId(oldTransaction.getAccountId());
		voidTransaction.setReferenceTransaction(oldTransaction.getId());
		voidTransaction.setAmount(oldTransaction.getAmount());
		voidTransaction.setDetail("(Cancel) " + oldTransaction.getDetail());
		voidTransaction.setTransactionDate(new Date());
		voidTransaction.setTransactionStatus(TransactionStatus.voidTransactionRef);

		voidTransaction.setBalanceBefore(account.getBalance());
		if (oldTransaction.getTransactionType().equals(TransactionType.expense)) {
			voidTransaction.setTransactionType(TransactionType.income);
			account.setBalance(account.getBalance() + voidTransaction.getAmount());
		} else {
			voidTransaction.setTransactionType(TransactionType.expense);
			account.setBalance(account.getBalance() - voidTransaction.getAmount());
		}
		voidTransaction.setBalanceAfter(account.getBalance());

		accountRepository.save(account);
		transactionRepository.save(voidTransaction);
		transactionRepository.save(oldTransaction);

	}

	public Transaction adjustTransaction(Transaction newTransaction) throws IncorrectTransactionStatusException {

		Transaction oldTransaction = transactionRepository.findOne(newTransaction.getId());
		if (oldTransaction.getTransactionStatus().equals(TransactionStatus.voidTransaction)) {
			throw new IncorrectTransactionStatusException("Transaction " + oldTransaction.getId() + " void.");
		}

		oldTransaction.setTransactionStatus(TransactionStatus.voidTransaction);

		Account account = accountRepository.findOne(oldTransaction.getAccountId());

		Transaction voidTransaction = new Transaction();

		voidTransaction.setAccountId(oldTransaction.getAccountId());
		voidTransaction.setReferenceTransaction(oldTransaction.getId());
		voidTransaction.setAmount(oldTransaction.getAmount());
		voidTransaction.setDetail("(Cancel) " + oldTransaction.getDetail());
		voidTransaction.setTransactionDate(new Date());
		voidTransaction.setTransactionStatus(TransactionStatus.voidTransactionRef);

		voidTransaction.setBalanceBefore(account.getBalance());
		if (oldTransaction.getTransactionType().equals(TransactionType.income)) {
			voidTransaction.setTransactionType(TransactionType.expense);
			account.setBalance(account.getBalance() - voidTransaction.getAmount());
		} else {
			voidTransaction.setTransactionType(TransactionType.income);
			account.setBalance(account.getBalance() + voidTransaction.getAmount());
		}
		voidTransaction.setBalanceAfter(account.getBalance());

		transactionRepository.save(voidTransaction);
		transactionRepository.save(oldTransaction);

		
		
		newTransaction.setBalanceBefore(account.getBalance());
		if (oldTransaction.getTransactionType().equals(TransactionType.income)) {
			newTransaction.setTransactionType(TransactionType.income);
			account.setBalance(account.getBalance() + newTransaction.getAmount());
		} else {
			oldTransaction.setTransactionType(TransactionType.expense);
			account.setBalance(account.getBalance() - newTransaction.getAmount());
		}

		newTransaction.setId(null);
		newTransaction.setBalanceAfter(account.getBalance());
		newTransaction.setReferenceTransaction(oldTransaction.getId());
		newTransaction.setTransactionStatus(TransactionStatus.normal);
		
		voidTransaction.setTransactionDate(new Date());

		transactionRepository.save(newTransaction);
		accountRepository.save(account);

		return newTransaction;

	}

	private Pageable createPageRequest(TransactionCriteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
