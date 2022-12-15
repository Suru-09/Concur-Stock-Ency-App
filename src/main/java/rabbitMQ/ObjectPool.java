package rabbitMQ;

import java.util.Set;

public abstract class ObjectPool<T> {

     private Set<T> availableResources;
     private Set<T> usedResources;

}
