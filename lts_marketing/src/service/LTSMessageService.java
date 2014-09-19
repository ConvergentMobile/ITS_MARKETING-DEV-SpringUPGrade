package service;

import java.util.List;

import data.LTUserForm;
import user.Campaign;
import user.TargetUserList;

public interface LTSMessageService {
	public Boolean checkQuota(List<String> listIds, Long userId, Integer msgLength);
	public void scheduleJob(String schedDate, String schedTime,
			Integer repeatDayCount, Integer repeatMonthCount,
			Integer numOccurrenceDays, Integer numOccurrenceMonths,
			Campaign campaign, String tzone);
	public void sendMessageLT(Campaign campaign, LTUserForm ltUser);
	public void sendMessage(Campaign campaign, Long userId);
	public List<TargetUserList> getList(String officeIds);
	public List<String> getListData(String listId);
}
