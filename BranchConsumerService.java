package org.example.service;


import org.example.cache.BranchCache;
import org.example.dto.BranchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BranchConsumerService {

    private final RestTemplate restTemplate;
    private final BranchCache branchCache;
    private final String providerBaseUrl;

    public BranchConsumerService(RestTemplate restTemplate,
                                 BranchCache branchCache,
                                 @Value("${provider.base-url:http://localhost:8081}") String providerBaseUrl) {
        this.restTemplate = restTemplate;
        this.branchCache = branchCache;
        this.providerBaseUrl = providerBaseUrl;
    }

    public List<BranchResponse> getBranchByPincode(String pincode) {
        return branchCache.get(pincode);
    }

    public List<BranchResponse> getAllBranchesFromCache() {
        return branchCache.getSnapshot().values().stream()
                .flatMap(List::stream)
                .toList();

    }

    public List<BranchResponse> fetchAllBranchesFromProvider() {
        String url = providerBaseUrl + "/branches";
        BranchResponse[] response = restTemplate.getForObject(url, BranchResponse[].class);
        if (response == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(response);
    }

    public int refreshCacheFromProvider() {
        List<BranchResponse> branches = fetchAllBranchesFromProvider();
        branchCache.replaceAll(branches);
        return branchCache.size();
    }
}
