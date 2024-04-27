package book_store.controller;

import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import book_store.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/books/add-three-default-books.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get all books")
    void getAll_ReturnsAllBooks_ExpectedSuccess() throws Exception {
        List<BookDto> expected = new ArrayList<>();
        expected.add(new BookDto().setId(1L).setTitle("Kobzar").setAuthor("Shevchenko")
                .setPrice(BigDecimal.valueOf(10.99)).setIsbn("9460303332081"));
        expected.add(new BookDto().setId(2L).setTitle("Avatar").setAuthor("Unknow")
                .setPrice(BigDecimal.valueOf(20.99)).setIsbn("9460306342021"));
        expected.add(new BookDto().setId(3L).setTitle("Terminator").setAuthor("Arnold")
                .setPrice(BigDecimal.valueOf(30.99)).setIsbn("9460301332081"));

        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        for (BookDto book : expected) {
            Assertions.assertTrue(Arrays.stream(actual)
                    .anyMatch(a -> a.getId().equals(book.getId())
                            && a.getTitle().equals(book.getTitle())
                            && a.getAuthor().equals(book.getAuthor())
                            && a.getPrice().equals(book.getPrice())
                            && a.getIsbn().equals(book.getIsbn())));
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ReturnsBookById_ExpectedSuccess() throws Exception {
        BookDto expectedBook = new BookDto()
                .setId(1L)
                .setTitle("Kobzar")
                .setAuthor("Shevchenko")
                .setPrice(BigDecimal.valueOf(10.99))
                .setIsbn("9460303332081");

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBook = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto.class);

        Assertions.assertEquals(expectedBook.getId(), actualBook.getId());
        Assertions.assertEquals(expectedBook.getTitle(), actualBook.getTitle());
        Assertions.assertEquals(expectedBook.getAuthor(), actualBook.getAuthor());
        Assertions.assertEquals(expectedBook.getPrice(), actualBook.getPrice());
        Assertions.assertEquals(expectedBook.getIsbn(), actualBook.getIsbn());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/books/delete-John_Doe-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Create a new book")
    void createBook_CreatesNewBook_ExpectedSuccess() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto()
                .setAuthor("John Doe")
                .setTitle("Book Title")
                .setDescription("Book Description")
                .setIsbn("9460306332081")
                .setPrice(BigDecimal.valueOf(10.99));

        BookDto expected = new BookDto()
                .setAuthor(requestDto.getAuthor())
                .setTitle(requestDto.getTitle())
                .setDescription(requestDto.getDescription())
                .setIsbn(requestDto.getIsbn())
                .setPrice(requestDto.getPrice());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                post("/books")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(result.getResponse().getContentAsString(), BookDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertEquals(expected.getAuthor(), actual.getAuthor());
        EqualsBuilder.reflectionEquals(expected,actual,"id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Delete a book")
    void delete_DeletesBookById_ExpectedSuccess() throws Exception {
        Book book = new Book();
                book.setId(4L);
                book.setAuthor("John Doe");
                book.setTitle("Book Title");
                book.setDescription("Book Description");
                book.setIsbn("9460306332081");
                book.setPrice(BigDecimal.valueOf(10.99));
        mockMvc.perform(
                        delete("/books/{id}", 4L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("Update a book")
    void updateBook_UpdatesBookDetails_ExpectedSuccess() throws Exception {
        CreateBookRequestDto updateRequestDto = new CreateBookRequestDto()
                .setAuthor("New Author")
                .setTitle("Updated Title")
                .setDescription("Updated Description")
                .setIsbn("9460306332081")
                .setPrice(BigDecimal.valueOf(15.99));

        String jsonRequest = objectMapper.writeValueAsString(updateRequestDto);

        mockMvc.perform(
                        put("/books/{id}", 1L)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Search for books")
    void search_ReturnsFilteredBooks_ExpectedSuccess() throws Exception {
        String title = "Kobzar";
        String author = "Shevchenko";

        MvcResult result = mockMvc.perform(
                        get("/books/search")
                                .param("titles", title)
                                .param("authors", author)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] foundBooks = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);

        Assertions.assertEquals(1, foundBooks.length);
        BookDto foundBook = foundBooks[0];
        Assertions.assertEquals(title, foundBook.getTitle());
        Assertions.assertEquals(author, foundBook.getAuthor());
    }

    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM books");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}