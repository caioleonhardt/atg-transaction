package com.leonhardt.transaction;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import atg.commerce.order.Order;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;

public class LockUtils {

	private LockUtils() {
    }
	
	public static int indexOfPameterAnnotation(Method method, Class<?> annotation) {
		if (method == null) {
			throw new IllegalArgumentException("the method parameter cannot be null");			
		}

		if (annotation == null) {
			throw new IllegalArgumentException("the annotation parameter cannot be null");			
		}
		
		Annotation[][] annotations = method.getParameterAnnotations();
		
		int index = -1;
		int count = 0;
		
		for (int i = 0; i < annotations.length; i++) {
			for (int j = 0; j < annotations[i].length; j++) {
				if (annotation.isInstance(annotations[i][j])) {
					index = i;
					count++;
				}
			}
		}
		
		if (index == -1) {
			throw new IllegalArgumentException("at least one method parameter must has Lock annotation");			
		}

		if (count > 1) {
			throw new IllegalArgumentException("more than once Lock annotations in method");			
		}
		
		return index;
	}
	
	public static String lockOrderPath(Method method, Object[] args, LockField field) {
	    int index = LockUtils.indexOfPameterAnnotation(method, Lock.class);
        
        Order order = (Order) args[index];
        
        String lockId = LockField.PROFILE.compareTo(field) == 0?  order.getProfileId() : order.getId();
        
	    return "/atg/commerce/order/OrderRepository/" + lockId;
	}
	
	public static String lockPath(Method method, Object[] args) throws RepositoryException {
		int index = LockUtils.indexOfPameterAnnotation(method, Lock.class);
		
		Object lockObj = args[index];
		
		if (lockObj instanceof String && !StringUtils.isBlank((String)lockObj)) {
		    return (String) lockObj;
		}
		
		return RepositoryUtils.getUriForRepositoryItem((RepositoryItem) lockObj); 
	}
}
