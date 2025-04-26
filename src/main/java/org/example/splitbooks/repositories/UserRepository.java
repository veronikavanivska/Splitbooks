package org.example.splitbooks.repositories;

import org.example.splitbooks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


   boolean existsByEmail(String email);
   User findByEmail(String email);

}
