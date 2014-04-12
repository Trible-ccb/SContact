package com.trible.scontact.controller;

import java.util.List;

public interface ISearchControl<T> {

	List<T> searchByName(String qStr);
	
}
