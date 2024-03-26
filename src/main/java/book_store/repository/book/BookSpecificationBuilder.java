package book_store.repository.book;

import book_store.dto.book.BookSearchParameters;
import book_store.model.Book;
import book_store.repository.SpecificationBuilder;
import book_store.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;
    private static final String SEARCH_BY_TITLE = "title";
    private static final String SEARCH_BY_AUTHOR = "author";

    @Override
    public Specification<Book> build(BookSearchParameters searchParameters) {
        Specification<Book> spec = Specification.where(null);
        if (searchParameters.titles() != null && searchParameters.titles().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(SEARCH_BY_TITLE)
                    .getSpecification(searchParameters.titles()));
        }
        if (searchParameters.authors() != null && searchParameters.authors().length > 0) {
            spec = spec.and(bookSpecificationProviderManager.getSpecificationProvider(SEARCH_BY_AUTHOR )
                    .getSpecification(searchParameters.authors()));
        }
        return spec;
    }
}
