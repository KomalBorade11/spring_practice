package org.example.cache;

import org.example.dto.BranchResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BranchCache {

    private final Map<String, List<BranchResponse>> cache = new ConcurrentHashMap<>();

    public Map<String, List<BranchResponse>> getSnapshot() {
        Map<String, List<BranchResponse>> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, List<BranchResponse>> entry : cache.entrySet()) {
            snapshot.put(entry.getKey(), List.copyOf(entry.getValue()));
        }
        return Collections.unmodifiableMap(snapshot);
    }

    public List<BranchResponse> get(String pincode) {
        List<BranchResponse> branches = cache.get(pincode);
        return branches == null ? Collections.emptyList() : List.copyOf(branches);
    }

    public void put(String pincode, BranchResponse response) {
        if (pincode == null || response == null) {
            return;
        }
        cache.computeIfAbsent(pincode, key -> Collections.synchronizedList(new java.util.ArrayList<>()))
                .add(response);
    }

    public boolean contains(String pincode) {
        return cache.containsKey(pincode);
    }

    public void replaceAll(Collection<BranchResponse> branches) {
        cache.clear();
        for (BranchResponse branch : branches) {
            if (branch != null && branch.getPincode() != null) {
                cache.computeIfAbsent(branch.getPincode(), key -> Collections.synchronizedList(new java.util.ArrayList<>()))
                        .add(branch);
            }
        }
    }

    public int size() {
        return cache.values().stream().mapToInt(List::size).sum();
    }

    public void clear() {
        cache.clear();
    }
}
