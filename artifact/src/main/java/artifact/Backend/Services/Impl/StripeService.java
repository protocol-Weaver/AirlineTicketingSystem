package artifact.Backend.Services.Impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.param.ChargeCreateParams;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Service implementation for handling Payment Processing via Stripe API.
 * * <p><strong>Note:</strong> This implementation uses Stripe's Test Mode.
 * It bypasses raw card data transmission (which requires PCI compliance/HTTPS)
 * by using Stripe "Magic Tokens" to simulate various transaction outcomes.</p>
 */
public class StripeService {

    private static String API_KEY;

    public StripeService() {
        Dotenv dotenv = Dotenv.load();
        API_KEY = dotenv.get("API_KEY");
        Stripe.apiKey = API_KEY;
    }

    /**
     * Processes a credit card charge.
     * * Uses heuristics on the card number to select a simulation token:
     * <ul>
     * <li>Starts with 4 -> Visa (Success)</li>
     * <li>Starts with 5 -> Mastercard (Success)</li>
     * <li>Ends with 0002 -> Force Decline</li>
     * <li>Ends with 0004 -> Force Insufficient Funds</li>
     * </ul>
     *
     * @param cardNumber Raw card number (used for simulation logic only).
     * @param expMonth   Expiration month.
     * @param expYear    Expiration year.
     * @param cvc        Card verification code.
     * @param amountPKR  Amount to charge in Pakistani Rupees.
     * @return The Stripe Transaction ID if successful.
     * @throws StripeException If the API request fails or the card is declined.
     */
    public String chargeCreditCard(String cardNumber, int expMonth, int expYear, String cvc, double amountPKR) throws StripeException {
        
        // --- BYPASS LOGIC FOR RESTRICTED ACCOUNTS ---
        // Since we cannot send raw card numbers (Token.create) without account activation,
        // we use Stripe's "Magic Tokens" instead. 
        // These simulate a successful card capture.
        
        String sourceToken;

        // Simple logic to pick a test token based on the first digit of user input
        if (cardNumber.startsWith("4")) {
            sourceToken = "tok_visa";       // Simulates a Visa
        } else if (cardNumber.startsWith("5")) {
            sourceToken = "tok_mastercard"; // Simulates a Mastercard
        } else if (cardNumber.startsWith("3")) {
            sourceToken = "tok_amex";       // Simulates an Amex
        } else {
            sourceToken = "tok_visa";       // Default fallback
        }

        // Logic to test DECLINES: type a specific number in the UI, e.g., "4000000000000002"
        if (cardNumber.endsWith("0002")) {
            sourceToken = "tok_chargeDeclined";
        } else if (cardNumber.endsWith("0004")) {
            sourceToken = "tok_chargeDeclinedInsufficientFunds";
        }

        // ---------------------------------------------

        // 2. Create the Charge directly using the Magic Token
        // Stripe expects amount in the smallest currency unit (e.g., paisa for PKR)
        long amountInSmallestUnit = (long) (amountPKR * 100);

        ChargeCreateParams params = ChargeCreateParams.builder()
                .setAmount(amountInSmallestUnit)
                .setCurrency("pkr")
                .setDescription("Flight Booking (Card ending in " + cardNumber.substring(Math.max(0, cardNumber.length() - 4)) + ")")
                .setSource(sourceToken) // Using the safe token instead of raw card
                .build();

        Charge charge = Charge.create(params);

        // 3. Return the Transaction ID if successful
        return charge.getId();
    }
}