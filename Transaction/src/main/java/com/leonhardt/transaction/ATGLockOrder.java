package com.leonhardt.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ATGLockOrder {
	
	LockField field() default LockField.ORDER;
	
	LockType type() default LockType.WRITE;
}