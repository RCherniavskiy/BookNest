package book_store.controller;

import book_store.dto.shoppingCart.AddToCartRequest;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.dto.cartItemDto.UpdateCartItemRequest;
import book_store.model.User;
import book_store.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ShoppingCart management", description = "Endpoints for managing shopping carts of users.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/shoppingCarts")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get shoppingCart", description = "Get shoppingCart")
    @GetMapping()
    public ShoppingCartDto getShoppingCart(Authentication authentication, Pageable pageable) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.findUserShoppingCart(user, pageable);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Add book", description = "Add book to the shopping cart")
    @PostMapping
    public ShoppingCartDto addBookToShoppingCart(@RequestBody @Valid AddToCartRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToCart(request, user);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update quantity", description = "Update quantity of a book in the shopping cart")
    @PutMapping("cart-items/{cartItemId}")
    public ShoppingCartDto updateCartItemQuantity(@PathVariable Long cartItemId, @RequestBody @Valid UpdateCartItemRequest request) {
        return shoppingCartService.updateCartItemQuantity(cartItemId, request);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Remove book", description = "Remove a book from the shopping cart")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("cart-items/{cartItemId}")
    public void removeBookFromCart(@PathVariable Long cartItemId) {
        shoppingCartService.removeBookFromCart(cartItemId);
    }
}
