package kz.kaznu.acmkaznu.repository;

import kz.kaznu.acmkaznu.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByChatId(String chatId);
}
