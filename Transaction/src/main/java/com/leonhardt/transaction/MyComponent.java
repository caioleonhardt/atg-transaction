package com.leonhardt.transaction;

import atg.commerce.order.Order;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class MyComponent extends GenericService {
	
	@ATGTransaction
	public void executeTransaction() {
		System.out.println("executeTransaction");
	}
	
	@ATGLock
	public void executeLockTransaction(@Lock RepositoryItem item, String id) {
		System.out.println("executeLockTransaction");
	}

	@ATGLock
	public void execute(@Lock String string) {
	    System.out.println(string);
	}
	
	@ATGLockOrder
	public void executeLockOrder(@Lock Order order, String id) {
		System.out.println("executeLockOrder");
	}
}
