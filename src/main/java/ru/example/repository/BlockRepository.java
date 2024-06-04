package ru.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.entity.Block;
import ru.example.entity.UserEntity;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block,Long> {

    Optional<Block> findByUserName(String userName);
}
