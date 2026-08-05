# Iyzico Payment Service

## Proje Hakkında

Iyzico Payment Service, Spring Boot kullanılarak geliştirilmiş bir ödeme servisidir. Proje, iyzico Checkout Form entegrasyonu ile güvenli ödeme işlemlerinin başlatılması, ödeme sonucunun sorgulanması ve webhook bildirimlerinin işlenmesini sağlamaktadır.

Projede katmanlı mimari (Layered Architecture) uygulanmış olup iyzico SDK'sı uygulamanın iş mantığından ayrıştırılarak yalnızca entegrasyon katmanında kullanılmaktadır.

---

# Kullanılan Teknolojiler

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- Docker
- iyzico Java SDK
- React
- Vite
- Axios

---

# Proje Mimarisi

```
payment-service
│
├── config
│   ├── ApiKeyFilter.java
│   ├── IyzicoConfig.java
│   ├── OpenApiConfig.java
│   └── WebhookSignatureVerifier.java
│
├── controller
│   └── PaymentController.java
│
├── dto
│   ├── CheckoutFormRequest.java
│   ├── CheckoutFormResponse.java
│   ├── IyzicoWebhookRequest.java
│   ├── PaymentRequest.java
│   └── PaymentResponse.java
│
├── entity
│   ├── Payment.java
│   ├── PaymentTransaction.java
│   └── WebhookEvent.java
│
├── integration
│   └── iyzico
│       ├── IyzicoClient.java
│       ├── IyzicoCheckoutInitializeResult.java
│       ├── IyzicoCheckoutResult.java
│       └── IyzicoPaymentItemResult.java
│
├── repository
│   ├── PaymentRepository.java
│   ├── PaymentTransactionRepository.java
│   └── WebhookEventRepository.java
│
├── service
│   ├── PaymentService.java
│   └── WebhookService.java
│
└── PaymentServiceApplication.java
```

---

# Katmanların Görevleri

| Katman | Açıklama |
|---------|----------|
| **config** | Güvenlik, API Key doğrulaması, Swagger ve iyzico yapılandırmalarını içerir. |
| **controller** | HTTP isteklerini karşılar ve ilgili servis katmanına yönlendirir. |
| **dto** | Katmanlar arasında veri taşımak için kullanılan Request ve Response sınıflarını içerir. |
| **entity** | Veritabanı tablolarını temsil eden JPA Entity sınıflarını içerir. |
| **integration/iyzico** | iyzico SDK ile iletişim kuran entegrasyon katmanıdır. SDK yalnızca bu katmanda kullanılmaktadır. |
| **repository** | Spring Data JPA kullanılarak veritabanı işlemlerini gerçekleştirir. |
| **service** | Uygulamanın iş mantığını ve ödeme süreçlerini yönetir. |

---

# Uygulamaya Erişim

| Servis | Adres |
|---------|--------|
| Frontend | http://localhost:5173 |
| Backend | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI | http://localhost:8080/v3/api-docs |

---

# Kurulum

## 1. PostgreSQL

Docker ile PostgreSQL başlatılır.

```bash
docker compose up -d
```

## 2. Backend

```bash
cd payment-service
./mvnw spring-boot:run
```

## 3. Frontend

```bash
cd payment-web
npm install
npm run dev
```

---

# Ortam Değişkenleri

## Backend (.env)

```env
INTERNAL_API_KEY=your-api-key
IYZICO_API_KEY=your-iyzico-api-key
IYZICO_SECRET_KEY=your-iyzico-secret-key
```

## Frontend (.env)

```env
VITE_INTERNAL_API_KEY=your-api-key
```

---

# API Endpointleri

| Method | Endpoint | Açıklama |
|---------|----------|----------|
| POST | `/api/payments/checkout-form` | Checkout Form oluşturur ve ödeme sayfasını başlatır. |
| POST | `/api/payments/callback` | Ödeme tamamlandıktan sonra callback işlemini gerçekleştirir. |
| POST | `/api/payments/webhook` | iyzico tarafından gönderilen webhook bildirimlerini işler. |
| GET | `/api/payments/{conversationId}` | Conversation ID bilgisine göre ödeme sonucunu getirir. |

---

# Sandbox Ödeme Testi

Proje iyzico Sandbox ortamı kullanılarak test edilmektedir.

Örnek kart bilgileri:

| Alan | Değer |
|------|-------|
| Kart Numarası | 5528790000000008 |
| Son Kullanma Tarihi | 12/30 |
| CVV | 123 |

Frontend üzerinden ödeme başlatıldıktan sonra kullanıcı iyzico ödeme sayfasına yönlendirilir. Ödeme tamamlandıktan sonra callback mekanizması çalışır ve ödeme sonucu uygulama üzerinde görüntülenebilir.

---

# Sistem Akışı

```
React Frontend
        │
        ▼
Spring Boot REST API
        │
        ▼
API Key Doğrulama
        │
        ▼
Payment Service
        │
        ▼
Iyzico Client
        │
        ▼
iyzico Sandbox API
        │
        ▼
PostgreSQL
```

---

# Güvenlik

- API Key doğrulaması
- Webhook imza doğrulaması
- CORS yapılandırması
- Ortam değişkenleri (.env)
- Katmanlı mimari ile SDK izolasyonu

---

# Test

Proje, iyzico Sandbox ortamı kullanılarak test edilmiştir.

Test edilen senaryolar:

- Checkout Form oluşturma
- Sandbox ödeme işlemi
- Callback işlemi
- Webhook doğrulaması
- API Key doğrulaması
- Ödeme sonucu sorgulama

---

# Not

Bu proje, iyzico Checkout Form entegrasyonu, katmanlı mimari, API güvenliği ve webhook yönetimi gibi modern ödeme sistemlerinde kullanılan temel yaklaşımları örneklemek amacıyla hazırlanmıştır.
