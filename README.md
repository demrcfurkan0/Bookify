# Bookify - Kitap Kiralama Mikroservis Sistemi

## Proje Amacı

Bu proje, kullanıcıların kitap kiralayabileceği, kitap ve kullanıcı yönetimi yapabileceği bir mikroservis mimarisi uygulaması geliştirmeyi amaçlamaktadır. Sistem, temel CRUD işlemlerinin yanı sıra servisler arası iletişim, API gateway, config server, service discovery gibi bileşenlerini içerir.

## Kullanılan Teknolojiler

*   **Backend:**
    *   Java 17
    *   Spring Boot 3.4.0
    *   Spring Cloud (Eureka, Gateway, Config Server)
    *   Spring Data JPA
    *   PostgreSQL (Veritabanı)
    *   Lombok
    *   Spring Security (JWT)
*   **Frontend:**
    *   HTML, CSS
    *   Thymeleaf
*   **Test:**
    *   JUnit 5
    *   Mockito
*   **Diğer:**
    *   Maven (Bağımlılık Yönetimi)
    *   Postman / Swagger (API Testleri)
*   **IDE:** IntelliJ IDEA

## Mikroservis Mimarisi

Proje, aşağıdaki mikroservislerden oluşmaktadır:

1.  **User Service:** Kullanıcı kayıt ve yönetimi.
2.  **Book Service:** Kitap kayıt, yönetimi ve müsaitlik durumu takibi.
3.  **Rental Service:** Kitap kiralama ve iade işlemleri.
4.  **API Gateway:** Tüm API isteklerini yönlendirir, güvenlik, istek sınırlaması ve diğer çapraz kesim endişelerini yönetir.
5.  **Eureka Server (Service Registry):** Mikroservislerin birbirlerini bulmasını sağlar.
6.  **Config Server:**  Servislerin yapılandırma ayarlarını merkezi olarak yönetir.

## Kurulum ve Çalıştırma

### Ön Koşullar

*   **Java 17 veya üzeri** yüklü olmalı.
*   **Maven** yüklü olmalı.
*   **PostgreSQL** veritabanı kurulu olmalı.

### Veritabanı Oluşturma

1.  PostgreSQL'de, aşağıdaki veritabanlarını oluşturun:
    *   `book_db`
    *   `user_db`
    *   `rental_db`

### Projeyi Derleme

1.  Proje dizininde (yani, `bookify-project` klasöründe) terminali açın.
2.  Aşağıdaki komutu çalıştırarak projeyi derleyin:

    ```bash
    mvn clean install
    ```

### Servisleri Çalıştırma

1.  **Önce Eureka'yı başlatın:** `cd eureka-server && mvn spring-boot:run` (Port: 8761)
2.  **Sonra Config Server'ı başlatın:** `cd config-server && mvn spring-boot:run` (Port: 8888)
3.  **Ardından User Service'i başlatın:**  `cd user-service && mvn spring-boot:run` (Port: 8082)
4.  **Book Service'i başlatın:** `cd book-service && mvn spring-boot:run` (Port: 8081)
5.  **Rental Service'i başlatın:** `cd rental-service && mvn spring-boot:run` (Port: 8083)
6.  **Son olarak API Gateway'i başlatın:** `cd api-gateway && mvn spring-boot:run` (Port: 8080)

## API Endpoint'leri ve Swagger UI

*   API Gateway'in Swagger UI'ına erişmek için: `http://localhost:8080/swagger-ui/index.html`
*   **User Service API'ları:**
    *   `GET /api/users`  (Kullanıcıları Listeler)
    *   `POST /api/users/register` (Yeni Kullanıcı Kaydı)
    *   `GET /api/users/{userId}/rentals` (Kullanıcının Kiralamalarını Listeler)
*   **Book Service API'ları:**
    *   `GET /api/books` (Tüm kitapları listeler)
    *   `POST /api/books` (Yeni kitap ekler)
    *   `GET /api/books/{id}` (Kitap bilgisini ID'ye göre getirir)
*   **Rental Service API'ları:**
    *   `POST /api/rentals/rent` (Kitap kiralar)
    *   `PUT /api/rentals/{rentalId}/return` (Kitabı iade eder)

## Örnek Postman İstekleri

### Kullanıcı Kaydı (POST)

```bash
curl -X 'POST' \
  'http://localhost:8080/api/users/register' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123"
}'
