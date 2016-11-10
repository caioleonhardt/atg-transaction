package com.leonhardt.transaction.names;

import java.lang.reflect.Method;

import com.leonhardt.transaction.ATGLockOrder;
import com.leonhardt.transaction.Lock;
import com.leonhardt.transaction.LockField;

public final class LockUtils {

	private LockUtils() {
    }
	
	public static String lockPath(Method method, Object[] args) throws Exception {
		int index = LockNameHelper.indexOfPameterAnnotation(method, Lock.class);
		
		ATGLockOrder annotation = method.getAnnotation(ATGLockOrder.class);
		LockField lockField = null;
		
		if (annotation != null) {
			lockField = annotation.field();
		}
		
		Object lockObj = args[index];
		
		Lockable lockName = LockNameFactory.getInstance(lockObj, lockField);
		
		return lockName.name();
	}
	
	
}
