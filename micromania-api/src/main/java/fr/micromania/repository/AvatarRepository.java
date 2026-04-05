package fr.micromania.repository;

import fr.micromania.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    List<Avatar> findByActifTrueOrderByIdAsc();
    Optional<Avatar> findByIdAndActifTrue(Long id);
    Optional<Avatar> findFirstByActifTrueOrderByIdAsc();
}