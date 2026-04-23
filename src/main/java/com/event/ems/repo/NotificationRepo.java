package com.event.ems.repo;

import com.event.ems.model.NotificationModel;
import com.event.ems.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<NotificationModel, Long> {

	// Fetch notifications related to a specific event
	List<NotificationModel> findByEvent_Id(Long eventId);

	// Fetch personal notifications directly sent to a specific user
	List<NotificationModel> findByUserOrderByCreatedAtDesc(UserModel user);

	// Fetches personal notifications AND notifications for registered events (Used in Student Dashboard)
	@Query("SELECT DISTINCT n FROM NotificationModel n " +
			"WHERE (" +
			"   n.event.id IN (SELECT r.event.id FROM RegistrationModel r WHERE r.user = :student) " +
			"   OR n.eventReferenceId IN (SELECT r.event.id FROM RegistrationModel r WHERE r.user = :student) " +
			") " +
			"AND n.user.role = 'ORGANIZER' " +
			"ORDER BY n.createdAt DESC")
	List<NotificationModel> findBroadcastsForStudentEvents(@Param("student") UserModel student);

}