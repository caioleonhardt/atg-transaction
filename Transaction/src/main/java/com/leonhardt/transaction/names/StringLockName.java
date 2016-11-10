package com.leonhardt.transaction.names;

import atg.core.util.StringUtils;


class StringLockName implements Lockable {

	private String lockObj;
	
	StringLockName(String lockObj) {
		this.lockObj = lockObj;
	}
	
    @Override
    public String name() {
        if (StringUtils.isBlank(lockObj)) {
            throw new IllegalArgumentException("id cannot be null/empty to create the lock");
        }

        return lockObj;
    }

}
