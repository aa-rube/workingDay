package app.factory.service;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    public Item getItemById(int id) {
        return itemRepository.findById(id).orElse(null);
    }
    public void saveItem(Item item) {
        itemRepository.save(item);
    }
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }
    public void deleteItem(String text) {
        int id = Integer.parseInt(text.split("_")[1]);
        deleteItem(id);
    }
    public Item findByName(String s) {
        return itemRepository.findByName(s);
    }

}

