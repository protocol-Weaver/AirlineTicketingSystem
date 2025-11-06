package artifact.Backend.Services.Impl;

import artifact.Backend.Models.DashboardStats;
import artifact.Backend.Repositories.Interfaces.IDashboardRepository;
import artifact.Backend.Services.Interfaces.IDashboardService;

/**
 * Service implementation for retrieving Dashboard Analytics.
 * Acts as a facade over the Dashboard Repository.
 */
public class DashboardService implements IDashboardService {

    private final IDashboardRepository statsRepo;
    
    public DashboardService(IDashboardRepository repo) {
        this.statsRepo = repo;
    }

    /**
     * Fetches aggregated statistics for the admin dashboard.
     * @return DashboardStats object containing counts for Flights, Passengers, Revenue, etc.
     */
    @Override
    public DashboardStats getDashboardStats() {
        return statsRepo.getDashboardStats();
    }
}