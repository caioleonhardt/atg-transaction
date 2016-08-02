package com.leonhardt.transaction;

import atg.commerce.order.Order;
import atg.nucleus.GenericService;


public class MyComponent extends GenericService {
	
	@ATGTransaction
	public void executeTransaction() {
		System.out.println("executeTransaction");
	}
	
	@ATGLockTransaction
	public void executeLockTransaction(Order order, String id) {
		System.out.println("executeLockTransaction");
		
	}

	public void execute() {
		System.out.println("simple");
	}
	
}
