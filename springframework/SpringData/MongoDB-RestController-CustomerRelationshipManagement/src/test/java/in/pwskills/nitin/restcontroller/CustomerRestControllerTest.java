package in.pwskills.nitin.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.pwskills.nitin.document.Customer;
import in.pwskills.nitin.service.ICustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerRestController.class)
public class CustomerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = new Customer("1", "John Doe", "123 Street", 1000.0, "1234567890");
        Mockito.when(customerService.registerCustomer(Mockito.any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.cname").value("John Doe"));
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(
                new Customer("1", "John Doe", "123 Street", 1000.0, "1234567890"),
                new Customer("2", "Jane Doe", "456 Avenue", 2000.0, "0987654321")
        );
        Mockito.when(customerService.findAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].cname").value("John Doe"))
                .andExpect(jsonPath("$[1].cname").value("Jane Doe"));
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Customer customer = new Customer("1", "John Doe", "123 Street", 1000.0, "1234567890");
        Mockito.when(customerService.findCustomerById("1")).thenReturn(customer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.cname").value("John Doe"));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        Customer updatedCustomer = new Customer("1", "John Smith", "123 Street", 1500.0, "1234567890");
        Mockito.when(customerService.updateCustomer(Mockito.eq("1"), Mockito.any(Customer.class))).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cname").value("John Smith"))
                .andExpect(jsonPath("$.billAmount").value(1500.0));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        Mockito.doNothing().when(customerService).removeCustomer("1");

        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }
}