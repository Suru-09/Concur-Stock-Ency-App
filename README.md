# Concurrency Stock App

-----------------

## **Functional requirements**

------------

•	A user can place a request to sell/buy stocks at a certain price. 

•	A user can see the current price of the stock they’re looking for.

## **Non-functional requirements**

------------


1.	Our service needs to be available 24/7.
2.	It needs to be able to process requests as fast as possible because these influences the prices and difference in prices between price at request time and price at processing/buy time should be as small as possible.
3.	Prices should be updated fast as stocks are bought or sold.
4.	Out of stock or unavailability are possible problems unless a large capacity of stocks are considered (minimum amount set for companies).


## **Concurrency Issues** 

----------------------


```
  Problem: How do we manage having multiple threads?
  Answer: We are using a FixedJavaThreadPool.
```

```
  Problem: How do we simulate a user request?
  Answer: We are using events, therefore in our application a request is interchangeable with an event. 
          Our preferred framework is rabbitMQ.
```

```
  Problem: Multiple threads accessing the same company(resource).
  Answer: We are using a synchronised block on that bit of code.
```

```
  Problem: Exposing companies data to all of our threads.
  Answer: We implemented a custom thread safe repository, which is a singleton called GSonRepository.
```

## **High-level component design** 

-------------------------------

![Architecture](docs/High-level-arch.png)


## **Class diagram**

![ClassDiagram](docs/ArchitectureUML.png)


## TODO

-------------


- [] modify SendRequest so that each thread has only 1 channel 
- [] modify SendRequest so that it has only one connection for one Rabbit Queue.
- [] implement Sell and Buy stocks in ProcessRequest(bzl).
- [] also write back the new content for the companies in its json.
- [x] make ConsumerRequest return on callback true/false for the processed request of the user.
- [x] solve the serialising issues with futures when calling get() instead of isDone().
- [x] add Log4J depdencies and initial configuration.
- [] configure logs so that we have a clear order of processing things,
- [] add Object Pool for both Connections and Channels
- [] modify requestGenerator so that we have ids for each user
- [] create UserRepository and users in a json file
- [] create matching class
- [] create a new event for Client Response once the Request has been processed


