package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDtoNameDescription;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(
        properties = "spring.datasource.username=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void saveNewItem() {
        UserDto userDto = new UserDto(1L, "User", "user@mail.ru");
        ItemDtoNameDescription itemDtoNameDescription = new ItemDtoNameDescription("item", "cool item", true, null);

        UserDto user = userService.saveNewUser(userDto);
        itemService.saveNewItem(itemDtoNameDescription, user.getId());

        TypedQuery<Item> queryItem = em.createQuery("Select i from Item i where i.name like :item", Item.class);
        Item item = queryItem.setParameter("item", itemDtoNameDescription.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoNameDescription.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoNameDescription.getDescription()));
    }
}
