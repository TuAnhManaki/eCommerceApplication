package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OrderRepository orderRepository;
    
    @Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

    private User user;
    private String token;


    @BeforeEach
    void setUp() throws Exception {
        user = new User();
        user.setUsername("testuser");
        user.setPassword(bCryptPasswordEncoder.encode("password"));

        when(userRepository.findByUsername("testuser")).thenReturn(user);
     // Simulate obtaining a token for the user
        token = obtainToken("testuser", "password");
    }
    
    private String obtainToken(String username, String password) throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return result;
    }


    @Test
    @WithMockUser(username = "testuser")
    public void getOrderHistoryHistory() throws Exception {
        UserOrder order = new UserOrder();
        order.setId(1L);
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        mockMvc.perform(get("/api/order/history/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(orderRepository, times(1)).findByUser(user);
    }

    @Test
    @WithMockUser(username = "invaliduser")
    public void submitReturnNotFound() throws Exception {
        when(userRepository.findByUsername("invaliduser")).thenReturn(null);

        mockMvc.perform(post("/api/order/submit/invaliduser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderRepository, times(0)).save(any(UserOrder.class));
    }

    @Test
    @WithMockUser(username = "invaliduser")
    public void getOrderHistoryNotFound() throws Exception {
        when(userRepository.findByUsername("invaliduser")).thenReturn(null);

        mockMvc.perform(get("/api/order/history/invaliduser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(orderRepository, times(0)).findByUser(any(User.class));
    }
}
