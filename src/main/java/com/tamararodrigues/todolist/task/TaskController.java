package com.tamararodrigues.todolist.task;

import com.tamararodrigues.todolist.user.UserModel;
import com.tamararodrigues.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/tasks") //rota base do controller
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping({"", "/"})//operacao especifica para POST
    //ResponseEntity <Object> ->tipo de retorno (pode ser uma TaskModel se tiver certo, ou String em caso de erro)
    //@RequestBody TaskModel taskModel ->o spring pega o JSOn no corpo da request, e ja converte em uma class taskModel
    //HttpServletRequest request -> Dá acesso direto ao objeto da requisição HTTP que atravessou os filtros de segurança, permitindo resgatar o idUser previamente salvo no FilterTaskAuth.
    public ResponseEntity <Object> create( @RequestBody TaskModel taskModel,  @AuthenticationPrincipal UserModel userModel){

        //Esta linha garante que a tarefa fique vinculada unicamente ao utilizador que fez o login
            taskModel.setIdUser(userModel.getId());

            //validacao de a data inicial e menor que do termino
            if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser MENOR que a data do termino"); //400
            }

            //validacao se a hr de inicio/termino esta =/+ da data atual
            var currentDate = LocalDateTime.now();
            if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio/termino deve ser maior ou igual a data atual"); //400
            }

            //salva a tarefa da BD
        var task = this.taskRepository.save(taskModel);
        //Monta a resposta final que o servidor entrega ao usuario, no caso 201(created)
        return ResponseEntity.status(HttpStatus.OK).body(task);


    }
    @GetMapping({"", "/"})
    public ResponseEntity<Object> list( @AuthenticationPrincipal UserModel userModel){

        // Busca diretamente todas as tarefas associadas ao ID do usuário autenticado
        var tasks = taskRepository.findByIdUser(userModel.getId());

        //retorna 200 com uma msg caso nao haja tarefas
        if(tasks.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body("Sem tarefas");
        }

        return ResponseEntity.ok(tasks);//retorna 200 com o corpo da lista

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@AuthenticationPrincipal UserModel userModel, @PathVariable UUID id, @RequestBody TaskModel taskModel){

        // Busca a tarefa no banco de dados pelo ID recebido na URL
        var taskOld = this.taskRepository.findById(id).orElse(null);

        // Se a tarefa não existir, retorna um erro 404
        if(taskOld == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada");
        }
        //se a tarefa nao pertencer ao usuario, nao faz alteracoes
        if(!taskOld.getIdUser().equals(userModel.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não tem permissão para alterar esta tarefa");//400
        }

        //pega o antigo title e substitui pelo novo
        //taskOld.setTitle(taskModel.getTitle());

        //utilizando a classe para fazer somente as auteracoes necessarias
        Utils.copyNonNullProperties(taskModel,taskOld);

        //salva a taskOld ja auterada na BD e coloca em uma variavel
        var taskUpdated = this.taskRepository.save(taskOld);

        //retorno 200
        return ResponseEntity.ok().body(taskUpdated);
    }


}
