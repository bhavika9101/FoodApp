# 🍕 FoodDeliveryApp
---

## Full Class Diagram

```mermaid
classDiagram
    direction TB

    %% ════════════════════════════════════════════
    %% PACKAGE: model.enums
    %% ════════════════════════════════════════════

    class DeliveryAgentStatus {
        <<enumeration>>
        AVAILABLE
        ON_DELIVERY
        UNAVAILABLE
        -String displayName
        +getDisplayName() String
    }

    class OrderStatus {
        <<enumeration>>
        PLACED
        APPROVED
        READY_FOR_DELIVERY
        OUT_FOR_DELIVERY
        DELIVERED
        -String displayName
        +getDisplayName() String
    }

    class PaymentMode {
        <<enumeration>>
        CASH
        UPI
        -String displayName
        +getDisplayName() String
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: model.user
    %% ════════════════════════════════════════════

    class User {
        <<interface>>
        +getUserId() Integer
        +getUsername() String
        +getPassword() String
        +getPhoneNumber() String
        +setUsername(String username) void
        +setPassword(String password) void
        +setPhoneNumber(String phoneNumber) void
    }

    class TriFunction~U, P, N, R~ {
        <<interface>>
        +apply(U u, P p, N n) R
    }

    class BaseUser {
        <<abstract>>
        -IdGenerator idGenerator$
        -Integer userId
        -String username
        -String password
        -String phoneNumber
        +BaseUser()
        +BaseUser(String, String, String)
        +getUsername() String
        +getPassword() String
        +getUserId() Integer
        +getPhoneNumber() String
        +setUsername(String username) void
        +setPassword(String password) void
        +setPhoneNumber(String phoneNumber) void
        +equals(Object obj) boolean
        +hashCode() int
    }

    class Admin {
        -Admin instance$
        -Admin(String, String, String)
        +getInstance(String, String, String)$ Admin
    }

    class Customer {
        -String address
        +Customer()
        +Customer(String, String, String)
        +getAddress() String
        +setAddress(String address) void
    }

    class DeliveryAgent {
        -DeliveryAgentStatus status
        -Integer currentOrderId
        -Double grossEarning
        -Double baseSalary
        -Double commissionRate
        +DeliveryAgent()
        +DeliveryAgent(String, String, String)
        +getStatus() DeliveryAgentStatus
        +setStatus(DeliveryAgentStatus status) void
        +getCurrentOrderId() Integer
        +setCurrentOrderId(Integer) void
        +isAvailable() Boolean
        +incrementGrossEarning(Double amount) void
        +getGrossEarning() Double
        +setBaseSalary(Double baseSalary) void
        +setCommissionRate(Double commissionRate) void
        +getBaseSalary() Double
        +getCommissionRate() Double
    }

    class UserFactory {
        -Map~String, TriFunction~ registry$
        +createUser(String, String, String, String)$ User
        +registerNewUserType(String, TriFunction)$ void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: model.order
    %% ════════════════════════════════════════════

    class MenuComponent {
        <<interface>>
        +getId() Integer
        +getName() String
        +getPrice() Double
        +isComponent() Boolean
        +getComponentSet() Set~MenuComponent~
        +print() void
        +add(MenuComponent) boolean
    }

    class MenuItem {
        -IdGenerator idGenerator$
        -Integer itemId
        -String itemName
        -Double itemPrice
        +MenuItem()
        +MenuItem(String, Double)
        +getId() Integer
        +getName() String
        +getPrice() Double
        +isComponent() Boolean
        +getComponentSet() Set~MenuComponent~
        +print() void
        +add(MenuComponent) boolean
        +equals(Object obj) boolean
        +hashCode() int
        +toString() String
    }

    class MenuCategory {
        -IdGenerator idGenerator$
        -Integer categoryId
        -String categoryName
        -Set~MenuComponent~ componentSet
        +MenuCategory()
        +MenuCategory(String)
        +getId() Integer
        +getName() String
        +getPrice() Double
        +isComponent() Boolean
        +getComponentSet() Set~MenuComponent~
        +print() void
        +add(MenuComponent) boolean
        +equals(Object obj) boolean
        +hashCode() int
    }

    class Cart {
        -IdGenerator idGenerator$
        -Integer cartId
        -Map~MenuItem, Integer~ cartItemMap
        -Integer customerId
        +Cart()
        +Cart(Map~MenuItem, Integer~, Integer)
        +getCartId() Integer
        +getCartItemMap() Map~MenuItem, Integer~
        +getCustomerId() Integer
        +setCustomerId(Integer) void
    }

    class Order {
        -IdGenerator idGenerator$
        -Integer orderId
        -Integer customerId
        -String customerName
        -String customerAddress
        -Map~MenuItem, Integer~ items
        -Double subtotal
        -Double discountAmount
        -Double finalAmount
        -PaymentMode paymentMode
        -OrderStatus status
        -Integer assignedAgentId
        -String assignedAgentName
        +Order(Integer, String, String, Map, Double, Double, Double)
        +getOrderId() Integer
        +getCustomerId() Integer
        +getCustomerName() String
        +getCustomerAddress() String
        +getItems() Map~MenuItem, Integer~
        +getSubtotal() Double
        +getDiscountAmount() Double
        +getFinalAmount() Double
        +getPaymentMode() PaymentMode
        +setPaymentMode(PaymentMode) void
        +getStatus() OrderStatus
        +setStatus(OrderStatus) void
        +getAssignedAgentId() Integer
        +setAssignedAgentId(Integer) void
        +getAssignedAgentName() String
        +setAssignedAgentName(String) void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: model.payment
    %% ════════════════════════════════════════════

    class PaymentStrategy {
        <<interface>>
        +pay(Double amount) void
    }

    class CashPayment {
        +CashPayment()
        +pay(Double amount) void
    }

    class UpiPayment {
        +pay(Double amount) void
    }

    class Discount {
        -Double priceThreshold
        -Double discountRate
        +Discount(Double, Double)
        +getPriceThreshold() Double
        +getDiscountRate() Double
    }

    class Payment {
        -IdGenerator idGenerator$
        -PaymentStrategy strategy
        -Integer paymentId
        -Integer orderId
        -Double amount
        -PaymentMode paymentMode
        -Boolean isCompleted
        -String paymentIdentifier
        +Payment(Integer, Double, PaymentMode, PaymentStrategy, String)
        +processPayment() void
        +getPaymentId() Integer
        +getOrderId() Integer
        +getAmount() Double
        +getPaymentMode() PaymentMode
        +getIsCompleted() Boolean
    }

    class PaymentFactory {
        -Map~String, Supplier~ registry$
        +createPayment(String type)$ PaymentStrategy
        +registerNewUserType(String, Supplier)$ void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: exception
    %% ════════════════════════════════════════════

    class EmptyCartException {
        ~String message
        +EmptyCartException(String msg)
        +getMessage() String
    }

    class RestaurantClosedException {
        ~String message
        +RestaurantClosedException(String msg)
        +getMessage() String
    }

    class UserNotFoundException {
        ~String message
        +UserNotFoundException(String msg)
        +getMessage() String
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: observer
    %% ════════════════════════════════════════════

    class Observer {
        <<interface>>
        +update(String eventType, Order order) void
    }

    class AdminObserver {
        -String adminName
        +AdminObserver(String)
        +update(String, Order) void
        +getAdminName() String
        +equals(Object o) boolean
        +hashCode() int
    }

    class CustomerObserver {
        -Integer customerId
        -String customerName
        +CustomerObserver(Integer, String)
        +update(String, Order) void
        +getCustomerId() Integer
        +equals(Object o) boolean
        +hashCode() int
    }

    class DeliveryAgentObserver {
        -Integer agentId
        -String agentName
        +DeliveryAgentObserver(Integer, String)
        +update(String, Order) void
        +getAgentId() Integer
        +equals(Object o) boolean
        +hashCode() int
    }

    class EventManager {
        -Map~String, List~ listeners
        +subscribe(String, Observer) void
        +unsubscribe(String, Observer) void
        +notifyObservers(String, Order) void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: service
    %% ════════════════════════════════════════════

    class BaseService {
        <<abstract>>
        -Set~String~ globalUsernameRegistry$
        -Map~String, User~ allUserMap
        -Set~User~ loggedInUserSet
        -String passwordPattern
        +signUp(String, String, String, String) User
        +login(String, String) User
        +logout(String) void
        +printProfile(String) void
        +findLoggedInUser(String) User
        +getUserByUsername(String) User
        +isAnyUserLoggedIn() Boolean
        +getLoggedInUsers() Set~User~
        +getAllUserMap() Map~String, User~
        +editUserProfile(User, Map) void
    }

    class AdminService {
        -MenuComponent menu
        -OrderService orderService
        -DeliveryAgentService deliveryAgentService
        -Queue~Integer~ deliveryQueue
        -Double totalRevenue
        +AdminService(OrderService, DeliveryAgentService)
        +signUp(String, String, String, String) User
        +setMenu(MenuComponent) void
        +getMenu() MenuComponent
        +addMenuItemToCategory(Integer, MenuItem) void
        +addCategory(String) void
        +findCategory(MenuComponent, Integer) MenuComponent
        +addDiscount(Double, Double) void
        +removeDiscount(Double) void
        +viewPendingOrders() List~Order~
        +viewApprovedOrders() List~Order~
        +approveOrder(Integer) void
        +queueOrderForDelivery(Integer) void
        +assignOrderToAgent(Order, DeliveryAgent) void
        +processDeliveryQueue() void
        +getDeliveryQueue() Queue~Integer~
        +collectAllMenuItems(MenuComponent, List) void
        +getRevenue() Double
        +getCategoryList(MenuComponent) List~MenuCategory~
    }

    class CustomerService {
        +signUp(String, String, String, String) User
        +getCustomerByUsername(String) Customer
    }

    class DeliveryAgentService {
        -Integer deliveryAgentCount$
        -Integer DELIVERY_AGENT_COUNT_LIMIT$
        +signUp(String, String, String, String) User
        +getAgentByUsername(String) DeliveryAgent
        +findAvailableAgent() DeliveryAgent
        +markOrderAsDelivered(DeliveryAgent, OrderService, AdminService) void
        +viewAssignedOrder(DeliveryAgent, OrderService) void
        +isAnyAgentLoggedIn() Boolean
        +startDelivery(DeliveryAgent, OrderService) void
        +payDeliveryAgent(DeliveryAgent, Double) void
        +getGrossEarning(DeliveryAgent) Double
        +setDeliveryAgentBaseSalary(DeliveryAgent, Double) void
        +setDeliveryAgentCommissionRate(DeliveryAgent, Double) void
        +getDeliveryAgentFinanceInfo(DeliveryAgent) Map~String, Double~
    }

    class CartService {
        -Map~MenuItem, Integer~ cartItemMap
        +CartService()
        +addToCart(MenuItem, Integer) void
        +removeFromCart(MenuItem) void
        +reduceQuantity(MenuItem, Integer) void
        +clearCart() void
        +calculateTotalValue() Double
        +findDiscount(Double) Double
        +findFinalAmount(Double, Double) Double
        +finalizeCart(Integer) Cart
        +getCartItemMap() Map~MenuItem, Integer~
        +isEmpty() Boolean
        +printCart() void
    }

    class OrderService {
        -List~Order~ allOrders
        -EventManager eventManager
        +OrderService(EventManager)
        +addOrder(Order) void
        +getOrderById(Integer) Order
        +getOrdersByCustomerId(Integer) List~Order~
        +getPendingOrders() List~Order~
        +getApprovedOrders() List~Order~
        +updateOrderStatus(Integer, OrderStatus) void
        +getAllOrders() List~Order~
        +getOrderInfo(Integer) String
        +getOrderDetails(Integer) String
    }

    class DiscountService {
        -Map~Double, Double~ discountMap$
        +add(Discount)$ boolean
        +edit(Discount)$ boolean
        +remove(Double)$ boolean
        +getDiscount(Double)$ Discount
        +printAllDiscounts()$ void
    }

    class InvoiceService {
        +printInvoice(Order)$ void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: panel
    %% ════════════════════════════════════════════

    class AdminPanel {
        -AdminService adminService
        -OrderService orderService
        -CustomerService customerService
        -DeliveryAgentService deliveryAgentService
        -Scanner scanner
        -Admin currentAdmin
        +AdminPanel(AdminService, OrderService, CustomerService, DeliveryAgentService, Scanner)
        +run() Boolean
        -manageMenu() void
        -discountsMenu() void
        -ordersMenu() void
        -profilesMenu() void
        -financeMenu() void
        -login() void
        -signUp() void
        -logout() void
        -viewMenu() void
        -addMenuItem() void
        -addCategory() void
        -viewDiscounts() void
        -addDiscount() void
        -removeDiscount() void
        -viewPendingOrders() void
        -printOrderSummary(Order) void
        -approveOrder() void
        -viewAllOrderHistory() void
        -viewAllProfiles() void
        +isAdminLoggedIn() Boolean
        -viewDeliveryQueue() void
        -viewOrderDetails() void
        -viewAProfile() void
        -viewRevenue() void
        -displayCategoryList() void
        -manageDeliveryAgentFinance() void
        -editAdminProfile() void
    }

    class CustomerPanel {
        -CustomerService customerService
        -AdminService adminService
        -OrderService orderService
        -EventManager eventManager
        -Scanner scanner
        -Customer currentCustomer
        -CartService cartService
        +CustomerPanel(CustomerService, AdminService, OrderService, EventManager, Scanner)
        +run() Boolean
        -signUp() void
        -login() void
        -logout() void
        -viewMenu() void
        -addToCart() void
        -viewCart() void
        -removeFromCart() void
        -placeOrder() void
        -viewMyOrders() void
        -editCustomerProfile() void
    }

    class DeliveryAgentPanel {
        -DeliveryAgentService deliveryAgentService
        -OrderService orderService
        -AdminService adminService
        -EventManager eventManager
        -Scanner scanner
        -DeliveryAgent currentAgent
        +DeliveryAgentPanel(DeliveryAgentService, OrderService, AdminService, EventManager, Scanner)
        +run() Boolean
        -showGrossEarning() void
        -signUp() void
        -login() void
        -logout() void
        -switchAgent(List~DeliveryAgent~) void
        -switchToOtherAgent() void
        -getLoggedInAgents() List~DeliveryAgent~
        -viewAssignedOrder() void
        -startDelivery() void
        -markDelivered() void
        -editDeliveryAgentProfile() void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: facade
    %% ════════════════════════════════════════════

    class FoodOrderingFacade {
        -AdminService adminService
        -CustomerService customerService
        -DeliveryAgentService deliveryAgentService
        -OrderService orderService
        -EventManager eventManager
        -AdminPanel adminPanel
        -CustomerPanel customerPanel
        -DeliveryAgentPanel deliveryAgentPanel
        -Scanner scanner
        +FoodOrderingFacade()
        -initializeMenu() void
        -initializeDiscounts() void
        +run() void
        -runAdminPanel() void
        -runCustomerPanel() void
        -runDeliveryAgentPanel() void
    }

    %% ════════════════════════════════════════════
    %% PACKAGE: util
    %% ════════════════════════════════════════════

    class IdGenerator {
        -Integer id
        +generateId() Integer
    }

    %% ════════════════════════════════════════════
    %% MAIN
    %% ════════════════════════════════════════════

    class Main {
        +main(String[] args)$ void
    }

    %% ═══════════════════════════════════════════════
    %% RELATIONSHIPS
    %% ═══════════════════════════════════════════════

    %% Inheritance / Implementation
    BaseUser ..|> User : implements
    Admin --|> BaseUser : extends
    Customer --|> BaseUser : extends
    DeliveryAgent --|> BaseUser : extends

    MenuItem ..|> MenuComponent : implements
    MenuCategory ..|> MenuComponent : implements

    CashPayment ..|> PaymentStrategy : implements
    UpiPayment ..|> PaymentStrategy : implements

    AdminObserver ..|> Observer : implements
    CustomerObserver ..|> Observer : implements
    DeliveryAgentObserver ..|> Observer : implements

    EmptyCartException --|> Exception : extends
    RestaurantClosedException --|> Exception : extends
    UserNotFoundException --|> Exception : extends

    AdminService --|> BaseService : extends
    CustomerService --|> BaseService : extends
    DeliveryAgentService --|> BaseService : extends

    %% Composition & Aggregation
    MenuCategory o-- MenuComponent : contains *
    Cart o-- MenuItem : cartItemMap *
    Order o-- MenuItem : items *

    Order --> OrderStatus : status
    Order --> PaymentMode : paymentMode
    DeliveryAgent --> DeliveryAgentStatus : status

    Payment --> PaymentStrategy : strategy
    Payment --> PaymentMode : paymentMode

    EventManager o-- Observer : listeners *

    %% Dependencies & Associations
    BaseUser --> IdGenerator : uses
    MenuItem --> IdGenerator : uses
    MenuCategory --> IdGenerator : uses
    Cart --> IdGenerator : uses
    Order --> IdGenerator : uses
    Payment --> IdGenerator : uses

    BaseService --> UserFactory : uses
    UserFactory --> TriFunction : registry
    UserFactory ..> Admin : creates
    UserFactory ..> Customer : creates
    UserFactory ..> DeliveryAgent : creates

    PaymentFactory ..> CashPayment : creates
    PaymentFactory ..> UpiPayment : creates
    PaymentFactory --> PaymentStrategy : returns

    AdminService --> OrderService : orderService
    AdminService --> DeliveryAgentService : deliveryAgentService
    AdminService --> MenuComponent : menu

    OrderService --> EventManager : eventManager
    OrderService o-- Order : allOrders *

    CartService o-- MenuItem : cartItemMap *
    CartService --> DiscountService : uses
    CartService ..> Cart : creates

    DiscountService --> Discount : uses

    AdminPanel --> AdminService : adminService
    AdminPanel --> OrderService : orderService
    AdminPanel --> CustomerService : customerService
    AdminPanel --> DeliveryAgentService : deliveryAgentService
    AdminPanel --> Admin : currentAdmin

    CustomerPanel --> CustomerService : customerService
    CustomerPanel --> AdminService : adminService
    CustomerPanel --> OrderService : orderService
    CustomerPanel --> EventManager : eventManager
    CustomerPanel --> Customer : currentCustomer
    CustomerPanel --> CartService : cartService

    DeliveryAgentPanel --> DeliveryAgentService : deliveryAgentService
    DeliveryAgentPanel --> OrderService : orderService
    DeliveryAgentPanel --> AdminService : adminService
    DeliveryAgentPanel --> EventManager : eventManager
    DeliveryAgentPanel --> DeliveryAgent : currentAgent

    FoodOrderingFacade --> AdminService : adminService
    FoodOrderingFacade --> CustomerService : customerService
    FoodOrderingFacade --> DeliveryAgentService : deliveryAgentService
    FoodOrderingFacade --> OrderService : orderService
    FoodOrderingFacade --> EventManager : eventManager
    FoodOrderingFacade --> AdminPanel : adminPanel
    FoodOrderingFacade --> CustomerPanel : customerPanel
    FoodOrderingFacade --> DeliveryAgentPanel : deliveryAgentPanel

    Main ..> FoodOrderingFacade : creates & runs

    InvoiceService ..> Order : uses

    AdminObserver ..> Order : observes
    CustomerObserver ..> Order : observes
    DeliveryAgentObserver ..> Order : observes
```

---

## 📦 Package Breakdown

| Package | Classes | Role |
|---|---|---|
| **`model.user`** | [User](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/User.java#3-13), [BaseUser](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/BaseUser.java#7-69), [Admin](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/Admin.java#5-24), [Customer](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/Customer.java#3-22), [DeliveryAgent](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/DeliveryAgent.java#5-75), [UserFactory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/UserFactory.java#12-32), [TriFunction](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/TriFunction.java#3-7) | User domain models, inheritance hierarchy, and factory |
| **`model.order`** | [MenuComponent](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuComponent.java#5-15), [MenuItem](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuItem.java#8-81), [MenuCategory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuCategory.java#8-73), [Cart](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/Cart.java#9-43), [Order](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/Order.java#9-103) | Menu composite pattern, shopping cart, and orders |
| **`model.payment`** | [PaymentStrategy](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/PaymentStrategy.java#3-6), [CashPayment](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/CashPayment.java#3-11), [UpiPayment](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/UpiPayment.java#3-10), [Payment](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/Payment.java#6-60), [PaymentFactory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/PaymentFactory.java#10-28), [Discount](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/Discount.java#9-26) | Strategy pattern for payments & factory |
| **`model.enums`** | `DeliveryAgentStatus`, [OrderStatus](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/OrderService.java#51-76), [PaymentMode](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/Order.java#75-78) | Status and type enumerations |
| **`exception`** | [EmptyCartException](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/exception/EmptyCartException.java#3-14), [RestaurantClosedException](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/exception/RestaurantClosedException.java#3-14), [UserNotFoundException](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/exception/UserNotFoundException.java#3-14) | Custom checked exceptions |
| **`observer`** | [Observer](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/Observer.java#5-8), [AdminObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/AdminObserver.java#6-45), [CustomerObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/CustomerObserver.java#5-77), [DeliveryAgentObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/DeliveryAgentObserver.java#5-44), [EventManager](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/EventManager.java#10-36) | Observer pattern for event notifications |
| **`service`** | [BaseService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/BaseService.java#12-137), [AdminService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/AdminService.java#16-216), [CustomerService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/CustomerService.java#6-24), [DeliveryAgentService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/DeliveryAgentService.java#11-149), [CartService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/CartService.java#10-99), [OrderService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/OrderService.java#13-134), [DiscountService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/DiscountService.java#8-52), [InvoiceService](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/service/InvoiceService.java#8-47) | Business logic layer |
| **`panel`** | [AdminPanel](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/panel/AdminPanel.java#21-571), [CustomerPanel](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/panel/CustomerPanel.java#20-380), [DeliveryAgentPanel](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/panel/DeliveryAgentPanel.java#14-252) | Console UI / presentation layer |
| **`facade`** | [FoodOrderingFacade](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/facade/FoodOrderingFacade.java#17-171) | Facade pattern — single entry point |
| **`util`** | [IdGenerator](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/util/IdGenerator.java#3-9) | Auto-incrementing ID utility |

---

## 🎨 Design Patterns Used

| Pattern | Where | Description |
|---|---|---|
| **Singleton** | [Admin](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/Admin.java#5-24) | Double-checked locking ensures one admin instance |
| **Factory** | [UserFactory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/UserFactory.java#12-32), [PaymentFactory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/PaymentFactory.java#10-28) | Creates [User](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/user/User.java#3-13) / [PaymentStrategy](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/PaymentStrategy.java#3-6) from type string |
| **Strategy** | [PaymentStrategy](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/PaymentStrategy.java#3-6) ← [CashPayment](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/CashPayment.java#3-11), [UpiPayment](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/payment/UpiPayment.java#3-10) | Swappable payment algorithms |
| **Composite** | [MenuComponent](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuComponent.java#5-15) ← [MenuItem](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuItem.java#8-81), [MenuCategory](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/model/order/MenuCategory.java#8-73) | Tree structure for nested menu categories |
| **Observer** | [Observer](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/Observer.java#5-8) ← [AdminObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/AdminObserver.java#6-45), [CustomerObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/CustomerObserver.java#5-77), [DeliveryAgentObserver](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/DeliveryAgentObserver.java#5-44) + [EventManager](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/observer/EventManager.java#10-36) | Event-driven order status notifications |
| **Facade** | [FoodOrderingFacade](file:///d:/Bhavika_Work/TSS%20practice/FoodDeliveryApp/FoodApp/src/main/java/facade/FoodOrderingFacade.java#17-171) | Simplifies system startup and panel routing |

---

## 🔗 Key Relationship Legend

| Arrow | Meaning |
|---|---|
| `──▶` solid line | **Association** — field reference |
| `──▷` solid with triangle | **Inheritance** (`extends`) |
| `╌╌▷` dashed with triangle | **Implementation** (`implements`) |
| `╌╌▶` dashed line | **Dependency** — creates or uses transiently |
| `◇──` diamond | **Aggregation** — "has many" collection |
