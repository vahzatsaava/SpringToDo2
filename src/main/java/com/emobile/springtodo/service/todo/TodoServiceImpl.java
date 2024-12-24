package com.emobile.springtodo.service.todo;

import com.emobile.springtodo.entity.User;
import com.emobile.springtodo.model.TodoCreateRequest;
import com.emobile.springtodo.model.TodoResponse;
import com.emobile.springtodo.model.TodoUpdateRequest;
import com.emobile.springtodo.repository.UserRepository;
import com.emobile.springtodo.repository.todo.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    @Override
    @CacheEvict(value = {"todos", "allTodos", "completedTodos"}, allEntries = true)
    public void saveTodo(TodoCreateRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        todoRepository.saveTodo(request, user.getId());
    }

    @Override
    @CacheEvict(value = {"todos", "allTodos", "completedTodos"}, allEntries = true)
    public TodoResponse updateTodo(TodoUpdateRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return todoRepository.updateTodo(request, user.getId());
    }

    @Override
    public List<TodoResponse> allTodosByPrincipalWithPagination(Principal principal, int page, int size) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return todoRepository.allTodosByUserIdWithPagination(user.getId(), page, size);
    }


    @Override
    public List<TodoResponse> allTodosCompletedByPrincipal(Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return todoRepository.allTodosCompletedByUserId(user.getId());
    }

    @Override
    public Optional<TodoResponse> findTodoById(Long id, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        return todoRepository.findTodoById(id, user.getId());
    }
}
