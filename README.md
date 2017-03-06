# RestEasy Events
Event triggering for RestEasy

 * asynchronous event triggering on REST calls
 * exception handling and wrapping in JSON
 * supports Guice for dependency injection
 
 ## Example
 
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
