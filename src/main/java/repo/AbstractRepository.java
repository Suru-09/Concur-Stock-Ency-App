package repo;

import entity.BaseEntity;
import exceptions.RepoException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractRepository<ID, T extends BaseEntity<ID>> implements Repository<T, ID> {
    protected Map<ID, T> elems;

    public AbstractRepository()
    {
        elems = new ConcurrentHashMap<>();
    }

    @Override
    public void add(T el) throws RepoException
    {
        if(elems.containsKey(el.getId()))
        {
            throw new RepoException("The element already in the repo.");
        }
        else
        {
           elems.put(el.getId(), el);
        }
    }

    @Override
    public void delete(ID id) throws RepoException
    {
        if(elems.remove(id) == null)
        {
            throw new RepoException("Element not in the repo");
        }
    }

    @Override
    public T get(ID id) throws RepoException
    {
        return elems.get(id);
    }

    @Override
    public void update(T el) throws RepoException
    {
        if (elems.containsKey(el.getId()))
        {
            elems.put(el.getId(), el);
        }
        else
        {
            throw new RepoException("Element could not be updated");
        }
    }

    @Override
    public Collection<T> getAll()
    {
        return elems.values();
    }

}
