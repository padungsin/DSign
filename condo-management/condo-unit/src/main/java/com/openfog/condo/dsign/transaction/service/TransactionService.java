package com.openfog.condo.dsign.transaction.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria;
import com.openfog.condo.dsign.transaction.criteria.TransactionCriteria.SearchBy;
import com.openfog.condo.dsign.transaction.model.Transaction;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionStatus;
import com.openfog.condo.dsign.transaction.model.Transaction.TransactionType;
import com.openfog.condo.dsign.transaction.repository.TransactionRepository;
import com.openfog.condo.dsign.utility.repository.NumericConstantRepository;

@Service
@Transactional
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private NumericConstantRepository numericConstantRepository;

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

		if (transaction.getTransactionType().equals(TransactionType.expense)) {
			if (transaction.isHoldingTax()) {
				double holdingTaxPercent = numericConstantRepository.findByConstantKey("holdingtax.percent").getValue();
				transaction.setFullAmount(transaction.getAmount());
				transaction.setHoldingTaxtAmount(transaction.getFullAmount() * holdingTaxPercent / 100);
				transaction.setAmount(transaction.getFullAmount() - transaction.getHoldingTaxtAmount());

				Calendar cal = Calendar.getInstance();
				cal.setTime(transaction.getTransactionDate());
				cal.add(Calendar.MONTH, 1);
				cal.set(Calendar.DATE, 7);

				transaction.setRevenueDepartmentDate(cal.getTime());
			}

		}
		transaction.setTransactionStatus(TransactionStatus.normal);
		return transactionRepository.save(transaction);
	}

	public void cancelTransaction(Long id) throws IncorrectTransactionStatusException {
		Transaction oldTransaction = transactionRepository.findOne(id);
		if (oldTransaction.getTransactionStatus().equals(TransactionStatus.voidTransaction)) {
			throw new IncorrectTransactionStatusException("Transaction " + oldTransaction.getId() + " is cancelled.");
		}

		oldTransaction.setTransactionStatus(TransactionStatus.voidTransaction);

		Transaction voidTransaction = new Transaction();

		voidTransaction.setAccountId(oldTransaction.getAccountId());
		voidTransaction.setReferenceTransaction(oldTransaction.getId());
		voidTransaction.setAmount(oldTransaction.getAmount());
		voidTransaction.setDetail("(Cancel) " + oldTransaction.getDetail());
		voidTransaction.setTransactionDate(new Date());
		voidTransaction.setTransactionStatus(TransactionStatus.voidTransactionRef);

		if (oldTransaction.getTransactionType().equals(TransactionType.expense)) {
			voidTransaction.setTransactionType(TransactionType.income);
		} else {
			voidTransaction.setTransactionType(TransactionType.expense);
		}

		transactionRepository.save(voidTransaction);
		transactionRepository.save(oldTransaction);

	}

	public Transaction adjustTransaction(Transaction newTransaction) throws IncorrectTransactionStatusException {

		Transaction oldTransaction = transactionRepository.findOne(newTransaction.getReferenceTransaction());
		if (oldTransaction.getTransactionStatus().equals(TransactionStatus.voidTransaction)) {
			throw new IncorrectTransactionStatusException("Transaction " + oldTransaction.getId() + " void.");
		}

		oldTransaction.setTransactionStatus(TransactionStatus.voidTransaction);

		Transaction voidTransaction = new Transaction();

		voidTransaction.setAccountId(oldTransaction.getAccountId());
		voidTransaction.setReferenceTransaction(oldTransaction.getId());
		voidTransaction.setAmount(oldTransaction.getAmount());
		voidTransaction.setDetail("(Cancel) " + oldTransaction.getDetail());
		voidTransaction.setTransactionDate(new Date());
		voidTransaction.setTransactionStatus(TransactionStatus.voidTransactionRef);

		if (oldTransaction.getTransactionType().equals(TransactionType.income)) {
			voidTransaction.setTransactionType(TransactionType.expense);
		} else {
			voidTransaction.setTransactionType(TransactionType.income);
		}

		transactionRepository.save(voidTransaction);
		transactionRepository.save(oldTransaction);

		save(newTransaction);

		return newTransaction;

	}

	private Pageable createPageRequest(TransactionCriteria criteria) {

		PageRequest pageRequest = new PageRequest(criteria.getPage(), criteria.getItemPerPage(), criteria.getSort());

		return pageRequest;

	}
}
