package org.example.service;

import jakarta.annotation.PostConstruct;
import org.example.cache.BranchCache;
import org.example.dto.SchedulerStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ScheduledFuture;
@Service
public class BranchCacheSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(BranchCacheSchedulerService.class);

    private final TaskScheduler taskScheduler;
    private final BranchConsumerService branchConsumerService;
    private final BranchCache branchCache;
    private final boolean autoStart;
    private volatile String cronExpression;

    private volatile ScheduledFuture<?> scheduledFuture;
    private volatile LocalDateTime lastRefreshTime;
    private volatile String lastStatusMessage = "Scheduler has not run yet";

    public BranchCacheSchedulerService(TaskScheduler taskScheduler,
                                       BranchConsumerService branchConsumerService,
                                       BranchCache branchCache,
                                       @Value("${cache.refresh.cron:0 0 14 * * *}") String cronExpression,
                                       @Value("${cache.scheduler.auto-start:true}") boolean autoStart) {
        this.taskScheduler = taskScheduler;
        this.branchConsumerService = branchConsumerService;
        this.branchCache = branchCache;
        this.cronExpression = cronExpression;
        this.autoStart = autoStart;
    }

    @PostConstruct
    public void initializeScheduler() {
        if (autoStart) {
            startScheduler();
        }
    }

    public synchronized String startScheduler() {
        if (isRunning()) {
            return "Scheduler is already running";
        }

        scheduleCurrentCron();
        lastStatusMessage = "Scheduler started";
        logger.info("Branch cache scheduler started with cron: {}", cronExpression);
        return "Scheduler started";
    }

    public synchronized String stopScheduler() {
        if (!isRunning()) {
            return "Scheduler is already stopped";
        }

        scheduledFuture.cancel(false);
        scheduledFuture = null;
        lastStatusMessage = "Scheduler stopped";
        logger.info("Branch cache scheduler stopped");
        return "Scheduler stopped";
    }

    public synchronized String resumeScheduler() {
        if (isRunning()) {
            return "Scheduler is already running";
        }
        return startScheduler().replace("started", "resumed");
    }

    public synchronized String refreshNow() {
        runRefreshCycle("Manual refresh completed", "Manual refresh failed");
        return lastStatusMessage;
    }

    public synchronized String updateDailyRunTime(String timeValue) {
        if (timeValue == null || timeValue.isBlank()) {
            throw new IllegalArgumentException("Time is required. Use HH:mm format, for example 14:30");
        }

        LocalTime parsedTime;
        try {
            parsedTime = LocalTime.parse(timeValue.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid time format. Use HH:mm format, for example 14:30");
        }

        String newCronExpression = String.format("0 %d %d * * *", parsedTime.getMinute(), parsedTime.getHour());
        cronExpression = newCronExpression;

        if (isRunning()) {
            scheduledFuture.cancel(false);
            scheduleCurrentCron();
            lastStatusMessage = "Scheduler time updated to " + parsedTime + " and rescheduled";
        } else {
            lastStatusMessage = "Scheduler time updated to " + parsedTime + ". Start scheduler to apply";
        }

        logger.info("Branch cache scheduler time updated. New cron: {}", cronExpression);
        return lastStatusMessage;
    }

    public SchedulerStatusResponse getStatus() {
        SchedulerStatusResponse response = new SchedulerStatusResponse();
        response.setRunning(isRunning());
        response.setCronExpression(cronExpression);
        response.setLastRefreshTime(lastRefreshTime);
        response.setNextScheduledRun(calculateNextRun());
        response.setCacheSize(branchCache.size());
        response.setLastStatusMessage(lastStatusMessage);
        return response;
    }

    private void runScheduledRefresh() {
        runRefreshCycle("Scheduled refresh completed", "Scheduled refresh failed");
    }

    private synchronized void runRefreshCycle(String successPrefix, String failurePrefix) {
        try {
            int cacheSize = branchConsumerService.refreshCacheFromProvider();
            lastRefreshTime = LocalDateTime.now();
            lastStatusMessage = successPrefix + " at " + lastRefreshTime + " with " + cacheSize + " records";
            logger.info(lastStatusMessage);
        } catch (Exception ex) {
            lastStatusMessage = failurePrefix + ": " + ex.getMessage();
            logger.error("Branch cache refresh failed", ex);
        }
    }

    private LocalDateTime calculateNextRun() {
        try {
            CronExpression expression = CronExpression.parse(cronExpression);
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            ZonedDateTime next = expression.next(now);
            return next == null ? null : next.toLocalDateTime();
        } catch (Exception ex) {
            logger.warn("Unable to calculate next run from cron: {}", cronExpression, ex);
            return null;
        }
    }

    private boolean isRunning() {
        return scheduledFuture != null && !scheduledFuture.isCancelled();
    }

    private void scheduleCurrentCron() {
        scheduledFuture = taskScheduler.schedule(this::runScheduledRefresh, new CronTrigger(cronExpression));
    }
}
