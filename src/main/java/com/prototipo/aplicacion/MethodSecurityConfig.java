package com.prototipo.aplicacion;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;


/*Para decidir si un usuario en sesion tiene accesso o no a utilizar un metodo necesitas activar la anotacion
@PreAuthorize y anotar el metodo deseado. Utiliza la misma expression que usamos en thymeleaf.
Para activar la anotacion de seguridad necesitamos crear otra clase de de configuracion en el paquete raiz,
es decir se agrega en el backend este prePostEnabled para poder utilizar la anotacion @PreAuthorize
*/
@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

}
