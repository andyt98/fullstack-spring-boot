package com.andy.journey;

import com.andy.customer.Customer;
import com.andy.customer.CustomerRegistrationRequest;
import com.andy.customer.CustomerUpdateRequest;
import com.andy.customer.Gender;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_PATH = "/api/v1/customers";

    @Test
    public void canRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobar123.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        Customer expectedCustomer = new Customer(
                name, email, age, gender
        );

        Assertions.assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    public void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobar123.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // delete customer by id
        webTestClient.delete()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();
        Name fakerName = faker.name();

        String name = fakerName.fullName();
        String email = fakerName.lastName() + "-" + UUID.randomUUID() + "@foobar123.com";
        int age = RANDOM.nextInt(1, 100);
        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age, gender
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer by id
        String newName = "newName";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_PATH + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();


        Customer expected = new Customer(
                id, newName, email, age, gender
        );

        Assertions.assertThat(updatedCustomer).isEqualTo(expected);
    }
}
