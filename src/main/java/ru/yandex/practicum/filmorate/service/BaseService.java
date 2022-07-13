package ru.yandex.practicum.filmorate.service;

import java.util.List;

public abstract class BaseService<T> {

    abstract void add(Long firstId, Long secondId);

    abstract void remove(Long firstId, Long secondId);

    abstract T getById(Long id);

    abstract T add(T t);

    abstract T update(T t);

    abstract List<T> get();

}
