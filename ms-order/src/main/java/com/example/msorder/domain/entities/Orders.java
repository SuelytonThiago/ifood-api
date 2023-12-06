package com.example.msorder.domain.entities;
import com.example.msorder.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Orders implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private Long userId;
    private String storeName;

    @Embedded
    private OrderUserInfo orderUserInfo;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItems> items = new ArrayList<>();

    public Orders(Long id, LocalDate date, Long userId, String storeName, OrderUserInfo orderUserInfo, OrderStatus orderStatus) {
        this.id = id;
        this.date = date;
        this.userId = userId;
        this.storeName = storeName;
        this.orderUserInfo = orderUserInfo;
        this.orderStatus = orderStatus;
    }

    public Orders(OrderUserInfo info, Long userIdInfo, String storeNameInfo){
        date = LocalDate.now();
        userId = userIdInfo;
        storeName = storeNameInfo;
        orderUserInfo = info;
        orderStatus = OrderStatus.WAITING_CONFIRMATION;
    }

    public Double getTotal(){
        double total = 0.0;
        for(OrderItems x : this.items){
            total = total + x.getSubTotal();
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Orders orders)) return false;
        return getId().equals(orders.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
