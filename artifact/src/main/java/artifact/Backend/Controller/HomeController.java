package artifact.Backend.Controller;

import artifact.Backend.Models.DashboardStats;
import artifact.Backend.Repositories.Impl.RepositoryProvider;
import artifact.Backend.Services.Impl.DashboardService;
import artifact.Backend.Services.Interfaces.IDashboardService;

public class HomeController extends BaseController {

    // Use Interface for loose coupling
    private final IDashboardService dashboardService;

    public HomeController() {
        super();
        // Inject Repository into Service
        this.dashboardService = new DashboardService(RepositoryProvider.getDashboardRepository());
    }

    /**
     * Fetches the stats. 
     * The View will call this and update the Labels itself.
     */
    public DashboardStats loadStats() {
        return dashboardService.getDashboardStats();
    }
}