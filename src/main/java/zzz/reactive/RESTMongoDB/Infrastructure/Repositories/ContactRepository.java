package zzz.reactive.RESTMongoDB.Infrastructure.Repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import zzz.reactive.RESTMongoDB.Infrastructure.Entities.ContactDocument;

public interface ContactRepository extends ReactiveMongoRepository<ContactDocument, String> {

    Mono<ContactDocument> findFirstByEmail(String email);

}
