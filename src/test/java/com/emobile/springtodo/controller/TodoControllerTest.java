package com.emobile.springtodo.controller;

import com.emobile.springtodo.entity.User;
import com.emobile.springtodo.model.TodoCreateRequest;
import com.emobile.springtodo.model.TodoResponse;
import com.emobile.springtodo.model.TodoUpdateRequest;
import com.emobile.springtodo.repository.UserRepository;
import com.emobile.springtodo.repository.todo.TodoRepository;
import com.emobile.springtodo.service.todo.TodoServiceImpl;
import com.emobile.springtodo.utils.AbstractRestControllerBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoControllerTest extends AbstractRestControllerBaseTest {

    @Autowired
    private TodoServiceImpl todoService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Mock
    private Principal principal;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE todo RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }


    @Test
    void saveTodo_ShouldSaveTodo() {
        createTestUser();

        TodoCreateRequest request = new TodoCreateRequest("New Todo", "Description");

        when(principal.getName()).thenReturn("testUser");

        todoService.saveTodo(request, principal);

        List<TodoResponse> todos = todoRepository.allTodosByUserIdWithPagination(1L,1,1);
        assertEquals(1, todos.size());
        assertEquals("New Todo", todos.get(0).getTitle());

    }

    @Test
    void findTodoById_ShouldReturnTodoFromDatabase() {
        createTestUser();

        when(principal.getName()).thenReturn("testUser");

        TodoCreateRequest todoCreateRequest = new TodoCreateRequest("New Todo", "Description");
        User user = userRepository.findByUsername(principal.getName()).get();
        todoRepository.saveTodo(todoCreateRequest, user.getId());


        Optional<TodoResponse> response = todoService.findTodoById(1L, principal);

        assertTrue(response.isPresent());
        assertEquals("New Todo", response.get().getTitle());
        assertEquals("Description", response.get().getDescription());
    }

    @Test
    void updateTodo_ShouldUpdateTodoInDatabase() {
        createTestUser();

        when(principal.getName()).thenReturn("testUser");

        TodoCreateRequest todoCreateRequest = new TodoCreateRequest("New Todo", "Description");
        User user = userRepository.findByUsername(principal.getName()).get();
        todoRepository.saveTodo(todoCreateRequest, user.getId());

        TodoUpdateRequest request = new TodoUpdateRequest(1L, "Updated Todo", "Updated Description", true);

        TodoResponse response = todoService.updateTodo(request, principal);

        assertEquals("Updated Todo", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertTrue(response.isCompleted());
    }
    @Test
    void getAll_CompletedTodos_ShouldBeEmptySize() {
        createTestUser();
        when(principal.getName()).thenReturn("testUser");

        TodoCreateRequest todoCreateRequest = new TodoCreateRequest("New Todo", "Description");
        User user = userRepository.findByUsername(principal.getName()).get();
        todoRepository.saveTodo(todoCreateRequest, user.getId());

        List<TodoResponse> todoResponses = todoService.allTodosCompletedByPrincipal(principal);

        assertEquals(0, todoResponses.size());

    }

    @Test
    void getAll_CompletedTodos_ShouldHaveOneValue() {
        createTestUser();
        when(principal.getName()).thenReturn("testUser");

        TodoCreateRequest todoCreateRequest = new TodoCreateRequest("New Todo", "Description");
        User user = userRepository.findByUsername(principal.getName()).get();
        todoRepository.saveTodo(todoCreateRequest, user.getId());

        TodoUpdateRequest request = new TodoUpdateRequest(1L, "Updated Todo", "Updated Description", true);

        TodoResponse response = todoService.updateTodo(request, principal);

        List<TodoResponse> todoResponses = todoService.allTodosCompletedByPrincipal(principal);

        assertEquals(1, todoResponses.size());

    }

    @Test
    void findTodoById_ShouldThrowException_WhenUserNotFound() {
        when(principal.getName()).thenReturn("testUser");

        assertThrows(NoSuchElementException.class, () -> todoService.findTodoById(1L, principal));
    }

    @Test
    void saveTodo_ShouldThrowException_WhenUserNotFound() {
        TodoCreateRequest request = new TodoCreateRequest("New Todo", "Description");
        when(principal.getName()).thenReturn("testUser");

        assertThrows(NoSuchElementException.class, () -> todoService.saveTodo(request, principal));
    }

    private void createTestUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setRole("USER");
        userRepository.save(user);
    }

}