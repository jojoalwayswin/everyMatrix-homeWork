THE everyMatrix-homeWork 

## Run it 
1. Clone the repo
2. install jdk 24
3. java -jar out/everyMatrix-homework.jar

## THINK OF THIS HOMEWORK
### The Optimization of the original HttpServer architecture

this project requires not use any framework,and we have to use com.sun. net.httpserver.HttpServer.
this service requires the ability to handle a large number of simultaneous requests, so the io blocking feature of com. sun.net. httpserver.HttpServer clearly does not meet the requirements.
We need to optimize the HttpServer architecture. 
* Optimization point one:  use the setExecutor() method to set the thread pool to improve concurrency.
* Optimization point two: in the handle method of the request, submit time-consuming operations to an asynchronous thread pool for execution, and the main thread can be quickly released.
* Optimization point three: The use of the original thread pool still consumes a lot of resources (1000 threads consume 1GB of memory), which cannot meet the huge concurrent requests. Therefore, the new feature of Java 21 version virtual threads is used to create a virtual thread pool (1000 threads only consume a few MB of memory), which easily meets the requirements of millions of concurrent requests.

### The think of the original httpServer structure
The original HttpServer architecture is based on the request path and parameters, which is not flexible enough.
We use a controller class to manage the required request methods and annotations to bind the request path and parameters.
When a request arrives at this service, we will find the corresponding request method in the handler method of the registered HomeworkHttpHandler class based on the request type and path, and then find the path parameters and request parameters based on the path

###  Get session Method
* use a SessionManager class to manage sessions
* concurrentHashMap is used to store session information to ensure thread safety and concurrency performance
* Use a timed thread in the construction method of the SessionManager to execute the method of clearing expired sessions

### Post a customer’s stake on a betting offer
* Use a two-layer concurrent HashMap to store data, with the outermost layer being betofferid and the inner layer containing the customer-id and total number of posted stake
* determine whether the session is legitimate,and only requests with valid session keys will be processed

### Get a high stakes list for a betting offer
* Firstly, check if the bidding offer ID exists in the map
* find the customer and stake map corresponding to the betofferid
* sort them in descending order based on stake
* limited to 20 items
* using the map method to output k=v format
* concatenate the output string with "," 
