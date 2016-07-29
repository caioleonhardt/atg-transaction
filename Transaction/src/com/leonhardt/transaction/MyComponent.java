package com.leonhardt.transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;


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
		System.out.println("execute");
	}

	public void test() {
		OrderManager om = (OrderManager) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/order/OrderManager");
		Order order = null;
		try {
			order = om.loadOrder("1015871");
		} catch (CommerceException e) {
			logError(e);
		}
		
		for (int i = 0; i < 5; i++) {
			logInfo("excuting lock " + i);
			executeLockTransaction(order, "teste");
			logInfo("finishing lock " + i);
		}
	}
}
