package orcha.lang.configuration

import org.springframework.beans.factory.annotation.Autowired

class MessagingMiddlewareAdapter (
        val adapter: Adapter = Adapter.MessagingMiddleware,
 val connection: MiddlewareConnection = MiddlewareConnection(),
 val middlewareConfig: MiddlewareConfig = MiddlewareConfig()) : ConfigurableProperties(){

    }