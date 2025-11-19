package artifact.Backend.Services.Interfaces;

import artifact.Backend.Models.DashboardStats;

public interface IDashboardService {
    /**
     * Retrieves the aggregate statistics for the dashboard.
     * @return DashboardStats object containing counts.
     */
    DashboardStats getDashboardStats();
}