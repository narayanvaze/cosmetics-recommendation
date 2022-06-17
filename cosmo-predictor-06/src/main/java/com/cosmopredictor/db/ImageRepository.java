package com.cosmopredictor.db;
import java.util.Optional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cosmopredictor.model.ImageModel;

@ComponentScan(basePackages= {"com.cosmopredictor.model"})
public interface ImageRepository extends JpaRepository<ImageModel, Long> {
	Optional<ImageModel> findByName(String name);
}