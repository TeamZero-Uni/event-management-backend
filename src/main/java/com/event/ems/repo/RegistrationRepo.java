package com.event.ems.repo;

import com.event.ems.model.RegitrationStatus;
import com.event.ems.model.RegistrationModel;
import com.event.ems.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepo extends JpaRepository <RegistrationModel,Long> {
	boolean existsByEvent_Id(Long eventId);

	List<RegistrationModel> findByEvent_IdAndStatus(Long eventId, RegitrationStatus status);
    List<RegistrationModel> findByUser(UserModel user);

	void deleteByEvent_Id(Long eventId);

	Boolean  existsByUser_UserIdAndEvent_Id(Long userId, long eventId);
}
