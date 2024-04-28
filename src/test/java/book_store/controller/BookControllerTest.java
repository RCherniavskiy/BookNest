package book_store.controller;

import book_store.dto.BookDto;
import book_store.dto.CreateBookRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
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
import org.springframework.web.util.NestedServletException;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        Set<Long> categoryIds = new HashSet<>();
        expected.add(new BookDto().setId(1L).setTitle("Kobzar").setAuthor("Shevchenko")
                .setPrice(BigDecimal.valueOf(10.99)).setIsbn("9460303332081").setCategoryIds(categoryIds));
        expected.add(new BookDto().setId(2L).setTitle("Avatar").setAuthor("Unknow")
                .setPrice(BigDecimal.valueOf(20.99)).setIsbn("9460306342021").setCategoryIds(categoryIds));
        expected.add(new BookDto().setId(3L).setTitle("Terminator").setAuthor("Arnold")
                .setPrice(BigDecimal.valueOf(30.99)).setIsbn("9460301332081").setCategoryIds(categoryIds));

        MvcResult result = mockMvc.perform(
                        get("/books")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto[] actual = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto[].class);
        Assertions.assertEquals(3, actual.length);
        for (BookDto book : expected) {
            Assertions.assertTrue(Arrays.asList(actual).containsAll(expected));
        }
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Test
    @DisplayName("Get book by ID")
    void getBookById_ReturnsBookById_ExpectedSuccess() throws Exception {
        Set<Long> categoryIds = new HashSet<>();
        BookDto expectedBook = new BookDto()
                .setId(1L)
                .setTitle("Kobzar")
                .setAuthor("Shevchenko")
                .setPrice(BigDecimal.valueOf(10.99))
                .setIsbn("9460303332081")
                .setCategoryIds(categoryIds);

        MvcResult result = mockMvc.perform(
                        get("/books/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookDto actualBook = objectMapper.readValue(result.getResponse().getContentAsByteArray(), BookDto.class);

        Assertions.assertEquals(expectedBook, actualBook);
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
        EqualsBuilder.reflectionEquals(requestDto, actual, "id");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @Sql(scripts = "classpath:database/books/add-kobzar-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("Delete a book")
    void delete_DeletesBookById_ExpectedSuccess() throws Exception {
        mockMvc.perform(
                        delete("/books/{id}", 4L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(
                            get("/books/{id}", 4L)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(user("user").roles("USER"))
                    )
                    .andExpect(status().isNotFound());
        });
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("New Author"))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.isbn").value("9460306332081"))
                .andExpect(jsonPath("$.price").value(15.99));
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
