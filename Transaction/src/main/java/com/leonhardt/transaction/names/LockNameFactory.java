package com.leonhardt.transaction.names;

import com.leonhardt.transaction.LockField;

import atg.commerce.order.Order;
import atg.repository.RepositoryItem;

class LockNameFactory {

	static <T> Lockable getInstance(T lockObj, LockField field) {
		
		if (lockObj instanceof String) {
		    return new StringLockName((String)lockObj);
		}
		
		
		else if (lockObj instanceof RepositoryItem) {
			return new RepositoryItemLockName((RepositoryItem)lockObj);
		}
		
		
		else if (lockObj instanceof Order) {
			return new OrderLockName((Order) lockObj, field);
		}
		
		
		else {
			throw new IllegalArgumentException("no was possible resolve the lock name to this object");
		}
		
	}
	
	static <T> Lockable getInstance(T lockObj) {
		return getInstance(lockObj, null);
	}

}
