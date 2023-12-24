package app.factory.service;

import app.factory.model.Batch;
import app.factory.model.Item;
import app.factory.repository.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Service
public class BatchService {
    @Autowired
    private BatchRepository batchRepository;

    public Batch getBatchById(int id) {
        return batchRepository.findById(id).orElse(null);
    }

    public void deleteBatch(String text) {
        String batchId = text.split("_")[1];
        String url = "jdbc:mysql://localhost:3306/working_day_report";
        String user = "root";
        String password = "30STMParadise($)_(&)";

        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement()) {
            String sql = "DELETE FROM batch WHERE id = " + batchId;
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}