package com.meus_projetos.todolist.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meus_projetos.todolist.model.TaskModel;

public interface ITaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findByIdUser(UUID idUser);
    // List<TaskModel> findByIdAndIdUser(UUID id, UUID idUser);
    // TaskModel findByTitle(String title);
}
