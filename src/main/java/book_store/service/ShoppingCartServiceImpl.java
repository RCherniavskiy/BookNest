package book_store.service;

import book_store.dto.shoppingCart.AddToCartRequest;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.dto.cartItemDto.UpdateCartItemRequest;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.mapper.CartItemMapper;
import book_store.mapper.ShoppingCartMapper;
import book_store.model.Book;
import book_store.model.CartItem;
import book_store.model.ShoppingCart;
import book_store.model.User;
import book_store.repository.cartItem.CartItemRepository;
import book_store.repository.shoppingCart.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService{
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final BookMapper bookMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto findUserShoppingCart(User user, Pageable pageable) {
        Page<ShoppingCart> page = shoppingCartRepository.findByUserId(user.getId(), pageable);
        ShoppingCart shoppingCart = page.getContent().get(0);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto addBookToCart(AddToCartRequest request, User user) {
        Book book = bookMapper.bookFromId(request.getBookId());
        ShoppingCart shoppingCart = shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart newShoppingCart = new ShoppingCart();
                    newShoppingCart.setUser(user);
                    return shoppingCartRepository.save(newShoppingCart);
                });
        CartItem existingCartItem = cartItemRepository.findByShoppingCartAndBook(shoppingCart, book);
        if (existingCartItem != null) {
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setShoppingCart(shoppingCart);
            newCartItem.setBook(book);
            newCartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(newCartItem);
            shoppingCart.getCartItems().add(newCartItem);
        }
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto updateCartItemQuantity(Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item with id " + cartItemId + " not found"));
        Book book = cartItem.getBook();
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        ShoppingCart shoppingCart = cartItem.getShoppingCart();
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Override
    public void removeBookFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("CartItem with id " + cartItemId + " not found"));
        cartItemRepository.delete(cartItem);
    }
}
