package com.browxy.wrapper.webServer.db.service.impl;

import com.browxy.wrapper.webServer.db.repository.GenericRepository;
import com.browxy.wrapper.webServer.model.User;

public class UserServiceImpl extends GenericServiceImpl<User, Long> {
	public UserServiceImpl(GenericRepository<User, Long> repository) {
		super(repository);
	}
}
