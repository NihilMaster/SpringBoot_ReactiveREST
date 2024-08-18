package zzz.reactive.RESTMongoDB.Infrastructure.Routers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import zzz.reactive.RESTMongoDB.Infrastructure.Handlers.ContactHandler;

@Configuration
public class ContactRouter {

    @Bean
    public RouterFunction<ServerResponse> contactRoutes(ContactHandler contactHandler) {
        return RouterFunctions
                .route()
                .GET("/api/contacts", contactHandler::getAll)
                .GET("/api/contact/{id}", contactHandler::getById)
                .GET("/api/contact/email/{email}", contactHandler::getFirstByEmail)
                .POST("/api/contact", contactHandler::saveContact)
                .PUT("/api/contact/{id}", contactHandler::updateContact)
                .DELETE("/api/contact/{id}", contactHandler::deleteContact)
                .build();
    }

}
