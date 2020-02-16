# Silver Bar Marketplace

Simple problem that captures BUY or SELL order, also allows cancellation of order,
then provides API to show live demand of silver bars.

## Prerequisite
- Install java 11
- Install gradle

## To build 
- ./gradlew build

## Design 
The problem seems to trivial to solve at first sight, but quickly seems like will have issues when ran on scale, so few 
design decision taken to solve scale problems. I have applied CQRS architecture, where OrderService captures order by method 
`registerOrder` and `cancelOrder`, the method just validates and writes the order state to orderStorage, from orderStorage 
the orders are written to a queue which is consumed by LiveOrderBoardProjectionProcessor which updates the LiveOrderBoard state, 
which can be accessed by api `liveOrderBoard` in class LiveOrderBoardService.

The solution is assuming the LiveOrderBoardService might be bit stale (classic CQRS probably).

Also assumed that the library can be used by any JVM language, the interface has been kept JAVA friendly.

## Confession
I have tried to take care of concurrency issues, but concurrency is HARD, and I am sure I have missed cases, also I have not 
tested for concurrency issues, being lazy for writing tests for concurrency as its not easy, I will really appreciate 
when mistakes are pointed out, it always help me learn.

## Few design and implementation issues pending
The designed thoughts that how client will be able to access as library is NOT done, the solution just focused on interfaces 
and implementation to demonstrate the design.
