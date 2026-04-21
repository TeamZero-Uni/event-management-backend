package com.event.ems.repo;

import com.event.ems.model.NotificationModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationModel, Long> {

	List<NotificationModel> findByEvent_Id(Long eventId);
}
