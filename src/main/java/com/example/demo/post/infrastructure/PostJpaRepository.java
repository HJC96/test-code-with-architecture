package com.example.demo.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findById(long id);

    PostEntity save(PostEntity postEntity);
}