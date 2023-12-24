package app.bot.service;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

@Service
public class ItemsHandler {
    @Autowired
    private ItemService itemService;
    private final TreeSet<String> data = new TreeSet<>();

    public boolean addNewItem(String text) {
        data.clear();
        try {
            if (text.trim().split("\n").length > 1) {
                data.addAll(Arrays.asList(text.split("\n")));

                for (String itemName : data) {
                    Item item = new Item();
                    item.setName(itemName.trim());
                    itemService.saveItem(item);
                }
                return true;
            }

            Item item = new Item();
            item.setName(text.trim());
            itemService.saveItem(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addNewBatch(String text, int i) {
        data.clear();
        Item item = itemService.getItemById(i);
        List<Batch> batches = item.getBatches();

        try {
            if (text.trim().split("\n").length > 1) {
                data.addAll(Arrays.asList(text.split("\n")));

                for (String batchNumber : data) {
                    Batch batch = new Batch();
                    batch.setItem(item);
                    batch.setBatchNumber(batchNumber.trim());
                    batches.add(batch);
                }
                itemService.saveItem(item);
                return true;
            }

            Batch batch = new Batch();
            batch.setItem(item);
            batch.setBatchNumber(text.trim());
            batches.add(batch);
            itemService.saveItem(item);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
