package org.example.dto;

import java.time.LocalDateTime;

public class SchedulerStatusResponse {

    private boolean running;
    private String cronExpression;
    private LocalDateTime lastRefreshTime;
    private LocalDateTime nextScheduledRun;
    private Integer cacheSize;
    private String lastStatusMessage;

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public LocalDateTime getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(LocalDateTime lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public LocalDateTime getNextScheduledRun() {
        return nextScheduledRun;
    }

    public void setNextScheduledRun(LocalDateTime nextScheduledRun) {
        this.nextScheduledRun = nextScheduledRun;
    }

    public Integer getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getLastStatusMessage() {
        return lastStatusMessage;
    }

    public void setLastStatusMessage(String lastStatusMessage) {
        this.lastStatusMessage = lastStatusMessage;
    }
}
