// ========== ИСПОЛЬЗУЕМЫЕ ФУНКЦИИ ==========

// Эта функция используется в script.js
function formatDate(date) {
    if (!date) date = new Date();
    return date.toISOString().split('T')[0];
}

// Используется через вызов в другой функции
function logEvent(eventName) {
    console.log(`[${formatDate()}] Событие: ${eventName}`);
}

// ========== НЕИСПОЛЬЗУЕМЫЕ ФУНКЦИИ ==========

// Неиспользуемая функция для валидации пароля
function validatePassword(password) {
    if (password.length < 8) return false;
    if (!/[A-Z]/.test(password)) return false;
    if (!/[0-9]/.test(password)) return false;
    return true;
}

// Неиспользуемая функция для debounce
function debounce(func, delay) {
    let timeout;
    return function() {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, arguments), delay);
    };
}

// Неиспользуемая функция для throttle
function throttle(func, limit) {
    let inThrottle;
    return function() {
        if (!inThrottle) {
            func.apply(this, arguments);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// Неиспользуемая переменная
let analyticsEnabled = true;

// Неиспользуемый класс
class UserManager {
    constructor() {
        this.users = [];
    }
    
    addUser(user) {
        this.users.push(user);
    }
    
    getUsers() {
        return this.users;
    }
}