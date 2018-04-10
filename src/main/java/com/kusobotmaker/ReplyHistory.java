package com.kusobotmaker;

import java.util.HashMap;
import java.util.Map;

import twitter4j.Status;

class ReplyHistory
{
	private Map<Long,Long> map = new HashMap<Long,Long>();
	public void addHistory(Status status) {
		Long repToId = status.getInReplyToStatusId();
		if(repToId != -1){
			map.put(status.getId(), repToId);
		}
	}
	public long countReplyHistory(Long statusId) {
		Long id = map.get(statusId);
		if(id == null || id == -1)
		{
			return 0;
		}else
		{
			return countReplyHistory(id) + 1;
		}
	}
}