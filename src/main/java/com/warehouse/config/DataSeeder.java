package com.warehouse.config;

import com.warehouse.model.Item;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.repository.ItemRepository;
import com.warehouse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * Seeds demo accounts (one per role) and a small starting catalog so the API
 * is immediately usable after `mvn spring-boot:run` with no manual setup.
 * Safe to run repeatedly — skips seeding if data already exists.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random(7);

    @Override
    public void run(String... args) {
        seedUsers();
        seedItems();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .username("admin").password(passwordEncoder.encode("admin123"))
                .fullName("Alex Rivera").role(Role.ADMIN).enabled(true).build());

        userRepository.save(User.builder()
                .username("manager").password(passwordEncoder.encode("manager123"))
                .fullName("Jordan Blake").role(Role.MANAGER).enabled(true).build());

        userRepository.save(User.builder()
                .username("staff").password(passwordEncoder.encode("staff123"))
                .fullName("Sam Ortiz").role(Role.STAFF).enabled(true).build());

        System.out.println("Seeded demo accounts -> admin/admin123 (ADMIN), manager/manager123 (MANAGER), staff/staff123 (STAFF)");
    }

    private void seedItems() {
        if (itemRepository.count() > 0) return;

        String[] zones = {"A", "B", "C", "D"};
        List<String[]> catalog = List.of(
                new String[]{"Electronics", "Wireless Mouse"},
                new String[]{"Electronics", "USB Hub"},
                new String[]{"Electronics", "Bluetooth Speaker"},
                new String[]{"Apparel", "Cotton Tee"},
                new String[]{"Apparel", "Rain Shell"},
                new String[]{"Home & Garden", "LED Bulb Pack"},
                new String[]{"Home & Garden", "Tool Set"},
                new String[]{"Sporting Goods", "Yoga Mat"},
                new String[]{"Sporting Goods", "Water Bottle"},
                new String[]{"Automotive", "Floor Mats"},
                new String[]{"Automotive", "Jump Starter"},
                new String[]{"Office Supplies", "Desk Organizer"}
        );

        int id = 1000;
        for (String[] entry : catalog) {
            id += 1;
            int reorderPoint = 15 + random.nextInt(25);
            itemRepository.save(Item.builder()
                    .sku("SKU-" + id)
                    .name(entry[1])
                    .category(entry[0])
                    .stock(random.nextInt(120))
                    .reorderPoint(reorderPoint)
                    .unitCost(BigDecimal.valueOf(5 + random.nextDouble() * 80).setScale(2, java.math.RoundingMode.HALF_UP))
                    .zone(zones[random.nextInt(zones.length)])
                    .aisle(1 + random.nextInt(4))
                    .bin(1 + random.nextInt(6))
                    .build());
        }

        System.out.println("Seeded " + catalog.size() + " starter inventory items");
    }
}
