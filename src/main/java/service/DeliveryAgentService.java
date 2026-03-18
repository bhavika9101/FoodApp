package service;

import dao.DeliveryAgentDAO;
import model.enums.DeliveryAgentStatus;
import model.user.DeliveryAgent;
import model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class DeliveryAgentService extends BaseService {
    private final DeliveryAgentDAO deliveryAgentDAO = new DeliveryAgentDAO();

    @Override
    public User signUp(String type, String username, String password, String phone) {
        if (!type.equalsIgnoreCase("delivery_agent")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        return super.signUp(type, username, password, phone);
    }

    public DeliveryAgent findAvailableAgent() {
        try (ResultSet rs = deliveryAgentDAO.getAgentsByStatus("AVAILABLE")) {
            if (rs.next()) {
                return new DeliveryAgent(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("phone_number"),
                        DeliveryAgentStatus.valueOf(rs.getString("agent_status")),
                        rs.getDouble("gross_earning"),
                        rs.getDouble("base_salary"),
                        rs.getDouble("commission_rate"));
            }
        } catch (SQLException e) {
            System.out.println("Error finding available agent: " + e.getMessage());
        }
        return null;
    }

    public void updateAgentStatus(int agentId, DeliveryAgentStatus status) {
        try {
            deliveryAgentDAO.updateStatus(agentId, status.name());
        } catch (SQLException e) {
            System.out.println("Error updating agent status: " + e.getMessage());
        }
    }

    public void payDeliveryAgent(int agentId, double amount) {
        try {
            deliveryAgentDAO.updateGrossEarning(agentId, amount);
            System.out.println("Payment of Rs." + amount + " credited to agent #" + agentId);
        } catch (SQLException e) {
            System.out.println("Error paying agent: " + e.getMessage());
        }
    }

    public double getDeliveryAgentGrossEarning(int agentId) {
        try {
            return deliveryAgentDAO.getGrossEarning(agentId);
        } catch (SQLException e) {
            System.out.println("Error getting earnings: " + e.getMessage());
            return 0.0;
        }
    }

    public void setDeliveryAgentBaseSalary(int agentId, double salary) {
        try {
            deliveryAgentDAO.updateBaseSalary(agentId, salary);
            System.out.println("Base salary set to Rs." + salary + " for agent #" + agentId);
        } catch (SQLException e) {
            System.out.println("Error setting salary: " + e.getMessage());
        }
    }

    public void setDeliveryAgentCommissionRate(int agentId, double rate) {
        try {
            deliveryAgentDAO.updateCommissionRate(agentId, rate);
            System.out.println("Commission rate set to " + (rate * 100) + "% for agent #" + agentId);
        } catch (SQLException e) {
            System.out.println("Error setting commission rate: " + e.getMessage());
        }
    }

    public Map<String, Double> getDeliveryAgentFinancials(int agentId) {
        try {
            return deliveryAgentDAO.getBaseSalaryAndCommission(agentId);
        } catch (SQLException e) {
            System.out.println("Error getting financials: " + e.getMessage());
            return Map.of();
        }
    }

    public int getAgentCount() {
        try {
            return deliveryAgentDAO.getAgentCount();
        } catch (SQLException e) {
            System.out.println("Error getting agent count: " + e.getMessage());
            return 0;
        }
    }

    public DeliveryAgentStatus getAgentStatus(int agentId) {
        try {
            String status = deliveryAgentDAO.getStatus(agentId);
            if (status != null) {
                return DeliveryAgentStatus.valueOf(status);
            }
        } catch (SQLException e) {
            System.out.println("Error getting agent status: " + e.getMessage());
        }
        return DeliveryAgentStatus.UNAVAILABLE;
    }

    public Integer getAgentCurrentOrderId(int agentId) {
        try {
            return deliveryAgentDAO.getCurrentOrderId(agentId);
        } catch (SQLException e) {
            System.out.println("Error getting agent's current order: " + e.getMessage());
            return null;
        }
    }

    public DeliveryAgent getAgentByUsername(String username) {
        try (ResultSet userRs = new dao.UserDAO().getUserByUsername(username)) {
            if (userRs.next()) {
                int userId = userRs.getInt("user_id");
                String userType = userRs.getString("user_type");
                if (!"DELIVERY_AGENT".equals(userType)) {
                    return null;
                }
                try (ResultSet agentRs = deliveryAgentDAO.getAgentById(userId)) {
                    if (agentRs.next()) {
                        return new DeliveryAgent(userId,
                                userRs.getString("username"),
                                userRs.getString("password"),
                                userRs.getString("phone_number"),
                                DeliveryAgentStatus.valueOf(agentRs.getString("agent_status")),
                                agentRs.getDouble("gross_earning"),
                                agentRs.getDouble("base_salary"),
                                agentRs.getDouble("commission_rate"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding agent: " + e.getMessage());
        }
        return null;
    }
}
