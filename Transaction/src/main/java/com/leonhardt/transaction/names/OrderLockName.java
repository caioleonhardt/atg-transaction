package com.leonhardt.transaction.names;

import atg.commerce.order.Order;

import com.leonhardt.transaction.LockField;

class OrderLockName implements Lockable {

    private static final String ORDER_REP = "/atg/commerce/order/OrderRepository/";
    
    private Order order;
    private LockField field;
    
    OrderLockName(Order order, LockField field) {
		this.order = order;
		this.field = field;
	}
    
    @Override
    public String name() throws Exception {
    	String lockId = null;
    	String lockField = null;
    	
    	
    	if ( field != null && LockField.PROFILE.equals(field)) {
			lockId = order.getProfileId();
			lockField = field.toString();
    	}
    	else {
    		lockId = order.getId();
    		lockField = LockField.ORDER.toString();
    	}
    	
        
        return ORDER_REP + lockField + ":" + lockId;
    }

}
