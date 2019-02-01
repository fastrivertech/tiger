package com.frt.dr.service.query;

import java.util.List;
import java.util.Optional;
import com.frt.dr.model.Resource;

public interface ResourceQuery<T extends Resource> {
	enum QUERY_STATES {
		CREATED, PREPARED, EXECUTED
	}
	public void prepareQuery();
	public Optional<List<Resource>> doQuery();
}
