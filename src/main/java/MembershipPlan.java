import java.io.Serializable;

/**
 * MembershipPlan class represents different membership tiers in the library.
 * Each plan has different fees, loan limits, loan periods, and perks.
 */
public class MembershipPlan implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // Plan Types
    public enum PlanType {
        BASIC("Basic Plan", 0.00, 3, 14, 0.50, false, false),
        PREMIUM("Premium Plan", 9.99, 5, 21, 0.25, true, false),
        VIP("VIP Plan", 19.99, 10, 30, 0.10, true, true),
        STAFF("Staff Plan", 0.00, 20, 60, 0.00, true, true);
        
        private final String displayName;
        private final double monthlyFee;
        private final int maxLoans;
        private final int loanPeriodDays;
        private final double dailyOverdueFee;
        private final boolean canReserveBooks;
        private final boolean priorityAccess;
        
        PlanType(String displayName, double monthlyFee, int maxLoans, 
                 int loanPeriodDays, double dailyOverdueFee, 
                 boolean canReserveBooks, boolean priorityAccess) {
            this.displayName = displayName;
            this.monthlyFee = monthlyFee;
            this.maxLoans = maxLoans;
            this.loanPeriodDays = loanPeriodDays;
            this.dailyOverdueFee = dailyOverdueFee;
            this.canReserveBooks = canReserveBooks;
            this.priorityAccess = priorityAccess;
        }
        
        public String getDisplayName() { return displayName; }
        public double getMonthlyFee() { return monthlyFee; }
        public int getMaxLoans() { return maxLoans; }
        public int getLoanPeriodDays() { return loanPeriodDays; }
        public double getDailyOverdueFee() { return dailyOverdueFee; }
        public boolean canReserveBooks() { return canReserveBooks; }
        public boolean hasPriorityAccess() { return priorityAccess; }
    }
    
    // Instance attributes
    private PlanType planType;
    private boolean isActive;
    private java.time.LocalDate startDate;
    private java.time.LocalDate expiryDate;
    
    // Constructors
    
    /**
     * Create a membership plan with specified type
     */
    public MembershipPlan(PlanType planType) {
        this.planType = planType;
        this.isActive = true;
        this.startDate = java.time.LocalDate.now();
        // Staff plans don't expire, others expire in 1 year
        this.expiryDate = planType == PlanType.STAFF ? null : startDate.plusYears(1);
    }
    
    /**
     * Create a default BASIC membership plan
     */
    public MembershipPlan() {
        this(PlanType.BASIC);
    }
    
    // Getters
    
    public PlanType getPlanType() {
        return planType;
    }
    
    public String getPlanName() {
        return planType.getDisplayName();
    }
    
    public double getMonthlyFee() {
        return planType.getMonthlyFee();
    }
    
    public int getMaxLoans() {
        return planType.getMaxLoans();
    }
    
    public int getLoanPeriodDays() {
        return planType.getLoanPeriodDays();
    }
    
    public double getDailyOverdueFee() {
        return planType.getDailyOverdueFee();
    }
    
    public boolean canReserveBooks() {
        return planType.canReserveBooks();
    }
    
    public boolean hasPriorityAccess() {
        return planType.hasPriorityAccess();
    }
    
    public boolean isActive() {
        if (!isActive) return false;
        if (expiryDate == null) return true; // Staff plans never expire
        return !java.time.LocalDate.now().isAfter(expiryDate);
    }
    
    public java.time.LocalDate getStartDate() {
        return startDate;
    }
    
    public java.time.LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    // Setters and Actions
    
    /**
     * Upgrade to a new plan type
     */
    public void upgradePlan(PlanType newPlanType) {
        if (newPlanType.ordinal() > this.planType.ordinal()) {
            this.planType = newPlanType;
            this.startDate = java.time.LocalDate.now();
            this.expiryDate = newPlanType == PlanType.STAFF ? null : startDate.plusYears(1);
            System.out.println("Plan upgraded to: " + newPlanType.getDisplayName());
        } else {
            System.out.println("Cannot downgrade using upgradePlan. Use changePlan instead.");
        }
    }
    
    /**
     * Change to any plan type
     */
    public void changePlan(PlanType newPlanType) {
        this.planType = newPlanType;
        this.startDate = java.time.LocalDate.now();
        this.expiryDate = newPlanType == PlanType.STAFF ? null : startDate.plusYears(1);
        System.out.println("Plan changed to: " + newPlanType.getDisplayName());
    }
    
    /**
     * Renew the current plan
     */
    public void renewPlan() {
        if (planType == PlanType.STAFF) {
            System.out.println("Staff plans don't need renewal.");
            return;
        }
        this.startDate = java.time.LocalDate.now();
        this.expiryDate = startDate.plusYears(1);
        this.isActive = true;
        System.out.println("Plan renewed until: " + expiryDate);
    }
    
    /**
     * Deactivate the plan
     */
    public void deactivate() {
        this.isActive = false;
        System.out.println("Membership plan deactivated.");
    }
    
    /**
     * Calculate annual cost
     */
    public double getAnnualCost() {
        return planType.getMonthlyFee() * 12;
    }
    
    /**
     * Get days until expiry
     */
    public long getDaysUntilExpiry() {
        if (expiryDate == null) return Long.MAX_VALUE; // Never expires
        return java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), expiryDate);
    }
    
    /**
     * Check if plan is expiring soon (within 30 days)
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) return false;
        long daysLeft = getDaysUntilExpiry();
        return daysLeft > 0 && daysLeft <= 30;
    }
    
    /**
     * Display plan benefits
     */
    public void displayBenefits() {
        System.out.println("\n========== " + planType.getDisplayName().toUpperCase() + " BENEFITS ==========");
        System.out.println("• Maximum simultaneous loans: " + planType.getMaxLoans() + " books");
        System.out.println("• Loan period: " + planType.getLoanPeriodDays() + " days");
        System.out.println("• Overdue fee: $" + String.format("%.2f", planType.getDailyOverdueFee()) + "/day");
        System.out.println("• Monthly fee: $" + String.format("%.2f", planType.getMonthlyFee()));
        System.out.println("• Book reservation: " + (planType.canReserveBooks() ? "Yes" : "No"));
        System.out.println("• Priority access to new books: " + (planType.hasPriorityAccess() ? "Yes" : "No"));
        if (planType == PlanType.STAFF) {
            System.out.println("• Extended access hours: Yes");
            System.out.println("• Administrative privileges: Yes");
        }
        System.out.println("================================================\n");
    }
    
    /**
     * Compare plans - display comparison table
     */
    public static void comparePlans() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    MEMBERSHIP PLANS COMPARISON                        ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Feature          │ Basic    │ Premium  │ VIP      │ Staff            ║");
        System.out.println("╠══════════════════╪══════════╪══════════╪══════════╪══════════════════╣");
        System.out.println("║ Monthly Fee      │ FREE     │ $9.99    │ $19.99   │ FREE             ║");
        System.out.println("║ Max Loans        │ 3 books  │ 5 books  │ 10 books │ 20 books         ║");
        System.out.println("║ Loan Period      │ 14 days  │ 21 days  │ 30 days  │ 60 days          ║");
        System.out.println("║ Overdue Fee/day  │ $0.50    │ $0.25    │ $0.10    │ None             ║");
        System.out.println("║ Reserve Books    │ No       │ Yes      │ Yes      │ Yes              ║");
        System.out.println("║ Priority Access  │ No       │ No       │ Yes      │ Yes              ║");
        System.out.println("║ Admin Access     │ No       │ No       │ No       │ Yes              ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════╝\n");
    }
    
    @Override
    public String toString() {
        return "MembershipPlan{" +
                "type=" + planType.getDisplayName() +
                ", active=" + isActive() +
                ", expires=" + (expiryDate != null ? expiryDate : "Never") +
                '}';
    }
}
