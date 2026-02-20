package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    @EntityGraph(attributePaths = {"author", "topic"})
    @Override
    @NonNull
    List<Post> findAll();
}
