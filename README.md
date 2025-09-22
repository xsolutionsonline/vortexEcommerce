# Vortex E-Commerce: Order Service

`order-service` es un microservicio backend para gestionar pedidos dentro de la plataforma de e-commerce de alto tráfico Vortex. Está diseñado para ser resiliente, escalable y mantenible, utilizando un enfoque de arquitectura hexagonal y comunicación basada en eventos.

## ✨ Características

- **Creación y gestión de órdenes**: Soporte para el ciclo de vida completo de una orden (creación, procesamiento, envío, entrega, cancelación).
- **Arquitectura Hexagonal**: Separación clara entre la lógica de negocio (dominio), los casos de uso (aplicación) y la tecnología (infraestructura).
- **Comunicación Asíncrona**: Utiliza Apache Kafka para publicar eventos de órdenes, permitiendo que otros servicios (como notificaciones o inventario) reaccionen a los cambios de estado de forma desacoplada.
- **Seguridad**: Protegido con Spring Security y autenticación basada en tokens JWT.
- **Caché Distribuido**: Integración con Redis para cachear datos y mejorar el rendimiento.
- **Manejo de Concurrencia**: Implementa bloqueo optimista para prevenir conflictos en actualizaciones de datos.

## 🏗️ Estructura del Proyecto

El proyecto sigue los principios de la **Arquitectura Hexagonal** (también conocida como Puertos y Adaptadores) para aislar la lógica de negocio central de las dependencias externas.

- `domain`: El núcleo del servicio. Contiene los modelos de negocio (`Order`, `OrderItem`), las reglas de negocio, las excepciones personalizadas y los *puertos* (interfaces que definen contratos para la comunicación con el exterior, como `OrderRepositoryPort` o `InventoryPort`). No tiene dependencias de ningún framework.
- `application`: La capa de orquestación. Contiene los casos de uso o servicios (`OrderService`) que utilizan el dominio para ejecutar las acciones solicitadas por el usuario.
- `infrastructure`: Contiene las implementaciones concretas (adaptadores) de los puertos definidos en el dominio.
  - `adapter/in`: Puntos de entrada a la aplicación.
    - `web`: Controladores REST que exponen la API.
    - `kafka`: Consumidores de eventos de Kafka.
  - `adapter/out`: Puntos de salida de la aplicación.
    - `persistence`: Implementación de los repositorios utilizando Spring Data JPA.
    - `kafka`: Productores de eventos para Kafka.
    - `http`: Clientes para consumir otras APIs (ej. servicio de inventario).
  - `config`: Clases de configuración de Spring para beans, seguridad, caché, etc.
  - `security`: Implementación de la lógica de JWT.

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 21
- **Framework**: Spring Boot 3.3.1
- **Datos**:
  - Spring Data JPA / Hibernate
  - Base de datos en memoria H2 (para desarrollo y pruebas)
- **Mensajería**:
  - Spring for Apache Kafka (para arquitectura orientada a eventos)
- **Caché**:
  - Spring Cache
  - Redis
- **Seguridad**:
  - Spring Security
  - JSON Web Tokens (JWT)
- **Build y Dependencias**:
  - Apache Maven
  - Lombok (para reducir código boilerplate)
  - MapStruct (para mapeo de objetos)
- **Pruebas**:
  - JUnit 5, Mockito, AssertJ
  - Spring Boot Test, Spring Security Test
  - Testcontainers (para pruebas de integración con Kafka y Redis)
- **Calidad de Código**:
  - Jacoco (para reportes de cobertura de código)

## 🚀 Ejecución y Despliegue

### Prerrequisitos

- JDK 21 o superior.
- Apache Maven 3.8 o superior.
- Docker y Docker Compose (para levantar los servicios de infraestructura).

### 1. Ejecución Local

Para ejecutar la aplicación en un entorno local, es necesario levantar las dependencias de infraestructura (Kafka y Redis). Se proporciona un archivo `docker-compose.yml` para facilitar este proceso.

**a. Levantar la infraestructura con Docker:**

Desde la raíz del proyecto, ejecuta:
```bash
docker-compose up -d
```
Este comando iniciará contenedores para:
- Zookeeper (en el puerto `2181`)
- Kafka (en el puerto `9092`)
- Redis (en el puerto `6379`)

**b. Configurar la aplicación:**

El archivo `application.properties` está configurado para conectarse a servicios en la nube. Para el desarrollo local, es recomendable usar un perfil de Spring.

Crea un archivo `src/main/resources/application-local.properties` con el siguiente contenido para apuntar a los servicios de Docker:

```properties
# Kafka
spring.kafka.bootstrap-servers=localhost:9092
app.kafka.topic.replicas=1 # En local, solo tenemos un broker

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.ssl.enabled=false
```

**c. Iniciar la aplicación:**

Puedes iniciar la aplicación desde tu IDE o usando el siguiente comando de Maven, activando el perfil `local`:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

La aplicación estará disponible en `http://localhost:8080`.

### 2. Ejecutar Pruebas

Para ejecutar el conjunto completo de pruebas unitarias y de integración, utiliza el siguiente comando de Maven. Esto también generará un reporte de cobertura en `target/site/jacoco/index.html`.

```bash
mvn clean verify
```

## 🔑 API y Autenticación

La API está protegida y requiere un token JWT para la mayoría de los endpoints.

### 1. Obtener un Token

Puedes obtener un token autenticándote con uno de los usuarios de prueba definidos en `application.properties`.

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{
    "username": "user",
    "password": "user123"
}'
```

### Consola H2

Mientras la aplicación se ejecuta con el perfil por defecto o `local`, puedes acceder a la consola de la base de datos en memoria H2 para inspeccionar los datos.

- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:vortexdb`
- **Username**: `vortex`
- **Password**: `admin1234`

## ☁️ Despliegue en Azure

El archivo `azure-deploy.yml` contiene una definición de plantilla de Azure Resource Manager (ARM) para desplegar los contenedores de Kafka y Zookeeper en Azure Container Instances (ACI). Esto sirve como ejemplo de cómo se podría configurar un entorno de desarrollo/pruebas en la nube.
