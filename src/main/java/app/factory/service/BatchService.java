package app.factory.service;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.repository.BatchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class BatchService {
    @Autowired
    private BatchRepository batchRepository;
    private ItemService itemService;

    public Batch getBatchById(int id) {
        return batchRepository.findById(id).orElse(null);
    }

    public void deleteBatch(Integer itemId, Integer batchId) {
        Item item = itemService.getItemById(itemId);

        if (item != null) {
            List<Batch> batches = item.getBatches();
            batches.removeIf(batch -> batch.getId().equals(batchId));
            item.setBatches(batches);
            itemService.saveItem(item);
        }
    }
}