# Events for RestEasy 
Sometime we need to do a little bit more when a REST API is called, but in doing so we slow down the REST API itself.  
Events for RestEasy is a simple library that enables triggering of custom events when a REST API is called.


Event triggering supports:
* asynchronous event execution
* events on specific response codes
* exception based event triggers 
* exception handling and wrapping to JSON response
* Guice for dependency injection

## Setup
```xml
 <dependency>      
      <groupId>com.zandero</groupId>      
      <artifactId>rest.events</artifactId>      
      <version>1.0</version>      
 </dependency>
 ```
 
## Example
Simple event triggered on every call to **/ping**
 
```java
@GET
@Path("/ping")
@RestEvent(processor = PingEvent.class)
@Produces(MediaType.APPLICATION_JSON)
public String ping() {

    return "ping";
}
```

```java
public class PingEvent implements RestEventProcessor {
    @Override
    public RestEventResult execute(Serializable entity, RestEventContext context) throws Exception {
        // magic happens here
        return RestEventResult.ok();
    }
}
```

[Additional info](https://github.com/zandero/rest/wiki/Home)
