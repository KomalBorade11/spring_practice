package org.example.controller;

import org.example.cache.BranchCache;
import org.example.dto.BranchResponse;
import org.example.dto.SchedulerStatusResponse;
import org.example.dto.SchedulerTimeUpdateRequest;
import org.example.service.BranchCacheSchedulerService;
import org.example.service.BranchConsumerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consumer")
public class BranchConsumerController {

    private final BranchConsumerService service;
    private final BranchCache branchCache;
    private final BranchCacheSchedulerService schedulerService;

    public BranchConsumerController(BranchConsumerService service,
                                    BranchCache branchCache,
                                    BranchCacheSchedulerService schedulerService) {
        this.service = service;
        this.branchCache = branchCache;
        this.schedulerService = schedulerService;
    }

    @GetMapping("/{pincode}")
    public ResponseEntity<List<BranchResponse>> getBranch(@PathVariable String pincode) {
        List<BranchResponse> response = service.getBranchByPincode(pincode);
        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public List<BranchResponse> getAllBranches() {
        return service.getAllBranchesFromCache();
    }

    @GetMapping("/cache")
    public Map<String, List<BranchResponse>> showCache() {
        return branchCache.getSnapshot();
    }

    @PostMapping("/scheduler/start")
    public ResponseEntity<String> startScheduler() {
        return ResponseEntity.ok(schedulerService.startScheduler());
    }

    @PostMapping("/scheduler/stop")
    public ResponseEntity<String> stopScheduler() {
        return ResponseEntity.ok(schedulerService.stopScheduler());
    }

    @PostMapping("/scheduler/resume")
    public ResponseEntity<String> resumeScheduler() {
        return ResponseEntity.ok(schedulerService.resumeScheduler());
    }

    @PostMapping("/scheduler/refresh")
    public ResponseEntity<String> refreshNow() {
        return new ResponseEntity<>(schedulerService.refreshNow(), HttpStatus.OK);
    }

    @PostMapping("/scheduler/time")
    public ResponseEntity<String> updateSchedulerTime(@RequestBody SchedulerTimeUpdateRequest request) {
        try {
            return ResponseEntity.ok(schedulerService.updateDailyRunTime(request.getTime()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/scheduler/status")
    public SchedulerStatusResponse schedulerStatus() {
        return schedulerService.getStatus();
    }
}
