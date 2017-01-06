import org.json.JSONObject;

public class Order {
    public enum FinancialStatus {
        pending("pending"),
        authorized("authorized"),
        partially_paid("partially_paid"),
        paid("paid"),
        partially_refunded("partially_refunded"),
        refunded("refunded"),
        voided("voided");

        private final String mValue;

        FinancialStatus(final String value) {
            mValue = value;
        }

        public static FinancialStatus parse(final String value) {
            switch (value) {
                case "pending":
                    return pending;
                case "authorized":
                    return authorized;
                case "partially_paid":
                    return partially_paid;
                case "paid":
                    return paid;
                case "partially_refunded":
                    return partially_refunded;
                case "refunded":
                    return refunded;
                case "voided":
                    return voided;
            }
            return null;
        }
    }

    public String id;
    public String cancelled_at;
    public String currency;
    public FinancialStatus financial_status;
    public boolean test = false;
    public double total_price;

    public Order() {}

    public Order setAttributes(JSONObject jsonObject) {
    	id = jsonObject.optString("id");
        cancelled_at = jsonObject.isNull("cancelled_at") ? null : jsonObject.optString("cancelled_at");
        currency = jsonObject.optString("currency");
        financial_status = FinancialStatus.parse(jsonObject.optString("financial_status"));
        test = jsonObject.optBoolean("test");
        total_price = jsonObject.optDouble("total_price");
        return this;
    }
}
