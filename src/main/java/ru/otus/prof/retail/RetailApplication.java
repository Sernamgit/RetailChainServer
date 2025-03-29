package ru.otus.prof.retail;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.prof.retail.controllers.shops.ShopController;
import ru.otus.prof.retail.mappers.shop.ShopMapper;
import ru.otus.prof.retail.repositories.shops.ShopRepository;


@SpringBootApplication
public class RetailApplication implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(RetailApplication.class);
    private ShopRepository shopRepository;
    private ShopMapper shopMapper;
    private ShopController shopController;

    public RetailApplication(ShopRepository shopRepository, ShopMapper shopMapper, ShopController shopController) {
        this.shopRepository = shopRepository;
        this.shopMapper = shopMapper;
        this.shopController = shopController;
    }

    public static void main(String[] args) {
        SpringApplication.run(RetailApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {

    }
}
