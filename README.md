# Iyzico Payment Service

##  Proje Hakkında

Iyzico Payment Service, Spring Boot kullanılarak geliştirilmiş bir ödeme servisidir. Proje, iyzico Checkout Form entegrasyonu ile güvenli ödeme işlemlerinin başlatılması, ödeme sonucunun sorgulanması ve webhook bildirimlerinin işlenmesini sağlamaktadır.

Projede katmanlı mimari (Layered Architecture) uygulanmış olup iyzico SDK'sı uygulamanın iş mantığından ayrıştırılarak yalnızca entegrasyon katmanında kullanılmaktadır.

---

# ️ Kullanılan Teknolojiler

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

## ️ Kurulum

## 1. PostgreSQL

Docker ile PostgreSQL başlatılır.

```bash
docker compose up -d
```

## 2. Backend

```bash
./mvnw spring-boot:run
```

## 3. Frontend

```bash
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
| POST | `/api/payments/checkout-form` | Ödeme sayfasını oluşturur |
| POST | `/api/payments/callback` | Ödeme sonucunu işler |
| POST | `/api/payments/webhook` | iyzico webhook bildirimlerini alır |
| GET | `/api/payments/{conversationId}` | Ödeme bilgisini getirir |

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

Bu proje ödeme servislerinde kullanılan temel güvenlik mekanizmaları, katmanlı mimari ve iyzico Checkout Form entegrasyonunu örneklemek amacıyla hazırlanmıştır.
