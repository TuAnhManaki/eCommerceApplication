package com.example.demo;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Description of Test Item");
        item.setPrice( new BigDecimal("10"));

        when(itemRepository.findByName("Test Item")).thenReturn(List.of(item));
        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepository.findAll()).thenReturn(List.of(item));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getItemById_invalidId_shouldReturnNotFound() throws Exception {
        when(itemRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/item/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    
    
    @Test
    @WithMockUser(username = "testuser")
    void getItems_shouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/item")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getItemById_validId_shouldReturnItem() throws Exception {
        mockMvc.perform(get("/api/item/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

   

    @Test
    @WithMockUser(username = "testuser")
    void getItemsByName_invalidName_shouldReturnNotFound() throws Exception {
        when(itemRepository.findByName("Invalid Name")).thenReturn(List.of());

        mockMvc.perform(get("/api/item/name/Invalid Name")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "testuser")
    void getItemsByName_validName_shouldReturnItems() throws Exception {
        mockMvc.perform(get("/api/item/name/Test Item")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"));
    }
}
