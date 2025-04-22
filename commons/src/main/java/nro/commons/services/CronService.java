package nro.commons.services;

import lombok.Getter;
import nro.commons.services.cron.CronServiceException;
import nro.commons.services.cron.RunnableRunner;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.*;

public class CronService {

    @Getter
    private static CronService instance;

    private final TimeZone timeZone;
    private final Scheduler scheduler;
    private final Class<? extends RunnableRunner> runnableRunner;

    private static final Logger log = LoggerFactory.getLogger(CronService.class);

    public static synchronized void initSingleton(Class<? extends RunnableRunner> runnableRunner, TimeZone timeZone) {
        if (instance != null) {
            throw new CronServiceException("CronService is already initialized");
        }

        instance = new CronService(runnableRunner, timeZone);
    }

    private CronService(Class<? extends RunnableRunner> runnableRunner, TimeZone timeZone) {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.threadPool.threadCount", "1");

        try {
            scheduler = new StdSchedulerFactory(properties).getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            throw new CronServiceException("Failed to initialize CronService", e);
        }
        if (runnableRunner == null) {
            throw new CronServiceException("RunnableRunner class must be defined");
        }

        this.runnableRunner = runnableRunner;
        this.timeZone = timeZone;
    }

    public void shutdown() {
        try {
            scheduler.shutdown(false);
        } catch (SchedulerException e) {
            log.error("Failed to shutdown CronService correctly", e);
        }
    }

    public JobDetail schedule(Runnable r, String cronExpression) {
        return schedule(r, cronExpression, false);
    }

    public JobDetail schedule(Runnable r, String cronExpression, boolean longRunning) {
        try {
            return schedule(r, new CronExpression(cronExpression), longRunning);
        } catch (ParseException e) {
            throw new RuntimeException("CronExpression \"" + cronExpression + "\" is invalid.", e);
        }
    }

    public JobDetail schedule(Runnable r, CronExpression cronExpression) {
        return schedule(r, cronExpression, false);
    }

    public JobDetail schedule(Runnable r, CronExpression cronExpression, boolean longRunning) {
        return schedule(r, runnableRunner, cronExpression, longRunning);
    }

    public JobDetail schedule(Runnable r, Class<? extends RunnableRunner> runnableRunner, CronExpression cronExpression, boolean longRunning) {
        try {
            JobDataMap jdm = new JobDataMap();
            jdm.put(RunnableRunner.KEY_RUNNABLE_OBJECT, r);
            jdm.put(RunnableRunner.KEY_PROPERTY_IS_LONGRUNNING_TASK, longRunning);
            jdm.put(RunnableRunner.KEY_CRON_EXPRESSION, cronExpression);

            String jobId = "Started at ms" + System.currentTimeMillis() + "; ns" + System.nanoTime();
            JobKey jobKey = new JobKey("JobKey:" + jobId);
            JobDetail jobDetail = JobBuilder.newJob(runnableRunner).usingJobData(jdm).withIdentity(jobKey).build();

            CronScheduleBuilder csb = CronScheduleBuilder.cronSchedule(cronExpression).inTimeZone(timeZone);
            CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(csb).build();

            scheduler.scheduleJob(jobDetail, trigger);
            return jobDetail;
        } catch (Exception e) {
            throw new CronServiceException("Failed to start job", e);
        }
    }

    public boolean cancel(JobDetail jd) {
        if (jd == null) {
            return false;
        }

        if (jd.getKey() == null) {
            throw new CronServiceException("JobDetail should have JobKey");
        }

        try {
            return scheduler.deleteJob(jd.getKey());
        } catch (SchedulerException e) {
            throw new CronServiceException("Failed to delete Job", e);
        }
    }

    public boolean cancel(Runnable r) {
        List<JobDetail> jobDetails = findJobDetails(r);
        if (jobDetails.isEmpty())
            return false;
        boolean allCancelled = true;
        for (JobDetail jobDetail : jobDetails) {
            allCancelled &= cancel(jobDetail);
        }
        return allCancelled;
    }

    public List<JobDetail> findJobDetails(Runnable runnable) {
        try {
            List<JobDetail> jobs = new ArrayList<>();
            for (JobKey jobKey : scheduler.getJobKeys(null)) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                if (jobDetail.getJobDataMap().get(RunnableRunner.KEY_RUNNABLE_OBJECT) == runnable)
                    jobs.add(jobDetail);
            }
            return jobs;
        } catch (Exception e) {
            throw new CronServiceException("Can't get all active job details", e);
        }
    }

    public List<? extends Trigger> getJobTriggers(JobDetail jd) {
        return getJobTriggers(jd.getKey());
    }

    public List<? extends Trigger> getJobTriggers(JobKey jk) {
        try {
            return scheduler.getTriggersOfJob(jk);
        } catch (SchedulerException e) {
            throw new CronServiceException("Can't get triggers for JobKey " + jk, e);
        }
    }

    public <T extends Runnable> List<JobDetail> findJobs(Class<T> runnableType, boolean withSubTypes) {
        try {
            Set<JobKey> keys = scheduler.getJobKeys(null);
            if (keys.isEmpty())
                return Collections.emptyList();

            List<JobDetail> jobs = new ArrayList<>(keys.size());
            for (JobKey jk : keys) {
                JobDetail jobDetail = scheduler.getJobDetail(jk);
                Object runnable = jobDetail.getJobDataMap().get(RunnableRunner.KEY_RUNNABLE_OBJECT);
                if (runnable != null) {
                    if (runnableType == runnable.getClass() || withSubTypes && runnableType.isAssignableFrom(runnable.getClass()))
                        jobs.add(jobDetail);
                }
            }
            return jobs;
        } catch (Exception e) {
            throw new CronServiceException("Couldn't collect job details for jobs of type " + runnableType, e);
        }
    }

    public <T extends Runnable> Map<T, Date> findNextFireTimes(Class<T> runnableType, boolean withSubTypes) {
        List<JobDetail> jobs = findJobs(runnableType, withSubTypes);
        if (jobs.isEmpty())
            return Collections.emptyMap();

        try {
            long now = System.currentTimeMillis();
            Map<T, Date> nextFireTimes = new HashMap<>(jobs.size());
            for (JobDetail job : jobs) {
                Object runnable = job.getJobDataMap().get(RunnableRunner.KEY_RUNNABLE_OBJECT);
                scheduler.getTriggersOfJob(job.getKey()).stream().map(Trigger::getNextFireTime)
                        .filter(nextFireTime -> nextFireTime != null && nextFireTime.getTime() > now).sorted().findFirst().ifPresent(nextFireTime -> nextFireTimes
                                .compute((T) runnable, (k, oldDate) -> oldDate == null || oldDate.after(nextFireTime) ? nextFireTime : oldDate));
            }
            return nextFireTimes;
        } catch (Exception e) {
            throw new CronServiceException("Can't get all active job details", e);
        }
    }

}
