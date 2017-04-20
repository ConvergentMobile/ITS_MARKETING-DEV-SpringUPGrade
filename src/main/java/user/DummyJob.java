package user;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DummyJob implements Job {
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
        String jobName = context.getJobDetail().getFullName();
        try {
        	System.out.println("DummyJob job executed at " + Calendar.getInstance().getTime());
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
}