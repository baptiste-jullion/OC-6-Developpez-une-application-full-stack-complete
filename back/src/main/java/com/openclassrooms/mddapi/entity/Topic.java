package com.openclassrooms.mddapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Topic extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
