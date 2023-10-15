package com.meus_projetos.todolist.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meus_projetos.todolist.model.TaskModel;
import com.meus_projetos.todolist.repository.ITaskRepository;
import com.meus_projetos.todolist.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @GetMapping("/list")
    public ResponseEntity list(HttpServletRequest req) {
        UUID idUser = (UUID) req.getAttribute("idUser");
        // System.out.print("id no controller -> ");
        // System.out.println(idUser);
        List<TaskModel> tasks = this.taskRepository.findByIdUser(idUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tasks); 
    }
    
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel newTask, HttpServletRequest req) {
        newTask.setIdUser((UUID) req.getAttribute("idUser"));
        var task = this.taskRepository.save(newTask);
        
        var currentDateTime = LocalDateTime.now();
        if(currentDateTime.isAfter(newTask.getEndAt()) 
        || newTask.getStartAt().isAfter(newTask.getEndAt())) {
            String msg = "DataHora atual não pode ser depois da data final e DataHora final não pode ser depois da inicial";
            System.out.println(msg);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(msg);
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(task);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@RequestBody TaskModel changedTask, HttpServletRequest req, @PathVariable UUID id) {
        TaskModel oldTask = this.taskRepository.findById(id).orElse(null);
        UUID idUser = (UUID) req.getAttribute("idUser");

        // Regra: Id de task recebido no path deve ser válido
        if (oldTask == null) {
            String msg = "Tarefa com id recebido não encontrada";
            System.out.println(msg);
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(msg);
        }
        
        // Regra: Usuários válidos só podem alterar suas tasks 
        // não pode alterar tasks de outros usuários
        if (!oldTask.getIdUser().equals(idUser)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Usuário não tem permissão para alterar essa tarefa");
        }
        
        // Modifica(atualiza) a task antiga com as novas alterações
        Utils.copyNonNullProperties(changedTask, oldTask);
        TaskModel updatedTask = this.taskRepository.save(oldTask);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedTask);
    }
}
