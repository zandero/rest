# RestEasy Events
Event triggering for RestEasy

* asynchronous event triggering on REST calls
* triggering of event on specific response codes
* exception based event triggers 
* exception handling and wrapping to JSON response
* supports Guice for dependency injection
 
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
