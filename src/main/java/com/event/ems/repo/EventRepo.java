package com.event.ems.repo;

import com.event.ems.model.EventModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<EventModel,Long> {

}
