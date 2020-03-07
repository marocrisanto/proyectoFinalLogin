package com.prototipo.aplicacion.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Passgenerator {

    //public static void main(String ...args) {
        //BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        //El String que mandamos al metodo encode es el password que queremos encriptar.
        //System.out.println(bCryptPasswordEncoder.encode("1234"));//Esta es la contraseña que vamos a encriptar
        /*
         * Resultado: $2a$04$n6WIRDQlIByVFi.5rtQwEOTAzpzLPzIIG/O6quaxRKY2LlIHG8uty(el resultado deberia ser algo parecido a esto)
         *
         *Se corre y al tener la clave encriptada se copia y vamos a la base de datos a actualizar  todos los passwords
         *Se ejecuta de nuevo la aplicacion pero comentandotodo e ingresamos al login
         */
//    }
}