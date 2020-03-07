package com.prototipo.aplicacion.service;
import com.prototipo.aplicacion.Exception.UsernameOrIdNotFound;
import com.prototipo.aplicacion.dto.ChangePasswordForm;
import com.prototipo.aplicacion.entity.User;
import javax.validation.Valid;

public interface UserService {


    public Iterable<User> getAllUsers();

    public User createUser(User user) throws Exception;
   
    public User getUserById(Long id)throws Exception;

    public  User updateUser(User user) throws Exception;

    public void deleteUser(Long id) throws UsernameOrIdNotFound;

    public User changePassword(ChangePasswordForm form) throws Exception;






}

