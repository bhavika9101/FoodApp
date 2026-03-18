package service;

import dao.*;
import model.enums.DeliveryAgentStatus;
import model.user.User;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryAgentServiceTest {


    @Test
    void signUp_withCorrectType_registersAgentSuccessfully() throws SQLException {
        try (MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class,
                 (mock, ctx) -> {
                     when(mock.isUsernameTaken("agent1")).thenReturn(false);
                     when(mock.isPhoneNumberTaken("8888888888")).thenReturn(false);
                     when(mock.insertUser("agent1", "pass123", "8888888888", "DELIVERY_AGENT")).thenReturn(200);
                 });
             MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class,
                 (mock, ctx) -> doNothing().when(mock).insertAgent(200, "UNAVAILABLE", 0.0, 0.0, 0.0));
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            User user = agentService.signUp("delivery_agent", "agent1", "pass123", "8888888888");

            assertNotNull(user, "Agent should be created");
            assertEquals("agent1", user.getUsername());
        }
    }

    @Test
    void signUp_withWrongType_returnsNull() {
        DeliveryAgentService agentService = new DeliveryAgentService();
        User user = agentService.signUp("admin", "someone", "pass", "1111111111");
        assertNull(user, "Should return null for non-delivery_agent type");
    }

    @Test
    void signUp_whenUsernameAlreadyTaken_returnsNull() throws SQLException {
        try (MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class,
                 (mock, ctx) -> when(mock.isUsernameTaken("agent1")).thenReturn(true));
             MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            User user = agentService.signUp("delivery_agent", "agent1", "pass123", "8888888888");

            assertNull(user, "Should return null when username is taken");
        }
    }


    @Test
    void updateAgentStatus_callsDAOWithCorrectParams() throws SQLException {
        try (MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class,
                 (mock, ctx) -> doNothing().when(mock).updateStatus(200, "ON_DELIVERY"));
             MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            agentService.updateAgentStatus(200, DeliveryAgentStatus.ON_DELIVERY);

            DeliveryAgentDAO constructed = agentDaoMock.constructed().get(agentDaoMock.constructed().size() - 1);
            verify(constructed).updateStatus(200, "ON_DELIVERY");
        }
    }

    @Test
    void updateAgentStatus_whenDaoThrows_handlesException() throws SQLException {
        try (MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class,
                 (mock, ctx) -> doThrow(new SQLException("DB down")).when(mock).updateStatus(anyInt(), anyString()));
             MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            assertDoesNotThrow(() -> agentService.updateAgentStatus(200, DeliveryAgentStatus.AVAILABLE));
        }
    }


    @Test
    void payDeliveryAgent_callsUpdateGrossEarning() throws SQLException {
        try (MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class,
                 (mock, ctx) -> doNothing().when(mock).updateGrossEarning(200, 500.0));
             MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            agentService.payDeliveryAgent(200, 500.0);

            DeliveryAgentDAO constructed = agentDaoMock.constructed().get(agentDaoMock.constructed().size() - 1);
            verify(constructed).updateGrossEarning(200, 500.0);
        }
    }

    @Test
    void payDeliveryAgent_whenDaoThrows_handlesException() throws SQLException {
        try (MockedConstruction<DeliveryAgentDAO> agentDaoMock = mockConstruction(DeliveryAgentDAO.class,
                 (mock, ctx) -> doThrow(new SQLException("DB error")).when(mock).updateGrossEarning(anyInt(), anyDouble()));
             MockedConstruction<UserDAO> userDaoMock = mockConstruction(UserDAO.class);
             MockedConstruction<CustomerDAO> customerDaoMock = mockConstruction(CustomerDAO.class);
             MockedConstruction<AdminDAO> adminDaoMock = mockConstruction(AdminDAO.class)) {

            DeliveryAgentService agentService = new DeliveryAgentService();
            assertDoesNotThrow(() -> agentService.payDeliveryAgent(200, 500.0));
        }
    }
}
