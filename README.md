# Payment Service

A fully flexible **Payment Service** built with **Spring Boot 3.5** that handles all types of payments, refunds, and transactions.  
Designed to be loose-coupled, extensible, and production-ready, so you can add new payment providers like **PhonePe, Google Pay, Paytm, Card**, etc., in the future.

---

## 🚀 Technology Stack

- Java 21
- Spring Boot 3.5
- H2 Database (for development/testing)
- Spring Data JPA
- Spring Web
- Spring Boot Actuator
- Micrometer (for future Prometheus integration)
- Maven
- Lombok

---

## 🏗 Project Structure

``` 
📂 Project Structure
src/main/java/com.sushilk/payment_service/
├── controllers      # REST endpoints
├── dtos             # Request & Response DTOs
├── entities         # JPA entities (Payment, PaymentTransaction, Refund, PaymentAttempt)
├── enums            # Enum classes (status, provider, transaction type)
├── repositories     # Spring Data JPA repositories
├── services         # Service interfaces
│   └── impl         # Service implementations
└── exceptions       # Custom exceptions & global error handling
```


---

## ✨ Features

- Initiate a payment with any provider.
- Refund a payment.
- Track all payment transactions.
- Idempotency support to avoid duplicate payments.
- Full audit of each request/transaction.
- Actuator endpoints for monitoring & metrics.
- Ready for future extension to multiple providers (PhonePe, Google Pay, Paytm, Card, etc.).
- Global exception handling with detailed error responses.

---

## ⚡ Getting Started

### Clone Repository

```bash
git clone <repository-url>
cd payment-service
```

Build Project
```mvn clean install```

Run Application
```mvn spring-boot:run```


Application will run on http://localhost:8080

🔹 REST Endpoints
Initiate Payment

URL: /payments/initiate
Method: POST

Request Body:
```
{
  "amount": 100.50,
  "currency": "INR",
  "orderId": "ORD123",
  "orderType": "PRODUCT",
  "paymentMethod": "CARD",
  "provider": "CARD",
  "userId": "USER123",
  "idempotencyKey": "unique-key-123",
  "description": "Payment for order ORD123"
}
```

Response:
```
{
  "paymentId": "uuid",
  "status": "CREATED",
  "provider": "CARD",
  "amount": 100.50
}
```
Refund Payment

URL: /payments/refund
Method: POST

Request Body:
```
{
  "paymentId": "uuid",
  "amount": 50.00,
  "reason": "Customer requested refund"
}
```

Response:
200 OK (Void)

📊 Actuator Endpoints (Monitoring)
```
/actuator/health : Application health status

/actuator/info : Application info

/actuator/metrics : JVM, DB, HTTP metrics

/actuator/httptrace : Last HTTP requests

/actuator/loggers : Dynamic logging levels

Example: http://localhost:8080/actuator/metrics
```
⚠️ Error Handling

All exceptions are handled globally and returned in a standard format using ErrorResponse:
```
status
message
timestamp
details
```
Example:
```
{
  "status": 400,
  "message": "Invalid payment amount",
  "timestamp": "2026-01-08T18:23:31.427",
  "details": "Amount must be greater than 0"
}
```
🔮 Future Improvements

- Add more payment providers: PhonePe, Google Pay, Paytm, UPI
- Integration with Prometheus + Grafana for real-time monitoring
- Use PostgreSQL/MySQL for production instead of H2
- Add unit & integration tests for all services
- Add JWT / Authentication for secure payment access
- Add idempotency via HTTP headers for production-grade duplicate prevention
