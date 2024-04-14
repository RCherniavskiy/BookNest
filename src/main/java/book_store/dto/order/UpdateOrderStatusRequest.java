package book_store.dto.order;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String status;
}
