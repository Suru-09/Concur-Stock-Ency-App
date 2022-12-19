package rabbitMQ;

import java.util.HashSet;
import java.util.Set;

public abstract class ObjectPool<T> {

     private final Set<T> availableResources = new HashSet<>();
     private final Set<T> usedResources = new HashSet<>();

     protected abstract T create();

     public synchronized T acquire()
     {
          if (availableResources.isEmpty())
          {
               availableResources.add(create());
          }

          var instance = availableResources.iterator().next();
          availableResources.remove(instance);
          usedResources.add(instance);
          return instance;
     }

     public synchronized void release(T instance)
     {
          usedResources.remove(instance);
          availableResources.add(instance);
     }

}
