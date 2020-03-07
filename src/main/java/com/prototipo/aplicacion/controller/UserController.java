package com.prototipo.aplicacion.controller;

import javax.validation.Valid;

import com.prototipo.aplicacion.Exception.CustomerFieldValidationException;
import com.prototipo.aplicacion.Exception.UsernameOrIdNotFound;
import com.prototipo.aplicacion.dto.ChangePasswordForm;
import com.prototipo.aplicacion.entity.Role;
import com.prototipo.aplicacion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.prototipo.aplicacion.repository.RoleRepository;
import com.prototipo.aplicacion.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping({"/","/login"})
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(Model model){
        Role userRole = roleRepository.findByName("USER");
        List<Role> roles = Arrays.asList(userRole);
        model.addAttribute("signup", true);
        model.addAttribute("userForm", new User());
        model.addAttribute("roles", roles);
        return "user-form/user-signup";
    }

    @PostMapping("/signup")
    public String signupAction(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model) {
        Role userRole = roleRepository.findByName("USER");
        List<Role> roles = Arrays.asList(userRole);
        model.addAttribute("userForm", user);
        model.addAttribute("roles",roles);
        model.addAttribute("signup",true);

        if (result.hasErrors()) {
            return "user-form/user-signup";
        } else {
            try {
                userService.createUser(user);
            } catch (CustomerFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
            } catch (Exception e) {
                model.addAttribute("formErrorMessage", e.getMessage());
            }
        }
        return index();
    }

    @GetMapping("/userForm")
    public String userForm(Model model) {
        model.addAttribute("userForm", new User());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("listTab", "active");
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String createUser(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model) {
        if(result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab","active");
        }else {
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab","active");

            }catch (CustomerFieldValidationException cfve) {
                result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab","active");
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("roles",roleRepository.findAll());
            }
            catch (Exception e) {
                model.addAttribute("formErrorMessage",e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab","active");
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("roles",roleRepository.findAll());
            }
        }

        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles",roleRepository.findAll());
        return "user-form/user-view";
    }
    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name="id") Long id) throws Exception {
        User userToEdit = userService.getUserById(id);
        model.addAttribute("userForm", userToEdit);
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("formTab", "active");
        model.addAttribute("editMode", "true");
        model.addAttribute("passwordForm",new ChangePasswordForm(id));

        return "user-form/user-view";
    }

    @PostMapping("/editUser")
    public String postEditUserForm (@Valid @ModelAttribute("userForm") User user, BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
            model.addAttribute("passwordForm",new ChangePasswordForm(user.getId()));
        } else {
                try {
                        userService.updateUser(user);
                        model.addAttribute("userForm", new User());
                        model.addAttribute("listTab", "active");
                } catch (Exception e) {
                        model.addAttribute("formErrorMessage", e.getMessage());
                        model.addAttribute("userForm", user);
                        model.addAttribute("formTab", "active");
                        model.addAttribute("userList", userService.getAllUsers());
                        model.addAttribute("roles", roleRepository.findAll());
                        model.addAttribute("editMode", "true");
                        model.addAttribute("passwordForm",new ChangePasswordForm(user.getId()));
                }
        }
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form/user-view";
    }

    @GetMapping("/userForm/cancel")
    public String cancelEditUser(ModelMap model) {
        return "redirect:/userForm";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name="id")Long id) {
        try {
            userService.deleteUser(id);
        } catch (UsernameOrIdNotFound uoin) {
            model.addAttribute("listErrorMessage",uoin.getMessage());
        }
        return userForm(model);
    }

    @PostMapping("/editUser/changePassword")
    public ResponseEntity postEditUseChangePassword(@Valid @RequestBody ChangePasswordForm form, Errors errors) {
        try {
                if( errors.hasErrors()) {
                //If error, just return a 400 bad request, along with the error message
                String result = errors.getAllErrors()
                        .stream().map(x -> x.getDefaultMessage())
                        .collect(Collectors.joining(""));

                        throw new Exception(result);
                }
                userService.changePassword(form);
        } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Success");
    }
}
