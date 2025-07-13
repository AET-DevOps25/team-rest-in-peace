package com.rip.notification_service.repository;

import com.rip.notification_service.model.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Integer> {

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificationSetting ns WHERE ns.email = :email")
    void deleteAllByEmail(@Param("email") String email);

    @Query("SELECT distinct ns FROM NotificationSetting ns WHERE ns.type = :type")
    List<NotificationSetting> findAllByType(@Param("type") String type);
}