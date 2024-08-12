package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindById() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(1L, response.getBody().getId());
    }

   
    @Test
    public void testCreateUser() {
        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");

        Cart cart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        ResponseEntity<User> response = userController.createUser(request);
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("hashedPassword", response.getBody().getPassword());
    }
    
    @Test
    public void testFindByUserName() {
        // Mock the repository response
        User user = new User();
        user.setUsername("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        // Call the method and assert the result
        ResponseEntity<User> response = userController.findByUserName("testuser");
        assertEquals("testuser", response.getBody().getUsername());
    }

}
