## DataSealGuard

`DataSealGuard` es una librería diseñada específicamente para reforzar la seguridad y validación en sistemas donde las tablas detalles se relacionan con tablas maestras a través de identificadores (ID y FK). En muchas aplicaciones, las tablas detalles no contienen toda la información, sino que se remiten a tablas maestras a través de relaciones. Esta característica, aunque optimiza el almacenamiento, puede ser un vector de ataque si no se maneja adecuadamente.

Para garantizar la integridad y autenticidad de las operaciones entre estas tablas, `DataSealGuard` utiliza firmas basadas en JWT (JSON Web Token) o HMAC. Al realizar una operación en una tabla maestra, se genera una firma que debe ser presentada cuando se desea realizar una operación correspondiente en una tabla detalle. De esta forma, se verifica que quien realiza operaciones en los detalles tiene la autorización correcta proveniente de la operación maestra.

Además, esta librería es altamente compatible con frameworks y servidores populares como JAX-RS, Spring Boot y Payara Micro, facilitando su integración en una amplia variedad de proyectos Java, donde la seguridad normal pueda ser de http session, jwt, tokens o algun otro, el jwt o hmac de datasealguard no choca con ninguno de estos.

### Instalación

Para incluir `DataSealGuard` en tu proyecto, añade la siguiente dependencia a tu archivo `pom.xml`:

```xml
<dependency>
    <groupId>org.osbo.core</groupId>
    <artifactId>DataSealGuard</artifactId>
    <version>1.0-SNAPSHOT</version>
    <type>jar</type>
</dependency>
```
Nota. pronto en maven repo

### Ejemplo de Uso

1. **Generación del Token en la Tabla Maestra:**

    Aquí está un ejemplo de cómo podrías generar un token en el controlador de ventas (`VentasController`):

    ```java
    @RestController
    @RequestMapping("venta")
    public class VentasController {

    @Value("${secret}")
    private String secret;

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody Venta venta, HttpSession httpsession) {
        User current = SessionSystem.getCurrent(httpsession);
        if (venta.getIdTienda() == current.getIdTienda() && current != null) {
            // aca hariamos la accion de persistir
            
            venta.setIdUsuario(current.getUser());

            // aca vamos a generar el token antes de responder 
            // depende como lo usamos puede estar en varios lugares
            ValidateObject<Integer, Map<String,Integer>> validate = new ValidateObject<Integer, Map<String,Integer>>();
            validate.setUser(current.getUser());
            validate.setData(new HashMap<String,Integer>());
            validate.getData().put("idventa", venta.getId());
            validate.getData().put("idtienda", venta.getIdTienda());
            SignerDataValidate sign = new SignerDataValidate();
            sign.setType(TypeSign.JWT)
                    .setTimeexpire(400)
                    .setSecret(secret);
            String token = sign.forSend(validate);

            // lo agregamos a la respuesta
            return ResponseEntity
                    .accepted()
                    .header("validation", token)
                    .body(venta);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("ud no tiene permisos");
        }
    }
    }

    ```

2. **Validación en el Detalle de la Tabla:**

    Aquí tienes un ejemplo de cómo validar en el detalle de ventas (`DetalleVentasController`):

    ```java
    @RestController
    @RequestMapping("detalleventas")
    public class DetalleVentasController {

    @Value("${secret}")
    private String secret;

    @PostMapping
    public ResponseEntity<?> inserta(@RequestBody DetalleVenta detalleventa,
            HttpSession httpsession,
            @RequestHeader("validate") String valid) {

        User current = SessionSystem.getCurrent(httpsession);

        ValidateObject<Integer, Map<String, Integer>> validate = new ValidateObject<Integer, Map<String, Integer>>();
        Type type = new TypeToken<ValidateObject<Integer, Map<String, Integer>>>() {
        }.getType();

        SignerDataValidate<ValidateObject<Integer, Map<String, Integer>>> sign = new SignerDataValidate();
        sign.setType(TypeSign.JWT)
                .setSecret(secret);
        
        try {
            validate = sign.extract(valid, type);
            if (validate.getData().get("idtienda") == current.getIdTienda()
                    && current != null
                    && validate.getUser() == current.getUser()
                    && validate.getData().get("idventa") == detalleventa.getIdVenta()) {
                // aca hariamos la accion de persistir

                return ResponseEntity
                        .accepted()
                        .body(detalleventa);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("ud no tiene permisos sobre estos datos");
            }
        } catch (InvalidSignException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("ud no tiene permisos sobre estos datos");
        }

    }
    }


    ```

### Características

- **Versátil**: Puede ser utilizado en cualquier microservicio desarrollado en Java.
- **Seguro**: Utiliza JWT y HMAC como opciones para firmar datos, garantizando la integridad y autenticidad de la información.
- **Integración con Frameworks Populares**: Compatible con JAX-RS, Spring Boot, Payara Micro.

### Licencia

DataSealGuard se distribuye bajo la licencia MIT. Puedes leer los términos completos y detalles de la licencia en [este enlace](https://opensource.org/licenses/MIT).
