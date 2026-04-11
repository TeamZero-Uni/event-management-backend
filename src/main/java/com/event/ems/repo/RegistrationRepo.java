package com.event.ems.repo;

import com.event.ems.model.RegitrationStatus;
import com.event.ems.model.RegistrationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepo extends JpaRepository <RegistrationModel,Long> {
	boolean existsByEvent_Id(Long eventId);

	List<RegistrationModel> findByEvent_IdAndStatus(Long eventId, RegitrationStatus status);

	void deleteByEvent_Id(Long eventId);
}
