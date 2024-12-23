package com.emobile.springtodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoUpdateRequest {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
}
