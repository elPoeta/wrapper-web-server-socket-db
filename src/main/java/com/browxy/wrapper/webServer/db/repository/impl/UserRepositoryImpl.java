package com.browxy.wrapper.webServer.db.repository.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.browxy.wrapper.webServer.db.DBManager;
import com.browxy.wrapper.webServer.model.User;

public class UserRepositoryImpl extends GenericRepositoryImpl<User, Long> {
	  public UserRepositoryImpl(DBManager dbManager) {
	        super(dbManager, "users");
	    }

	    @Override
	    protected User mapRowToEntity(Map<String, String> row) {
	    	if(row == null) {
	    		return null;
	    	}
	        User user = new User();
	        user.setId(Long.parseLong(row.get("id")));
	        user.setEmail(row.get("email"));
	        user.setIsAdmin(Boolean.getBoolean(row.get("isAdmin")));
	        user.setPassword(row.get("password"));
	        return user;
	    }

	    @Override
	    protected List<User> mapRowsToEntities(List<Map<String, String>> rows) {
	        List<User> users = new ArrayList<>();
	        for (Map<String, String> row : rows) {
	            users.add(mapRowToEntity(row));
	        }
	        return users;
	    }

	    @Override
	    protected Map<String, String> mapEntityToValues(User user) {
	        Map<String, String> values = new LinkedHashMap<>();
	        values.put("id", String.valueOf(user.getId()));
	        values.put("email", user.getEmail());
	        values.put("isAdmin", String.valueOf(user.isAdmin()));
	        values.put("password", user.getPassword());
	        return values;
	    }

	    @Override
	    protected String[][] mapValuesToRecord(Map<String, String> values) {
	        String[][] record = new String[values.size()][2];
	        int i = 0;
	        for (Map.Entry<String, String> entry : values.entrySet()) {
	            record[i][0] = entry.getKey();
	            record[i][1] = entry.getValue();
	            i++;
	        }
	        return record;
	    }
}
