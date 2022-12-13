package repo;

import exceptions.RepoException;

import java.util.Collection;

public interface Repository<T, ID> {
    void add(T el) throws RepoException;
    T get(ID id) throws RepoException;
    void delete(ID id) throws RepoException;
    void update(T el) throws RepoException;
    Collection<T> getAll();
}
