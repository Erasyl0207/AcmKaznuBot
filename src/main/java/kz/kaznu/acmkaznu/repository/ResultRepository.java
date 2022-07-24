package kz.kaznu.acmkaznu.repository;

import kz.kaznu.acmkaznu.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ResultRepository extends JpaRepository<Result, Long> {
}
