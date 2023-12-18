package app.bot.enviroment;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemsHandler {
    @Autowired
    private ItemService itemService;
    public boolean addNewItem(String text) {
        try {
            Item item = new Item();
            item.setName(text.trim());
            itemService.saveItem(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addNewBatch(String text, int i) {
        try {
            Item item = itemService.getItemById(i);
            Batch batch = new Batch();
            batch.setItem(item);
            batch.setBatchNumber(text.trim());
            item.getBatches().add(batch);
            itemService.saveItem(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
