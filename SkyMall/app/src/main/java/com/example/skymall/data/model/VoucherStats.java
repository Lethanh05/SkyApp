package com.example.skymall.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VoucherStats {
    @SerializedName("period_days")
    public int periodDays;

    @SerializedName("overall")
    public OverallStats overall;

    @SerializedName("usage_stats")
    public UsageStats usageStats;

    @SerializedName("top_vouchers")
    public List<TopVoucher> topVouchers;

    @SerializedName("daily_trend")
    public List<DailyTrend> dailyTrend;

    public static class OverallStats {
        @SerializedName("total_vouchers")
        public int totalVouchers;

        @SerializedName("active_vouchers")
        public int activeVouchers;

        @SerializedName("expired_vouchers")
        public int expiredVouchers;

        @SerializedName("scheduled_vouchers")
        public int scheduledVouchers;
    }

    public static class UsageStats {
        @SerializedName("vouchers_used")
        public int vouchersUsed;

        @SerializedName("total_usage")
        public int totalUsage;

        @SerializedName("unique_users")
        public int uniqueUsers;

        @SerializedName("total_discount_given")
        public double totalDiscountGiven;
    }

    public static class TopVoucher {
        @SerializedName("code")
        public String code;

        @SerializedName("type")
        public String type;

        @SerializedName("value")
        public double value;

        @SerializedName("usage_count")
        public int usageCount;

        @SerializedName("unique_users")
        public int uniqueUsers;

        @SerializedName("total_discount")
        public double totalDiscount;
    }

    public static class DailyTrend {
        @SerializedName("date")
        public String date;

        @SerializedName("usage_count")
        public int usageCount;

        @SerializedName("daily_discount")
        public double dailyDiscount;
    }
}
