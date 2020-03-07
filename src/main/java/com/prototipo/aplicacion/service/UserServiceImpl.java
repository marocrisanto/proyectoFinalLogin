package com.prototipo.aplicacion.service;


import com.prototipo.aplicacion.Exception.CustomerFieldValidationException;
import com.prototipo.aplicacion.Exception.UsernameOrIdNotFound;
import com.prototipo.aplicacion.dto.ChangePasswordForm;
import com.prototipo.aplicacion.entity.User;
import com.prototipo.aplicacion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Iterable<User> getAllUsers() {

        return repository.findAll();
    }

    private boolean checkUserNameAvailable(User user) throws Exception {
        Optional<User> userFound = repository.findByUsername(user.getUsername());
        if (userFound.isPresent()) {
            throw new CustomerFieldValidationException("Nombre de usuario no disponible", "username");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception {
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()) {
            throw new CustomerFieldValidationException("Confirmar contraseña es obligatorio", "confirmPassword");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new CustomerFieldValidationException("Contraseña y Confirmar contraseña no son iguales", "password");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception {
        if (checkUserNameAvailable(user) && checkPasswordValid(user)) {
            String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);
            user = repository.save(user);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws UsernameOrIdNotFound {
        return repository.findById(id).orElseThrow(() -> new UsernameOrIdNotFound("El ID delusuario no existe"));
    }

    @Override
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);
        return repository.save(toUser);
    }

    protected void mapUser(User from, User to) {
        to.setUsername(from.getUsername());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }

    @Override
    //Aca manejamos esta validacion que si se quiere borrar por metodo rest y que ese usuario logeado tenga el rol de admin, si tiene el rol de admin lo puede hacer.
    //Asi se puede manejar en la web asi como si fueran servicios rest
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUser(Long id) throws UsernameOrIdNotFound {
        User user = getUserById(id);
        repository.delete(user);
    }

    @Override
    public User changePassword(ChangePasswordForm form) throws Exception {
        User user = getUserById(form.getId());
        //Aqui verificamos la contraseña actual(currentPassword)
        if (!isLoggedUserADMIN() && !user.getPassword().equals(form.getCurrentPassword())) {
            throw new Exception("La contraseña actual no es correcta.");
        }

        if (user.getPassword().equals(form.getNewPassword())) {
            throw new Exception("La nueva contraseña debe ser diferente a la contraseña actual.");
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new Exception("La nueva contraseña y la contraseña actual no coinciden.");
        }
        //Una vez que el sistema ya tiene encriptacion, seguridad, vamos a pasar la contraseña en code porque no lo podemos pasar en crudo del formulario
        String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
        user.setPassword(encodePassword);
        return repository.save(user);
    }

    //Agregamos una validacion extra para que al verificar si es admin no sea necesaria la validacion anterior
    //Para hacer eso agregamos un metodo donde el usuario de sesion verifico si tiene el rol de admin o no y me salto esa validacion
    private boolean isLoggedUserADMIN() {
        //Obtener el usuario logeado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggedUser = null;
        Object roles = null;

        //Verificar si el usuario es admin
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;

            roles = loggedUser.getAuthorities().stream()
                    .filter(x -> "ROLE_ADMIN".equals(x.getAuthority())).findFirst()
                    .orElse(null);
        }
        return roles != null ? true : false;
    }

    //Es para obtener el usuario logeado
    public User getLoggedUser() throws Exception {
        //Obtener el usuario logeado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetails loggedUser = null;

        //Verifica si es una instancia de UserDetails(UserDetails es un objeto de springsecurity)
        // es de decir verifica que ese objeto traido de sesion es el usuario
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;
        }

        User myUser = repository
                .findByUsername(loggedUser.getUsername()).orElseThrow(() -> new Exception("Error obteniendo el usuario logeado desde la sesion."));

        return myUser;
    }

}


