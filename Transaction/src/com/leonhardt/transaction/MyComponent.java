package com.leonhardt.transaction;

import atg.nucleus.GenericService;


public class MyComponent extends GenericService {
	
	@ATGTransaction
	public void executeTransaction() {
		System.out.println("com.renner.proxy.MyObject.executeTransaction()");
	}

	public void execute() {
		System.out.println("com.renner.proxy.MyObject.execute()");
	}

}
