package zzz.reactive.RESTMongoDB.Infrastructure.Repositories;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import zzz.reactive.RESTMongoDB.Infrastructure.Entities.ContactDocument;

import java.util.Collections;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ReactiveMongoOperations mongoOperations;

    @BeforeAll
    public void createContacts() {
        // Crear instancias de ContactDocument
        ContactDocument contact1 = new ContactDocument("12345", "John Doe", "johndoe@example.com", "+1234567890");
        ContactDocument contact2 = new ContactDocument("12346", "Jane Doe", "johndoe@example.com", "+1234567891");
        ContactDocument contact3 = new ContactDocument("12347", "Jack Doe", "jackdoe@example.com", "+1234567892");

        // Guardar los documentos en el repositorio
        StepVerifier.create(contactRepository.save(contact1)).expectNextCount(1).verifyComplete();
        StepVerifier.create(contactRepository.save(contact2)).expectNextCount(1).verifyComplete();
        StepVerifier.create(contactRepository.save(contact3)).expectNextCount(1).verifyComplete();
    }

    @Test
    @Order(1)
    public void getAllTest() {
        contactRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    @Order(2)
    public void getByIdTest() {
        contactRepository.findAllById(Collections.singleton("12345"))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(3)
    public void getFirstByEmailTest() {
        contactRepository.findFirstByEmail("johndoe@example.com")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(4)
    public void saveContactTest() {
        ContactDocument newContact = new ContactDocument("12348", "James Doe", "jamesdoe@example.com", "+1234567894");
        Mono<ContactDocument> contact = contactRepository.save(newContact);
        StepVerifier.create(contact)
                .expectNextMatches(contactDocument ->
                        contactDocument.getEmail().equals(newContact.getEmail()) &&
                                contactDocument.getId().equals(newContact.getId()))
                .verifyComplete();
    }

    @Test
    @Order(5)
    public void updateContactTest() {
        ContactDocument newContact = new ContactDocument("12345", "John Doe", "johndoe@anon.com", "+1234567890");
        StepVerifier.create(contactRepository.findById(newContact.getId())
                        .flatMap(existingContact -> contactRepository.save(newContact)))
                .expectNextMatches(updatedContact ->
                        updatedContact.getEmail().equals(newContact.getEmail()) &&
                                updatedContact.getId().equals(newContact.getId()))
                .verifyComplete();
    }

    @Test
    @Order(6)
    public void deleteContactTest() {
        StepVerifier.create(contactRepository.deleteById("12348")
                        .then(contactRepository.findById("12348")))
                .expectNextCount(0)
                .verifyComplete();
    }

    @AfterAll
    public void dropAll() {
        mongoOperations.dropCollection(ContactDocument.class)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}
