package com.example.msstore.domain.entities;

import com.example.msstore.domain.enums.TypeCode;
import com.example.msstore.rest.dto.StoreRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String bio;
    private Long ownerId;
    @Enumerated(EnumType.STRING)
    private TypeCode type;

    @OneToMany(mappedBy = "reviewStore", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "foodStore", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<Food> menu = new ArrayList<>();

    public Store(Long id, String name, String bio, Long ownerId, TypeCode type) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.ownerId = ownerId;
        this.type = type;
    }

    public Store(StoreRequestDto requestDto, Long ownerIdStore, TypeCode typeCode){
        name = requestDto.getStoreName();
        bio = requestDto.getBio();
        ownerId = ownerIdStore;
        type = typeCode;
    }

    public double getAverageRating() {
        if (reviews.isEmpty()) {
            return 0.0;
        }

        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        return (double) totalRating / reviews.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store store)) return false;
        return getId().equals(store.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
