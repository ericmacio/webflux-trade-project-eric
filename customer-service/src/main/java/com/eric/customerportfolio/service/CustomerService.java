package com.eric.customerportfolio.service;

import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.exceptions.ApplicationExceptions;
import com.eric.customerportfolio.mapper.EntityDtoMapper;
import com.eric.customerportfolio.repository.CustomerRepository;
import com.eric.customerportfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;

    public CustomerService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(this::buildCustomerInformation);
    }

    private Mono<CustomerInformation> buildCustomerInformation(Customer customer) {
        return this.portfolioRepository.findAllByCustomerId(customer.getId())
                .collectList()
                .map(items -> EntityDtoMapper.toCustomerInformation(customer, items));
    }

}
