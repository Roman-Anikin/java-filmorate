package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;

import java.util.List;

public abstract class ModelController<T> {

    public abstract ResponseEntity<T> add(T model);

    public abstract ResponseEntity<T> update(T model);

    public abstract List<T> get();

}
