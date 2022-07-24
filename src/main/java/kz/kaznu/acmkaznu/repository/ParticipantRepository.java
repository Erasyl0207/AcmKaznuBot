package kz.kaznu.acmkaznu.repository;

import kz.kaznu.acmkaznu.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
}
