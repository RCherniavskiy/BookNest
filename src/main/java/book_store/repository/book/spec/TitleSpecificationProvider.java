package book_store.repository.book.spec;

import book_store.model.Book;
import book_store.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class TitleSpecificationProvider implements SpecificationProvider<Book> {
    @Override
    public String getKey() {
        return "title";
    }

    @Override
    public Specification<Book> getSpecification(String[] params) {
        //filter by title
        return (root, query, criteriaBuilder) -> root.get("title").in(Arrays.stream(params).toArray());
    }
}
