package com.leonhardt.transaction.names;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class LockNameHelper {
	
	/**
	 * 
	 * @return index of annotation in method parameters
	 * 
	 * @throws NullPointerException if {@code method} is {@code null} 
	 * @throws NullPointerException if {@code annotation} is {@code null}
	 *  
	 * @throws IllegalStateException if the expected annotation is not
	 *  present or if more then once annotation is found
	 */
	static int indexOfPameterAnnotation(Method method, Class<?> annotation) {
		if (method == null) {
			throw new NullPointerException("method may not be null");			
		}

		if (annotation == null) {
			throw new NullPointerException("annotation may not be null");			
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
			throw new IllegalStateException("at least one method parameter must has Lock annotation");			
		}

		if (count > 1) {
			throw new IllegalStateException("more than once Lock annotations in method");			
		}
		
		return index;
	}
}
