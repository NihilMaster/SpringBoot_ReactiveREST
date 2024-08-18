package zzz.reactive.RESTMongoDB.Infrastructure.Handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import zzz.reactive.RESTMongoDB.Infrastructure.Entities.ContactDocument;
import zzz.reactive.RESTMongoDB.Infrastructure.Repositories.ContactRepository;

import java.util.Collections;

@Component
public class ContactHandler {

    @Autowired
    private ContactRepository contactRepository;

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(contactRepository.findAll(), ContactDocument.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(contactRepository.findAllById(Collections.singleton(serverRequest.pathVariable("id"))), ContactDocument.class)
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getFirstByEmail(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .body(contactRepository.findFirstByEmail(serverRequest.pathVariable("email")), ContactDocument.class);
    }

    public Mono<ServerResponse> saveContact(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ContactDocument.class)
                .flatMap(contactDocument -> contactRepository.save(contactDocument))
                .flatMap(savedContact -> ServerResponse.ok().bodyValue(savedContact))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> updateContact(ServerRequest serverRequest) {
        String contactId = serverRequest.pathVariable("id");
        return contactRepository.findById(contactId)
                .flatMap(existingContact -> serverRequest.bodyToMono(ContactDocument.class)
                        .flatMap(contactDocument -> {
                            contactDocument.setId(contactId); // Asegurar que el ID sea el mismo
                            return contactRepository.save(contactDocument);
                        })
                        .flatMap(updatedContact -> ServerResponse.ok().bodyValue(updatedContact))
                )
                .switchIfEmpty(ServerResponse.notFound().build()); // Si no se encuentra el contacto
    }

    public Mono<ServerResponse> deleteContact(ServerRequest serverRequest) {
        return contactRepository
                .deleteById(serverRequest.pathVariable("id"))
                .then(ServerResponse.ok().build());
    }
}
