package app.factory.service;

import app.factory.model.Batch;
import app.factory.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    @Autowired
    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Batch getBatchById(int id) {
        return batchRepository.findById(id).orElse(null);
    }

    public Batch saveBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    public void deleteBatch(int id) {
        batchRepository.deleteById(id);
    }
}

