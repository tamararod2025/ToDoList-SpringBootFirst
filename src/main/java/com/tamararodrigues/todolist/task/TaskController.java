package com.tamararodrigues.todolist.task;

import com.tamararodrigues.todolist.user.IUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tasks") //rota base do controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;


    @PostMapping({"", "/"})//operacao especifica para POST
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){


            var idUser = (UUID) request.getAttribute("idUser");

            taskModel.setIdUser(idUser);

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);


    }
}
