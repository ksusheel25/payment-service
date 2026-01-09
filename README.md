# Payment Service

A production-ready, flexible **Payment Service** built with **Spring Boot 3.5** that handles multiple payment providers, transaction types, and payment methods.  
Designed with loose coupling, extensibility, and industry-grade design patterns to support e-commerce, P2P transfers, subscriptions, and more.

---

## 🚀 Technology Stack

- **Java 21**
- **Spring Boot 3.5.9**
- **H2 Database** (for development/testing)
- **Spring Data JPA**
- **Spring Web** (REST APIs)
- **Spring Boot Actuator** (Monitoring)
- **Jakarta Bean Validation** (Comprehensive input validation)
- **Lombok** (Boilerplate reduction)
- **Maven** (Build tool)

---

## 🏗 Project Structure

```
📂 Project Structure
src/main/java/com/sushilk/payment_service/
├── controllers/          # REST endpoints (PaymentController)
├── dtos/                # Data Transfer Objects
│   ├── InitiatePaymentRequest
│   ├── InitiatePaymentResponse
│   ├── RefundRequest
│   ├── CardDetails
│   ├── UPIDetails
│   ├── BeneficiaryDetails
│   ├── NetBankingDetails
│   └── ProviderResponse
├── entities/            # JPA entities
│   ├── Payment
│   ├── PaymentAttempt
│   ├── PaymentTransaction
│   ├── Refund
│   └── BaseEntity
├── enums/              # Enum classes
│   ├── PaymentProvider (PHONEPE, PAYTM, GOOGLEPAY, CARD)
│   ├── PaymentMethod (UPI, CARD, NET_BANKING)
│   ├── PaymentStatus
│   ├── PaymentAttemptStatus
│   ├── TransactionStatus
│   ├── TransactionType
│   ├── RefundStatus
│   └── OrderType
├── repositories/        # Spring Data JPA repositories
├── services/           # Service layer
│   ├── PaymentService
│   ├── PaymentProviderService (Strategy interface)
│   ├── PaymentProviderFactory
│   └── impl/
│       ├── PaymentServiceImpl
│       ├── PhonePePaymentProvider
│       ├── PaytmPaymentProvider
│       ├── GooglePayPaymentProvider
│       └── CardPaymentProvider
├── validation/         # Custom validators
│   ├── PaymentDetailsRequired
│   ├── ValidCardNumber (Luhn algorithm)
│   ├── ValidCardExpiry
│   └── ValidUPIId
└── exceptions/         # Exception handling
    ├── GlobalExceptionHandler (RFC 7807 ProblemDetail)
    ├── PaymentNotFoundException
    └── PaymentAlreadyExistsException
```

---

## ✨ Features

### Core Features
- ✅ **Multiple Payment Providers**: PhonePe, Paytm, GooglePay, Card
- ✅ **Multiple Payment Methods**: UPI, Card, Net Banking
- ✅ **Transaction Types**: E-commerce, P2P, Subscription, Wallet, Bill Payment, Donation
- ✅ **Comprehensive Validation**: Provider-based validation with detailed error messages
- ✅ **Idempotency Support**: Prevents duplicate payments using idempotency keys
- ✅ **Full Audit Trail**: Tracks all payment attempts, transactions, and refunds
- ✅ **Refund Management**: Complete refund lifecycle with validation
- ✅ **Provider-Agnostic Design**: Strategy + Factory pattern for easy extension

### Security & Privacy
- 🔒 **PCI Compliance**: Sensitive card details are masked before storage
- 🔒 **Data Masking**: UPI IDs, account numbers, phone numbers are masked in logs
- 🔒 **Secure Storage**: Only masked sensitive data stored in audit logs

### Validation Features
- ✅ **Card Number Validation**: Luhn algorithm (Mod 10) validation
- ✅ **Card Expiry Validation**: Format and expiration date validation
- ✅ **UPI ID Validation**: Format validation for VPA (Virtual Payment Address)
- ✅ **Phone Number Validation**: 10-digit Indian mobile number format
- ✅ **Provider-Payment Method Compatibility**: Ensures correct combinations
- ✅ **Beneficiary Validation**: Required for P2P, Bill Payment, Donation

### Monitoring & Observability
- 📊 **Spring Boot Actuator**: Health, metrics, environment, beans endpoints
- 📊 **Structured Logging**: Comprehensive logging with masked sensitive data
- 📊 **Error Tracking**: RFC 7807 ProblemDetail standard error responses

---

## ⚡ Getting Started

### Prerequisites
- Java 21
- Maven 3.6+

### Clone & Build

```bash
git clone <repository-url>
cd payment-service
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

Application will run on **http://localhost:8080**

### Database Console
H2 Console is available at **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:paymentdb`
- Username: `sa`
- Password: `password`

---

## 📡 API Endpoints

### 1. Initiate Payment

**Endpoint:** `POST /payments/initiate`

**Description:** Initiates a payment with the specified provider and payment method.

#### Example 1: Card Payment (E-commerce)

```json
{
  "userId": "USER123",
  "orderId": "PROD_ORD_001",
  "orderType": "PRODUCT",
  "amount": 1000.50,
  "currency": "USD",
  "provider": "CARD",
  "paymentMethod": "CARD",
  "idempotencyKey": "unique-card-key-123",
  "cardDetails": {
    "cardNumber": "4111111111111111",
    "cardholderName": "John Doe",
    "expiryDate": "12/25",
    "cvv": "123"
  }
}
```

#### Example 2: UPI Payment (PhonePe)

```json
{
  "userId": "USER123",
  "orderId": "ORD_456",
  "orderType": "PRODUCT",
  "amount": 500.00,
  "currency": "INR",
  "provider": "PHONEPE",
  "paymentMethod": "UPI",
  "idempotencyKey": "unique-phonepe-key-456",
  "upiDetails": {
    "upiId": "user@paytm",
    "phoneNumber": "9876543210"
  }
}
```

#### Example 3: P2P Transfer (Person-to-Person)

```json
{
  "userId": "USER123",
  "orderId": "P2P_ORD_001",
  "orderType": "P2P",
  "amount": 500.00,
  "currency": "INR",
  "provider": "PAYTM",
  "paymentMethod": "UPI",
  "idempotencyKey": "unique-p2p-key-789",
  "upiDetails": {
    "upiId": "sender@paytm",
    "phoneNumber": "9876543210"
  },
  "beneficiaryDetails": {
    "beneficiaryId": "USER456",
    "beneficiaryName": "Jane Doe",
    "beneficiaryType": "USER",
    "beneficiaryAccount": "receiver@paytm"
  }
}
```

#### Example 4: Net Banking Payment

```json
{
  "userId": "USER123",
  "orderId": "NB_ORD_001",
  "orderType": "PRODUCT",
  "amount": 2000.00,
  "currency": "INR",
  "provider": "CARD",
  "paymentMethod": "NET_BANKING",
  "idempotencyKey": "unique-nb-key-101",
  "netBankingDetails": {
    "bankCode": "HDFC",
    "bankName": "HDFC Bank",
    "customerId": "CUST123456"
  }
}
```

#### Success Response (201 Created)

```json
{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING"
}
```

### 2. Refund Payment

**Endpoint:** `POST /payments/refund`

**Description:** Processes a refund for an existing successful payment.

#### Request

```json
{
  "paymentId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 250.00,
  "reason": "Customer requested refund - Product defect"
}
```

#### Success Response (200 OK)

Empty response body - refund initiated successfully.

---

## 🔍 Validation Rules

### Provider-Based Validation

#### CARD Provider
- ✅ **paymentMethod** must be `CARD`
- ✅ **cardDetails** required
- ✅ Card number validated with Luhn algorithm
- ✅ Expiry date must be valid and not expired
- ✅ CVV must be 3-4 digits
- ❌ UPI details must NOT be provided
- ❌ Net banking details must NOT be provided

#### PHONEPE/PAYTM/GOOGLEPAY Providers
- ✅ **paymentMethod** must be `UPI`
- ✅ **upiDetails** required
- ✅ UPI ID format validation (username@bankname)
- ✅ Phone number validation (10-digit Indian mobile)
- ❌ Card details must NOT be provided
- ❌ Net banking details must NOT be provided

#### NET_BANKING Payment Method
- ✅ **netBankingDetails** required
- ✅ Bank code required
- ❌ Card/UPI details must NOT be provided

### Order Type Validation

#### P2P, BILL_PAYMENT, DONATION
- ✅ **beneficiaryDetails** required
- ✅ Beneficiary ID required
- ✅ Beneficiary account/UPI ID for transfers

#### PRODUCT, SUBSCRIPTION, WALLET
- ℹ️ Beneficiary details optional (merchant info can be derived from orderId)

### Field-Level Validation

#### Amount
- Must be positive (> 0)
- Minimum: 0.01
- Maximum: Based on provider limits

#### Currency
- 3-letter ISO code (e.g., USD, INR, EUR)
- Uppercase only

#### Card Number
- 13-19 digits
- Luhn algorithm validation
- Digits only (spaces/hyphens auto-removed)

#### UPI ID
- Format: `username@bankname` or `phone@provider`
- Valid handles: paytm, ybl, gpay, okaxis, okhdfcbank, etc.
- 5-256 characters

#### Refund Reason
- Required
- 5-500 characters

---

## 📊 Monitoring Endpoints

### Actuator Endpoints

```
GET /actuator/health      # Application health status
GET /actuator/info        # Application info
GET /actuator/metrics     # JVM, DB, HTTP metrics
GET /actuator/env         # Environment properties
GET /actuator/beans       # Spring beans information
```

**Example:** http://localhost:8080/actuator/health

---

## ⚠️ Error Handling

All exceptions are handled globally using **RFC 7807 ProblemDetail** standard format.

### Error Response Format

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "One or more validation errors occurred. Please check the fieldErrors for details.",
  "instance": "/payments/initiate",
  "timestamp": "2024-01-15T10:30:00Z",
  "errorCode": "VALIDATION_ERROR",
  "totalErrors": 2,
  "summary": "currency: Currency must be a valid 3-letter uppercase ISO code; amount: Amount must be greater than zero",
  "fieldErrors": [
    {
      "field": "currency",
      "message": "Currency must be a valid 3-letter uppercase ISO code",
      "rejectedValue": "inr"
    },
    {
      "field": "amount",
      "message": "Amount must be greater than zero",
      "rejectedValue": "-100"
    }
  ]
}
```

### Common Error Codes

- `VALIDATION_ERROR`: Input validation failed
- `PAYMENT_NOT_FOUND`: Payment does not exist
- `PAYMENT_ALREADY_EXISTS`: Duplicate payment attempt (idempotency)
- `INVALID_OPERATION`: Invalid payment state for operation
- `INVALID_ARGUMENT`: Invalid argument provided
- `INTERNAL_ERROR`: Unexpected server error

### Example Error Scenarios

#### Missing Required Fields

```json
{
  "title": "Validation Failed",
  "status": 400,
  "errorCode": "VALIDATION_ERROR",
  "fieldErrors": [
    {
      "field": "cardDetails",
      "message": "Card details are required when provider is CARD"
    }
  ]
}
```

#### Provider-Payment Method Mismatch

```json
{
  "title": "Validation Failed",
  "status": 400,
  "detail": "Payment method must be UPI when provider is PHONEPE",
  "errorCode": "VALIDATION_ERROR"
}
```

#### Invalid Payment State

```json
{
  "title": "Invalid Operation",
  "status": 400,
  "detail": "Payment with status 'FAILED' cannot be refunded. Only payments with status SUCCESS or PROCESSING can be refunded.",
  "errorCode": "INVALID_OPERATION"
}
```

---

## 🏛 Architecture

### Design Patterns

#### Strategy Pattern
- `PaymentProviderService` interface defines provider contract
- Each provider (PhonePe, Paytm, GooglePay, Card) implements the interface
- Provider-specific logic encapsulated in separate classes

#### Factory Pattern
- `PaymentProviderFactory` dynamically selects provider implementation
- Uses Map-based lookup for O(1) provider resolution

### Payment Flow

```
1. Client Request → PaymentController
2. Validation (Provider-based + Field-level)
3. Idempotency Check
4. Create Payment (CREATED)
5. Create PaymentAttempt (INITIATED)
6. Create PaymentTransaction (DEBIT, INITIATED)
7. Update Payment (INITIATED)
8. Call Provider (Strategy pattern)
9. Update Attempt, Transaction, Payment status
10. Return Response
```

### Refund Flow

```
1. Validate Payment exists & is refundable
2. Validate refund amount ≤ remaining refundable amount
3. Create Refund entry (INITIATED)
4. Create PaymentTransaction (REFUND, INITIATED)
5. Update Payment (REFUND_INITIATED)
6. Call Provider refund API
7. Update Refund, Transaction, Payment status
8. Rollback on failure (restore original status)
```

### Transaction Lifecycle

**Payment Statuses:**
- `CREATED` → `INITIATED` → `PROCESSING` → `SUCCESS` / `FAILED`
- `SUCCESS` → `REFUND_INITIATED` → `REFUNDED`

**Transaction Statuses:**
- `INITIATED` → `SUCCESS` / `FAILED`

**Refund Statuses:**
- `INITIATED` → `SUCCESS` / `FAILED`

---

## 🔐 Security Features

### Data Masking

All sensitive information is masked before storage:

- **Card Numbers**: `****-****-****-1234` (last 4 digits only)
- **CVV**: `***`
- **Expiry Date**: `**/**`
- **UPI IDs**: `user@pa***tm` (partial masking)
- **Phone Numbers**: `******1234` (last 4 digits only)
- **Account Numbers**: `****1234` (last 4 digits only)

### PCI Compliance

- Raw card details never stored in database
- Card details only exist in memory during processing
- All card information masked in audit logs
- Recommendation: Use tokenization services (Stripe, PayPal) in production

---

## 📋 Supported Transaction Types

### OrderType Enum

- **PRODUCT**: E-commerce product purchase
- **SUBSCRIPTION**: Recurring subscription payment
- **WALLET**: Wallet top-up or wallet-to-wallet transfer
- **P2P**: Person-to-person direct transfer (requires beneficiary)
- **BILL_PAYMENT**: Utility bill payments (requires beneficiary)
- **DONATION**: Donation payments (requires beneficiary)

---

## 🔌 Payment Providers

### Currently Supported

1. **CARD**: Card payments (Visa, Mastercard, etc.)
   - Payment Method: CARD
   - Requires: cardDetails

2. **PHONEPE**: PhonePe UPI payments
   - Payment Method: UPI
   - Requires: upiDetails

3. **PAYTM**: Paytm UPI payments
   - Payment Method: UPI
   - Requires: upiDetails

4. **GOOGLEPAY**: Google Pay UPI payments
   - Payment Method: UPI
   - Requires: upiDetails

### Adding New Providers

To add a new payment provider:

1. Create provider implementation:
```java
@Service
public class NewPaymentProvider implements PaymentProviderService {
    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.NEW_PROVIDER;
    }
    
    @Override
    public ProviderResponse initiatePayment(Payment payment) {
        // Provider-specific logic
    }
    
    @Override
    public ProviderResponse refundPayment(Payment payment, String reason) {
        // Provider-specific refund logic
    }
}
```

2. Add to `PaymentProvider` enum
3. Update validation rules if needed

---

## 🧪 Testing

### Running Tests

```bash
mvn test
```

### Test Coverage

- Unit tests for services (planned)
- Integration tests with H2 database (planned)
- Provider mock tests (planned)

---

## 🔮 Future Enhancements

### Planned Features

- [ ] Real provider SDK integration (PhonePe, Paytm, GooglePay APIs)
- [ ] Webhook handling from providers for payment confirmation
- [ ] Retry mechanism with exponential backoff
- [ ] Circuit breaker pattern (Resilience4j)
- [ ] Async payment confirmation processing
- [ ] Kafka event streaming for payment events
- [ ] Multi-currency support with conversion
- [ ] Settlement reports and reconciliation
- [ ] Admin dashboard for payment management
- [ ] JWT authentication for secure API access
- [ ] Rate limiting for API protection
- [ ] Prometheus + Grafana integration
- [ ] PostgreSQL/MySQL support for production
- [ ] Comprehensive unit & integration tests
- [ ] Contract testing for providers
- [ ] Payment analytics and reporting

---

## 📝 Best Practices

### Idempotency

Always provide a unique `idempotencyKey` for each payment request. Same key = same response (prevents duplicate charges).

### Error Handling

Always check error response structure:
- Check `errorCode` for specific error type
- Review `fieldErrors` array for validation issues
- Use `errorCode` for programmatic error handling

### Security

- Never log full card numbers or sensitive data
- Use masked data for debugging
- Implement tokenization for production
- Follow PCI DSS compliance guidelines

### Provider Selection

- Choose provider based on payment method
- Ensure provider-payment method compatibility
- Validate all required details before initiating payment

---

## 🤝 Contributing

1. Follow existing code structure and patterns
2. Add comprehensive validation for new features
3. Maintain backward compatibility
4. Update documentation for any changes
5. Add tests for new functionality
