package book_store.service;

import book_store.dto.cartItemDto.UpdateCartItemRequest;
import book_store.dto.shoppingCart.AddToCartRequest;
import book_store.dto.shoppingCart.ShoppingCartDto;
import book_store.exception.EntityNotFoundException;
import book_store.mapper.BookMapper;
import book_store.mapper.ShoppingCartMapper;
import book_store.model.Book;
import book_store.model.CartItem;
import book_store.model.ShoppingCart;
import book_store.model.User;
import book_store.repository.cartItem.CartItemRepository;
import book_store.repository.shoppingCart.ShoppingCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private BookMapper bookMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test findUserShoppingCart: Success - User Exists, ShoppingCart Returned")
    void testFindUserShoppingCart_Success_UserExists_ShoppingCartReturned() {
        User user = new User();
        user.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        Pageable pageable = Pageable.unpaged();
        Page<ShoppingCart> page = mock(Page.class);
        when(page.getContent()).thenReturn(List.of(shoppingCart));
        when(shoppingCartRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        ShoppingCartDto expectedDto = new ShoppingCartDto();
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expectedDto);
        ShoppingCartDto resultDto = shoppingCartService.findUserShoppingCart(user, pageable);
        assertEquals(expectedDto, resultDto);
    }

    @Test
    @DisplayName("Test addBookToCart: Success - Book Added To Cart, CartItem Quantity Updated")
    void testAddBookToCart_Success_BookAddedToCart_CartItemQuantityUpdated() {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(1L);
        request.setQuantity(1);
        User user = new User();
        user.setId(1L);
        Book book = new Book();
        book.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(1L);
        existingCartItem.setBook(book);
        existingCartItem.setQuantity(1);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        when(bookMapper.bookFromId(1L)).thenReturn(book);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByShoppingCartAndBook(shoppingCart, book)).thenReturn(existingCartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartDto result = shoppingCartService.addBookToCart(request, user);
        assertEquals(shoppingCartDto, result);
        assertEquals(2, existingCartItem.getQuantity());
    }

    @Test
    @DisplayName("Test updateCartItemQuantity: Success")
    public void testUpdateCartItemQuantity_Success() {
        long cartItemId = 1L;
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(5);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setShoppingCart(shoppingCart);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartDto result = shoppingCartService.updateCartItemQuantity(cartItemId, request);
        assertEquals(shoppingCartDto, result);
    }

    @Test
    @DisplayName("Test updateCartItemQuantity: Not Found")
    public void testUpdateCartItemQuantity_NotFound() {
        Long cartItemId = 1L;
        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.setQuantity(2);
        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.updateCartItemQuantity(cartItemId, request));
    }

    @Test
    @DisplayName("Test removeBookFromCart: Success")
    void testRemoveBookFromCart_Success() {
        long cartItemId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.of(cartItem));
        shoppingCartService.removeBookFromCart(cartItemId);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    @DisplayName("Test removeBookFromCart: CartItem Not Found")
    void testRemoveBookFromCart_CartItemNotFound() {
        long cartItemId = 1L;
        when(cartItemRepository.findById(cartItemId)).thenReturn(java.util.Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> shoppingCartService.removeBookFromCart(cartItemId));
    }

    @Test
    @DisplayName("Test addBookToCart: New ShoppingCart")
    void testAddBookToCart_NewShoppingCart() {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(1L);
        request.setQuantity(1);
        User user = new User();
        user.setId(1L);
        Book book = new Book();
        book.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        when(bookMapper.bookFromId(1L)).thenReturn(book);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenAnswer(invocation -> {
            ShoppingCart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);
            return savedCart;
        });
        when(shoppingCartMapper.toDto(any(ShoppingCart.class))).thenReturn(shoppingCartDto);
        ShoppingCartDto result = shoppingCartService.addBookToCart(request, user);
        assertEquals(shoppingCartDto, result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(shoppingCartRepository, times(1)).save(any(ShoppingCart.class));
    }

    @Test
    @DisplayName("Test addBookToCart: Existing ShoppingCart")
    void testAddBookToCart_ExistingShoppingCart() {
        AddToCartRequest request = new AddToCartRequest();
        request.setBookId(1L);
        request.setQuantity(1);
        User user = new User();
        user.setId(1L);
        Book book = new Book();
        book.setId(1L);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        shoppingCart.setUser(user);
        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(1L);
        existingCartItem.setBook(book);
        existingCartItem.setQuantity(1);
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(1L);
        when(bookMapper.bookFromId(1L)).thenReturn(book);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByShoppingCartAndBook(shoppingCart, book)).thenReturn(existingCartItem);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(shoppingCartDto);
        ShoppingCartDto result = shoppingCartService.addBookToCart(request, user);
        assertEquals(shoppingCartDto, result);
        assertEquals(2, existingCartItem.getQuantity());
    }
}
