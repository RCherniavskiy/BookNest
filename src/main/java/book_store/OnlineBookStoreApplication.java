package book_store;

import book_store.model.Book;
import book_store.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
public class OnlineBookStoreApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(OnlineBookStoreApplication.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book java = new Book();
                java.setTitle("Programming language");
                java.setAuthor("Stephen Prata");
                java.setPrice(BigDecimal.valueOf(999));
                java.setDescription("Programming language");
                java.setIsbn("isbn");
                java.setCoverImage("Cover");
                bookService.save(java);
                System.out.println(bookService.findAll());
            }
        };
    }
}
