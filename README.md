# Silver Bar Marketplace

Simple problem that captures BUY or SELL order, also allows cancellation of order,
then provides API to show live demand of silver bars.

## Prerequisite
- Install java 11
- Install gradle

## To build 
- ./gradlew build

## Design 
The problem seems to trivial to solve at first sight, but when running on scale will require different solution like every other problem, so few 
design decision taken to solve scale problems. I have applied CQRS architecture, where OrderService captures order by method 
`registerOrder` and `cancelOrder`, the method just validates and writes the order state to orderStorage, from orderStorage 
the orders are written to a queue which is consumed by LiveOrderBoardStorage `update` method which updates the LiveOrderBoard state, 
which can be accessed by api `liveOrderBoard` in class OrderService.

The solution is assuming the LiveOrderBoardService might be bit stale (my definition of classic CQRS).

Also assumed that the library can be used by any JVM language, the interface has been kept JAVA friendly.

Assumed that client is trusted.

Assumed the client can be used in concurrent environment.

Thought about the client can be deployed on multiple nodes, should generally work given the storage and queue are not in-memory :)


## Confession
I have tried to take care of concurrency issues, but concurrency is HARD, and I am sure I have missed cases, also I have not 
tested for concurrency issues, being lazy for writing tests for concurrency as its not easy, I will really appreciate 
when mistakes are pointed out, it always help me learn.

## Few implementation issues pending
Ideally i could have done multi module project and demonstrated tests as end to end test that it will work as library,
though OrderServiceTest is making use of apis from OrderService to test the scenarios.
