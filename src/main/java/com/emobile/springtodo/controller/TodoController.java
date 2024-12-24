package com.emobile.springtodo.controller;

import com.emobile.springtodo.model.TodoCreateRequest;
import com.emobile.springtodo.model.TodoResponse;
import com.emobile.springtodo.model.TodoUpdateRequest;
import com.emobile.springtodo.service.todo.TodoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/todos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TodoController {
    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<Void> createTodo(@Valid @RequestBody TodoCreateRequest request, Principal principal) {
        todoService.saveTodo(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<TodoResponse> updateTodo(
            @Valid @RequestBody TodoUpdateRequest request, Principal principal) {
        TodoResponse updatedTodo = todoService.updateTodo(request, principal);
        return ResponseEntity.ok(updatedTodo);
    }

    @GetMapping("/paged")
    public ResponseEntity<List<TodoResponse>> getAllTodosWithPagination(
            Principal principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<TodoResponse> todos = todoService.allTodosByPrincipalWithPagination(principal, page, size);
        return ResponseEntity.ok(todos);
    }


    @GetMapping("/completed")
    public ResponseEntity<List<TodoResponse>> getAllCompletedTodos(Principal principal) {
        List<TodoResponse> completedTodos = todoService.allTodosCompletedByPrincipal(principal);
        return ResponseEntity.ok(completedTodos);
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<TodoResponse> getTodoById(@PathVariable Long todoId, Principal principal) {
        Optional<TodoResponse> todo = todoService.findTodoById(todoId, principal);
        return todo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
