package config;

public class Endpoints {
    public static final String COURIER_CREATE = "/api/v1/courier"; // Создание курьера
    public static final String COURIER_LOGIN = "/api/v1/courier/login"; // Логин курьера
    public static final String COURIER_DELETE = "/api/v1/courier/"; // Удаление курьера (добавляем ID в тестах)
    public static final String ORDERS_CREATE = "/api/v1/orders"; // Создание заказа
    public static final String ORDERS_LIST = "/api/v1/orders"; // Получение списка заказов
}