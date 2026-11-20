# ADA 11 · CRUD y flujo de compras

## Archivos y cambios

| Archivos | Cambio |
| --- | --- |
| `CategoryController`, `ClientController` | Controladores nuevos con GET de lista y por ID, POST, PUT y DELETE. |
| `CategoryService`, `ClientService`, `CategoryRepository`, `ClientRepository`, `CategoriaRepository`, `ClienteRepository`, `CategoriaCrudRepository`, `ClienteCrudRepository` | Capas nuevas para los catálogos, siguiendo el patrón del proyecto. |
| `Client`, `ClientMapper`, `CategoryMapper` | Modelo de cliente, mapeo de entidades y conversión de listas. El ID del cliente se proporciona al crear el registro. Los controladores devuelven modelos de dominio. |
| `ProductController`, `ProductService`, `ProductRepository`, `ProductoRepository`, `ProductMapper` | Inyección de dependencias, conexión del repositorio a su interfaz de dominio y PUT. La actualización conserva el código de barras previo. El DELETE de productos conserva su respuesta 200. |
| `PurchaseController`, `PurchaseRequest`, `PurchaseItemRequest` | Controlador nuevo con GET, POST, PUT y DELETE. El DTO de entrada usa los nombres en español de Teams y las respuestas conservan el modelo `Purchase`. |
| `PurchaseService`, `PurchaseRepository`, `CompraRepository`, `CompraCrudRepository`, `PurchaseMapper`, `PurchaseItemMapper` | Validación, mapeo y transacciones. POST guarda la cabecera y todos sus detalles con un solo `save`. PUT conserva la fecha y el estado de la cabecera y actualiza los detalles por ID de producto. |
| `Cliente`, `Producto` | Los tipos de JPA se ajustaron al esquema de clase. El ID de cliente es textual, el celular es numérico y el estado de producto puede ser nulo en registros previos. |
| `Compra`, `CompraProducto`, `CompraProductoPK` | Nombres de tabla y columna del esquema de clase, fecha con hora, clave compuesta y `@MapsId` para propagar el ID generado. La cascada y `orphanRemoval` se aplican de compra a detalles. |
| `ApiExceptionHandler` | Traduce las violaciones de integridad rechazadas por PostgreSQL a HTTP 409, por ejemplo al borrar un cliente con compras. |
| `application-dev.properties` | El ZIP usa `SPRING_DATASOURCE_PASSWORD` para evitar redistribuir la contraseña local. Conserva tu archivo local si ya tienes el proyecto configurado. |

Se conservan los paquetes `domain`, `persistence` y `web.controller`, las clases Java con getters y setters, los mappers de MapStruct y el patrón de `ProductController` para Swagger y `ResponseEntity`. En compras, `@MapsId` vincula la clave compuesta al ID de la cabecera y `@Transactional` revierte la operación completa ante un error de persistencia.

Al crear una compra, se guardan fecha y hora y el estado inicial `A`, con los detalles activos. `CLI-001` es el ID textual del cliente y se usa tal como está registrado. PostgreSQL guarda un carácter en `medio_pago`, así que `TARJETA` se guarda como `T` y `EFECTIVO` como `E`. La respuesta y pgAdmin muestran el código guardado. Los totales de cada detalle se reciben en el JSON de la consigna.

## Abrir y ejecutar en Windows

1. Descomprime el ZIP y abre en IntelliJ la carpeta que contiene `build.gradle`. Selecciona un JDK 25 para Gradle y usa el Gradle Wrapper del proyecto (9.8.0).
2. Inicia PostgreSQL. El perfil `dev` del proyecto apunta a la base `market-backend-26-3`. Si usas el ZIP en una carpeta nueva, establece `SPRING_DATASOURCE_PASSWORD` en tu sesión de PowerShell o en la configuración de ejecución de IntelliJ. Puedes usar `SPRING_DATASOURCE_URL` y `SPRING_DATASOURCE_USERNAME` si tu entorno tiene otros valores.
3. En PowerShell, ejecuta estos comandos dentro de la carpeta que contiene `gradlew.bat`.

   ```powershell
   .\gradlew.bat clean build
   .\gradlew.bat test
   .\gradlew.bat bootRun
   ```

4. Abre [Swagger UI](http://localhost:8080/swagger-ui/index.html). Puedes consultar también el [documento OpenAPI](http://localhost:8080/v3/api-docs).

Para usar una base de pruebas, crea una base vacía desde pgAdmin y ejecuta `docs/esquema-pruebas.sql` en ella. Configura `SPRING_DATASOURCE_URL` para apuntar a esa base antes de iniciar la API. El SQL reproduce los nombres, tipos y referencias que registra IntelliJ para la base de clase.

## Guion para grabar la evidencia en Swagger UI

Abre cada operación, pulsa **Try it out** y **Execute**. En la grabación muestra el cuerpo enviado, el código HTTP y el cuerpo de respuesta. Guarda los IDs reales devueltos por los POST. Los IDs de ejemplo de abajo son ilustrativos.

| Controlador | Secuencia y evidencia |
| --- | --- |
| Category | GET `/categories`. POST con el JSON de categoría y captura el ID de la respuesta 201. GET por ID. PUT con otra descripción. GET por ID para mostrar el cambio. |
| Client | GET `/clients`. POST con el JSON de cliente y guarda su ID. GET por ID. PUT con otra dirección y confirma con GET. |
| Product | GET `/products`. POST dos productos distintos con el ID de la categoría creada. GET por ID y GET `/products/category/{categoryId}`. PUT de uno, cambia el stock y confirma con GET. |
| Purchase | GET `/purchases`. POST con el JSON anidado y los IDs del cliente y de ambos productos. Muestra el 201, el ID de compra y ambos detalles. GET por ID y GET `/purchases/client/{clientId}`. Cambia el comentario y una cantidad mediante PUT, luego comprueba con GET y pgAdmin. |
| Limpieza | DELETE de la compra (204). GET por ID para ver el 404. DELETE de sus dos productos (200), de la categoría (204) y del cliente (204). Muestra los GET de verificación. Este orden respeta las referencias de la base. |

### Categoría

```json
{
  "category": "Postres",
  "active": true
}
```

### Cliente

Usa un ID de cliente disponible. Si `CLI-001` ya existe, elige otro y usa el mismo ID al crear la compra.

```json
{
  "clientId": "CLI-001",
  "name": "Ana",
  "lastName": "Pérez",
  "mobile": 9990000000,
  "address": "Mérida",
  "email": "ana.ada11@example.com"
}
```

### Producto

Sustituye `categoryId` por el ID obtenido arriba. Para el segundo producto cambia el nombre y el precio. Para PUT reenvía todos los campos editables y cambia, por ejemplo, `stock`.

```json
{
  "name": "Helado",
  "categoryId": 1,
  "price": 250.0,
  "stock": 30,
  "active": true
}
```

### Compra

Sustituye los IDs por los de tu prueba. Si el ID del cliente es `CLI-027`, envía `"CLI-027"`. El PUT usa la misma estructura y reemplaza los campos editables y la lista solicitada.

```json
{
  "idCliente": "CLI-001",
  "medioPago": "TARJETA",
  "comentario": "Entregar por la tarde",
  "productosAdquiridos": [
    {"idProducto": 15, "cantidad": 2, "total": 500.0},
    {"idProducto": 8, "cantidad": 1, "total": 150.0}
  ]
}
```

### Confirmar el guardado en pgAdmin

```sql
SELECT id_categoria, descripcion, estado FROM categorias WHERE id_categoria = 1;
SELECT id, nombre, direccion FROM clientes WHERE id = 'CLI-001';
SELECT id_producto, nombre, cantidad_stock FROM productos WHERE id_producto = 15;
```

Usa los IDs de tu prueba y muestra las filas antes y después de los PUT de catálogos. Para compras, reemplaza `42` por el `purchaseId` devuelto por el POST y ejecuta las consultas después de POST, PUT y DELETE.

```sql
SELECT id_compra, id_cliente, fecha, medio_pago, comentario, estado
FROM compras
WHERE id_compra = 42;

SELECT id_compra, id_producto, cantidad, total, estado
FROM compras_productos
WHERE id_compra = 42
ORDER BY id_producto;
```

Para demostrar el rollback real, usa una base de pruebas desechable. Con los dos productos y el cliente existentes, agrega temporalmente la restricción siguiente. Envía otra compra con un comentario único y pon `cantidad: 99` en el segundo detalle. El POST debe devolver 409. Consulta por el comentario y compara los conteos de ambas tablas antes y después. La compra de prueba debe quedar fuera de ambas tablas.

```sql
ALTER TABLE compras_productos
ADD CONSTRAINT ada11_qty CHECK (cantidad < 50);

SELECT COUNT(*) FROM compras;
SELECT COUNT(*) FROM compras_productos;

SELECT id_compra
FROM compras
WHERE comentario = 'Prueba rollback ADA11';

ALTER TABLE compras_productos DROP CONSTRAINT ada11_qty;
```

## Verificación ejecutada

| Verificación | Resultado |
| --- | --- |
| `./gradlew clean build` con JDK 25 y Gradle 9.8.0 | Build exitoso. Compiló los mappers de MapStruct 1.6.3 y generó el JAR ejecutable. |
| `./gradlew test --rerun-tasks` | `MarketBackendV2ApplicationTests.contextLoads()` pasó. |
| `java -jar build/libs/market-backend-v2-0.0.1-SNAPSHOT.jar` | Spring Boot 4.1.1 arrancó con PostgreSQL 17.12, usando una base de pruebas creada con `docs/esquema-pruebas.sql`. |
| Swagger y API | Swagger UI y `/v3/api-docs` respondieron 200. Pasaron las pruebas HTTP de los cuatro controladores, los filtros, la protección de registros referenciados y la conservación del código de barras. |
| Persistencia en PostgreSQL | Se verificaron cabecera y dos detalles tras POST, cambios de la lista tras PUT, borrado en cascada y rollback completo al forzar un error de base de datos en el segundo detalle. |

El entorno inicial tenía bloqueada la caché compartida de Gradle y una prueba posterior falló al quedarse sin conexión a PostgreSQL. Con una caché aislada y una instancia de prueba activa, la compilación y las pruebas se completaron. Queda verificar con tu instalación de Windows y grabar el video en Swagger UI.

## Incorporar los cambios en tu repositorio

1. Abre `C:\market-backend-v2` y comprueba con `git status --short` qué cambios ya tienes. Si tienes trabajo local en archivos que coinciden, compáralos en IntelliJ e integra las líneas correspondientes.
2. Copia el contenido de `src/main/java` sobre la carpeta equivalente de tu proyecto. Conserva `src/main/resources/application-dev.properties` de tu copia local para seguir usando tus datos de conexión. Puedes copiar esta guía en `docs` si la quieres conservar en tu proyecto.
3. Comprueba las diferencias en IntelliJ o con `git status --short` y `git diff`. Abre los archivos nuevos que muestra `git status`, porque `git diff` por sí solo solo muestra los archivos ya versionados.
4. Cuando termines de revisar y grabar la prueba, selecciona en IntelliJ los archivos que quieres incluir y crea tu commit manualmente. Puedes usar el mensaje `feat(api): complete catalog CRUD and purchase flow`. Una vez que lo hayas subido manualmente, abre el commit en GitHub y pega el enlace directo en Teams.
