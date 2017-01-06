import org.json.JSONObject;

public class Transaction {
    public enum Kind {
    	authorization("authorization"),
    	capture("capture"),
    	sale("sale"),
    	void_kind("void"),
    	refund("refund");

        private final String mValue;

        Kind(final String value) {
            mValue = value;
        }

        public static Kind parse(final String value) {
            switch (value) {
                case "authorization":
                    return authorization;
                case "capture":
                    return capture;
                case "sale":
                    return sale;
                case "void":
                    return void_kind;
                case "refund":
                    return refund;
            }
            return null;
        }
    }
    
    public enum Status {
    	pending("pending"),
    	failure("failure"),
    	success("success"),
    	error("error");

        private final String mValue;

    	Status(final String value) {
            mValue = value;
        }

        public static Status parse(final String value) {
            switch (value) {
                case "pending":
                    return pending;
                case "failure":
                    return failure;
                case "success":
                    return success;
                case "error":
                    return error;
            }
            return null;
        }
    }

    public double amount;
    public String currency;
    public Kind kind;
    public Status status;
    public boolean test = false;

    public Transaction() {}

    public Transaction setAttributes(JSONObject jsonObject) {
        amount = jsonObject.optDouble("amount");
        currency = jsonObject.optString("currency");
        kind = Kind.parse(jsonObject.optString("cancelled_at"));
        status = Status.parse(jsonObject.optString("status"));
        test = jsonObject.optBoolean("test");
        return this;
    }
}
