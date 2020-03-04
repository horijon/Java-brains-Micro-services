// Go to https://www.themoviedb.org/settings/api and get my api-key, and replace in the properties file
Note: http://localhost:8761/ // for eureka dashboard
Note: http://localhost:8081/catalog/1 // for inter-service communication flow
Note: http://localhost:8081/hystrix // for hystrix dashboard, then put the url into the hystrix dashboard input field of the "Single Hystrix App" by replacing hystrix-app:port with localhost:8081 and replacing http to https
i.e. at the time when i am documenting this, put the url -> "http://localhost:8081/actuator/hystrix.stream"

-> Netflix's Eureka is used for the service discovery and stuffs related to it.
-> Hystrix is used for the circuit breaker.

Resilience -> If one instance/node gets down/slow, the other instance/node are present to handle
Fault Tolerance -> If the instance/node  is made stronger, so that there is minimal chance of
going down.
-> Circuit Breaker is used for fault tolerance.

-> MovieCatalogService calls 2 services (using RestTemplate and WebClient); inter-service communication.
-> Best suitable for a micro-service that calls more than 1 micro-services, so added on the MovieCatalogService.

-> When a micro-service becomes slow, it makes other downstream and sibling(it's downstream as well) micro-services slow as well as the threads of the server are occupied.
And even when we increase the number of threads in the server(tomcat container), it eventually becomes slow as the user when experiences slow page, may refresh the page
time and again. To mitigate this, we can set timeout on the requests, but even then this will only have a temporary fix. When the user refreshes the page or multiple
requests come then the threads may be occupied during the timeout time as well, this will again make other sibling and its downstream micro-services slow.
To mitigate this, we can go with a circuit breaker design pattern that detects slow services and decides the best possible to wait for some time again
then check if it is recovered and if not then not send any further requests to that service.

With eureka's presence, the url will not act as a url, just a service discovery, when using RestTemplate.

// calling localhost way ratingsdata
// UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/user/" + userId, UserRating.class);


// using WebClient we can call the rest api this way inside CatalogResource.java
/* using webClient
            Movie movie = webClientBuilder.build()
                    .get() // get request
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve() // fetch data
                    .bodyToMono(Movie.class) // after getting the data unmarshall(convert) it to Movie object, mono is like a future
                    .block(); // block the execution (make it synchronous), it is by default asynchronous
*/

A circuit breaker when trips(don't send further) the requests to the downstream micro-service, then it needs to fallback the request.
Fallback options:
1) Throw an error. (not recommended)
2) Return a fallback "default" response. (recommended)
3) Save previous responses (cache) and use that when possible. (bonus)
    This will send the response to the user from the cache may not be valid but is sensible.

Why circuit breakers?
1) Failing Fast (its better to fail fast than taking time and fail).
2) Fallback Functionality.
3) Automatic Recovery. ("when the sleep time after circuit breaks to start checking for recovery",
    is over then if it is recovered then requests are send further otherwise the same process).
Note: To achieve all this, there is a framework called "Hystrix".

-> @HystrixCommand breaks the circuit when something goes down and calls fallbackMethod when circuit breaks
-> Wherever @HystrixCommand is present in the request mapping method, the Spring Boot wraps the API class (containing that annotated method)
and returns a proxy class (where the circuit breaker logic is kept).
-> In this project, whenever the movie info service gets down, it does the fallback for the ratings service as well, but we can improve this by making different fallback for different rest calls
so that the fallback is achieved only for the slow/defected micro-service and the preceding rest calls to other micro-services works fine (but the following rest calls will anyhow falls back).
So, we refactored the code to call a separate method inside the same class and annotated each method with @HystrixCommand, also created fallback methods, but this doesn't work as expected because
the circuit breaker pattern is present in the wrapper class i.e. the proxy class, and when the annotation is bind to the request-mapping method, the proxy wraps the actual class and can intercept
it, but the methods that are inside the API (actual) class cannot be intercepted because the API class cannot intercept rather the only proxy(wrapper class) can intercept.
To achieve this we make a separate classes for intercepting each class and autowire it inside it so that those classes can be autowired (wrapped/act as a proxy) to have circuit breaker functionality.

The parameters for the @HystrixCommand to choose depends on the project and also it varies from time to time. So, we cannot determine the best/exact parameter values for all the project life,
even if we manage to make it perfect for a specific time (according to the requests it's effect changes so...).

For the Hystrix, we use the dependency of the hystrix and for the dashboard we use actuator and hystrix-dashboard dependencies.
The Hystrix has discontinued for further feature development and only working on maintainance, and they are working on a better alternative - adaptive fault tolerance,
which will be dynamic with respect to the configuration parameters.

Note: The way to handle outages all together when we actually have to do the implementation are:-
1) increase the number of instances(nodes) of the micro-services -> naive way.
2) have intelligent circuit breaker.
3) the bulk head pattern. (term comes from the ship-building workshop, bulkhead -> a dividing wall or barrier between separate compartments inside a ship, so that whenever if a hole happens
in the ship then only a compartment is affected so that whole ship will not be affected and be saved). This is achieved by using separate thread-pools for separate @HystrixCommand

Level 1 -> Service discovery.
Level 2 -> Fault tolerance and Resilience.
Level 3 -> Micro-service configuration.
Level 4 -> Microservices deployment Like Docker, Kubernetes...

Notes on properties file:
-> When we add another properties file of the same name on the folder where the jar resides, it overrides the file, that
was made during development, this is how the parameters can be given according to the environment on runtime.
eg; add application.properties on the path where jar exists i.e. in this case inside target folder and then run
java -jar pathToTarget/jarName.jar
-> Also, we can override the parameter values from the command line arguments.
eg; java -jar pathToTarget/jarName.jar paramterName=parameterValue
Note: The jar need not be extracted from the jar to classes and then change the file and then again jar it.
-> Note: But directly we can override by either another file of the same name on the same path of the jar or by command line
arguments.

Spring profiles (maven) :-
-> application-profileName.yml or application-profileName.properties
-> command line argument eg; "java -jar springProject.snapshot.version.jar --spring.profiles.active=test"
-> Beans can also be environment variables specific by using eg; @Profile("production") annotation on the bean method (not recommended to use)
@Values are injected into where we need in the framework/codebase, but to access the properties we need to go with the environment object in spring boot.