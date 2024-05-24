package taskmanagement.manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefault() {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(taskManager, "Должен быть возвращен проинициализированный экземпляр TaskManager");
        assertNotNull(historyManager, "Должен быть возвращен проинициализированный экземпляр HistoryManager");
    }
}
