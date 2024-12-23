package com.emobile.springtodo.service.todo;

import com.emobile.springtodo.model.TodoCreateRequest;
import com.emobile.springtodo.model.TodoResponse;
import com.emobile.springtodo.model.TodoUpdateRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface TodoService {
    void saveTodo(TodoCreateRequest request, Principal principal);
    TodoResponse updateTodo(TodoUpdateRequest request, Principal principal);
    List<TodoResponse> allTodosByPrincipal(Principal principal);
    List<TodoResponse> allTodosCompletedByPrincipal(Principal principal);
    Optional<TodoResponse> findTodoById(Long id, Principal principal);
}
