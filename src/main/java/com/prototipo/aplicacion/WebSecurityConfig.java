package com.prototipo.aplicacion;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

    //Indica que esta clase es de configuracion y necesita ser cargada durante el inicio del server
    @Configuration
    //Indica que esta clase sobreescribira la implmentacion de seguridad web
    @EnableWebSecurity
    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    String[] resources= new String[]{
            "/include/**","/css/**","/icons/**","/img/**","/js/**","/layer/**"
    };

    //Este metodo se sobreescribe de la clase heredada
    @Override
    protected void configure(HttpSecurity http) throws Exception {
                http
                 //Le estamos diciendo que vamos a autorizar unos requests
                .authorizeRequests()
                 //Cualquiera que tenga la palabra dentro de la ruta resources que le permita acceso
                .antMatchers(resources).permitAll()
                 //Cualquiera puede ingresar a la pagina index o a la pagina vacia
                .antMatchers("/","/index","/signup").permitAll()
                    //Cualquier otro request que se haga a parte de los de arriba va necesitar autenticacion
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                     //Y el formulario de login es el slash / login
                    .loginPage("/login")
                    //A este formulario pueden acceder todos
                    .permitAll()
                    //Si te logeas exitosamente  te va redirigir al userForm
                    .defaultSuccessUrl("/userForm")
                    //Si fallas al logearte te devuelve al login con este parametro de error true
                    .failureUrl("/login?error=true")
                    //Estamos indicando que este username and password pertenecen a el input de los formularios del index.html
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .and()
                    .csrf().disable()
                .logout()
                    //El logout lo puede hacer cualquiera
                    .permitAll()
                    //Si te sales exitosamente te va traer a esta pagina de login
                    .logoutSuccessUrl("/login?logout");
    }
        //Este es el encriptador
        BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        bCryptPasswordEncoder = new BCryptPasswordEncoder(4); //4 es el nivel mas bajo de encriptacion para nuestra aplicacion (el rango es 4 hasta 31)
        return bCryptPasswordEncoder;
    }

    //Aqui estamos utilizando el servicio que creamos anteriormente
    @Autowired
    UserDetailsService userDetailsService;

    //Aqui estamos indicando que la configuracion global va ser posible gracias a ese servicio
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    //Aqui indicamos que ese servicio es el encargado del login y encriptacion del password
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}



