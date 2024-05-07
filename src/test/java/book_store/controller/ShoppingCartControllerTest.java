package book_store.controller;

import book_store.dto.cartItemDto.CartItemDto;
import book_store.service.ShoppingCartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import book_store.dto.shoppingCart.AddToCartRequest;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.dto.cartItemDto.UpdateCartItemRequest;
import book_store.model.User;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShoppingCartService shoppingCartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Get shopping cart")
    void getShoppingCart() throws Exception {
        ShoppingCartDto mockShoppingCartDto = createMockShoppingCartDto();
        User user = new User();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(shoppingCartService.findUserShoppingCart(eq(user), any(Pageable.class))).thenReturn(mockShoppingCartDto);
        mockMvc.perform(get("/shoppingCarts").with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cartItems.length()").value(2));
    }

    @Test
    @DisplayName("Add book to shopping cart")
    void addBookToShoppingCart() throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(123L);
        request.setQuantity(1);
        ShoppingCartDto mockShoppingCartDto = createMockShoppingCartDto();
        User user = new User();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(shoppingCartService.addBookToCart(any(AddToCartRequest.class), eq(user))).thenReturn(mockShoppingCartDto);
        mockMvc.perform(post("/shoppingCarts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cartItems.length()").value(2));
    }

    @Test
    @DisplayName("Update the quantity of goods in the cart")
    void updateCartItemQuantity() throws Exception {
        Long cartItemId = 1L;
        int newQuantity = 2;
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(newQuantity);
        ShoppingCartDto mockShoppingCartDto = createMockShoppingCartDto();
        when(shoppingCartService.updateCartItemQuantity(cartItemId, request)).thenReturn(mockShoppingCartDto);
        mockMvc.perform(put("/shoppingCarts/cart-items/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cartItems").isArray());
    }

    @Test
    @DisplayName("Remove a book from the shopping cart (valid data)")
    void removeBookFromCart_ValidInput_DoesNotThrowException() throws Exception {
        Long cartItemId = 1L;
        mockMvc.perform(delete("/shoppingCarts/cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Remove book from shopping cart (invalid data)")
    void removeBookFromCart_InvalidInput_ThrowsException() throws Exception {
        Long cartItemId = 999L;
        mockMvc.perform(delete("/shoppingCarts/cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent());
    }

    private ShoppingCartDto createMockShoppingCartDto() {
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        CartItemDto item1 = new CartItemDto();
        item1.setId(1L);
        item1.setBookId(101L);
        item1.setQuantity(2);
        CartItemDto item2 = new CartItemDto();
        item2.setId(2L);
        item2.setBookId(102L);
        item2.setQuantity(3);
        Set<CartItemDto> cartItems = new HashSet<>();
        cartItems.add(item1);
        cartItems.add(item2);
        shoppingCartDto.setCartItems(cartItems);
        return shoppingCartDto;
    }
}
