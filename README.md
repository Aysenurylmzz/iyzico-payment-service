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
- Spring Boot Actuator
- iyzico Java SDK
- React
- Vite
- Axios
- ngrok
- JUnit 5
- Mockito

---

# Proje Mimarisi

```text
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
├── exception
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── PaymentNotFoundException.java
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
| --- | --- |
| **config** | Güvenlik, API Key doğrulaması, Swagger, webhook imza doğrulaması ve iyzico yapılandırmalarını içerir. |
| **controller** | HTTP isteklerini karşılar ve ilgili servis katmanına yönlendirir. |
| **dto** | Katmanlar arasında veri taşımak için kullanılan Request ve Response sınıflarını içerir. |
| **entity** | Veritabanı tablolarını temsil eden JPA Entity sınıflarını içerir. |
| **exception** | Özel hata sınıflarını ve merkezi hata yönetimi yapısını içerir. |
| **integration/iyzico** | iyzico SDK ile iletişim kuran entegrasyon katmanıdır. SDK yalnızca bu katmanda kullanılmaktadır. |
| **repository** | Spring Data JPA kullanılarak veritabanı işlemlerini gerçekleştirir. |
| **service** | Uygulamanın iş mantığını ve ödeme süreçlerini yönetir. |

---

# Uygulamaya Erişim

| Servis | Adres |
| --- | --- |
| Frontend | `http://localhost:5173` |
| Backend | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/actuator/health` |

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

Gizli bilgiler Git deposuna eklenmez. Gerekli değişkenler `.env.example` dosyaları üzerinden örnek olarak gösterilmektedir.

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
| --- | --- | --- |
| POST | `/api/payments/checkout-form` | Checkout Form oluşturur ve ödeme sayfasını başlatır. |
| POST | `/api/payments/callback` | Ödeme tamamlandıktan sonra callback işlemini gerçekleştirir. |
| POST | `/api/payments/webhook` | iyzico tarafından gönderilen webhook bildirimlerini işler. |
| GET | `/api/payments/{conversationId}` | Conversation ID bilgisine göre ödeme sonucunu getirir. |
| GET | `/actuator/health` | Uygulamanın çalışma durumunu kontrol eder. |

---

# Ödeme Akışı

1. Frontend üzerinden ödeme bilgileri backend'e gönderilir.
2. Backend benzersiz bir `conversationId` oluşturur.
3. iyzico Checkout Form başlatılır.
4. Kullanıcı iyzico Sandbox ödeme sayfasına yönlendirilir.
5. Ödeme tamamlandıktan sonra callback endpoint'i çağrılır.
6. Ödeme sonucu iyzico üzerinden sorgulanır.
7. Ödeme ve işlem bilgileri PostgreSQL veritabanına kaydedilir.
8. Kullanıcı frontend sonuç sayfasına yönlendirilir.

Başarılı olarak tamamlanan bir ödeme tekrar callback aldığında yeniden işlenmez.

---

# Sandbox Ödeme Testi

Proje iyzico Sandbox ortamı kullanılarak test edilmektedir.

Örnek Sandbox kart bilgileri:

| Alan | Değer |
| --- | --- |
| Kart Numarası | 5528790000000008 |
| Son Kullanma Tarihi | 12/30 |
| CVV | 123 |

Frontend üzerinden ödeme başlatıldıktan sonra kullanıcı iyzico ödeme sayfasına yönlendirilir. Ödeme tamamlandıktan sonra callback mekanizması çalışır ve ödeme sonucu uygulama üzerinde görüntülenir.

---

# Ngrok ile Callback Testi

Local ortamda çalışan backend'in internet üzerinden erişilebilir olması amacıyla ngrok kullanılabilir.

Örnek:

```bash
ngrok http 8080
```

Oluşturulan HTTPS adresi callback adresi olarak kullanılarak iyzico Sandbox ortamından gelen callback isteğinin local backend'e ulaşması sağlanabilir.

Ngrok üzerinden gerçekleştirilen testlerde callback isteğinin backend'e başarıyla ulaştığı ve ödeme sonrasında frontend sonuç sayfasına yönlendirme işleminin gerçekleştiği doğrulanmıştır.

> Ngrok tarafından oluşturulan URL oturumlara göre değişebileceğinden sabit bir ngrok adresi README içerisinde tutulmamaktadır.

---

# Hata Yönetimi

Uygulamadaki hataların merkezi olarak yönetilmesi amacıyla `GlobalExceptionHandler` kullanılmaktadır.

Ödeme kaydı bulunamadığında `PaymentNotFoundException` fırlatılır ve standart `ErrorResponse` formatında `404 Not Found` yanıtı döndürülür.

Örnek hata cevabı:

```json
{
  "timestamp": "2026-08-07T09:00:00",
  "status": 404,
  "message": "Ödeme bulunamadı."
}
```

---

# Actuator Health Check

Uygulamanın çalışma durumunun izlenebilmesi amacıyla Spring Boot Actuator kullanılmaktadır.

```text
GET /actuator/health
```

Uygulama sağlıklı çalıştığında örnek cevap:

```json
{
  "groups": [
    "liveness",
    "readiness"
  ],
  "status": "UP"
}
```

Health endpoint'i `ApiKeyFilter` içerisinde API Key doğrulamasından muaf tutulmuştur.

---

# Güvenlik

Projede aşağıdaki güvenlik önlemleri uygulanmıştır:

- API Key doğrulaması
- Webhook HMAC-SHA256 imza doğrulaması
- Webhook tekrar işleme kontrolü
- CORS yapılandırması
- Ortam değişkenleri ile hassas bilgilerin yönetimi
- `.env` dosyalarının Git deposuna gönderilmemesi
- Katmanlı mimari ile iyzico SDK izolasyonu
- Başarılı ödemelerin tekrar işlenmesinin engellenmesi

---

# Testler

Projede JUnit 5 ve Mockito kullanılarak otomatik testler geliştirilmiştir.

Test sınıfları:

- `PaymentServiceApplicationTests`
- `PaymentControllerTest`
- `WebhookServiceTest`
- `WebhookSignatureVerifierTest`
- `PaymentServiceTest`

Test edilen başlıca senaryolar:

- Spring Application Context'in yüklenmesi
- Callback sonrası frontend sonuç sayfasına yönlendirme
- Webhook imza doğrulaması
- Geçersiz ve eksik webhook imzalarının reddedilmesi
- Aynı webhook'un birden fazla kez işlenmesinin engellenmesi
- Başarılı (`SUCCESS`) ödemenin tekrar işlenmesinin engellenmesi
- API Key doğrulaması
- Ödeme sonucu sorgulama
- 404 hata yönetimi
- Sandbox ödeme akışı

Testler aşağıdaki komut ile çalıştırılabilir:

```bash
./mvnw test
```

---

# Sistem Akışı

```text
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
Callback / Webhook
        │
        ▼
PostgreSQL
```

---

# Not

Bu proje; iyzico Checkout Form entegrasyonu, katmanlı mimari, API güvenliği, merkezi hata yönetimi, webhook doğrulaması, sağlık kontrolü ve otomatik testler gibi modern ödeme servislerinde kullanılan temel yaklaşımları uygulamak amacıyla geliştirilmiştir.
