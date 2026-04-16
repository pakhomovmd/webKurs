// ========== ИСПОЛЬЗУЕМЫЕ ФУНКЦИИ ==========

// Эта функция вызывается в HTML
function handleMainButtonClick() {
    console.log('Главная кнопка нажата!');
    alert('Привет от анализатора!');
}

// Эта функция используется
function showMessage(message) {
    console.log('Сообщение:', message);
    const hero = document.querySelector('.hero');
    if (hero) {
        hero.style.backgroundColor = '#f0f0f0';
    }
}

// Эта функция используется в другой функции
function getCurrentTime() {
    return new Date().toLocaleTimeString();
}

// Используемая переменная
const appVersion = '1.0.0';

// Обработчики событий
document.addEventListener('DOMContentLoaded', () => {
    console.log('Страница загружена');
    showMessage('Добро пожаловать!');
    
    const mainButton = document.getElementById('mainButton');
    if (mainButton) {
        mainButton.addEventListener('click', handleMainButtonClick);
    }
    
    const secondaryBtn = document.getElementById('secondaryBtn');
    if (secondaryBtn) {
        secondaryBtn.addEventListener('click', () => {
            console.log('Вторичная кнопка нажата в', getCurrentTime());
        });
    }
});

// ========== НЕИСПОЛЬЗУЕМЫЕ ФУНКЦИИ (МЁРТВЫЙ КОД) ==========

// Эта функция нигде не вызывается
function oldFunction() {
    console.log('Эта функция устарела');
    return 'old value';
}

// Неиспользуемая функция с параметрами
function calculateSomething(a, b, c) {
    let result = a + b + c;
    return result * 2;
}

// Неиспользуемая функция для валидации
function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

// Неиспользуемая функция для работы с API
async function fetchUserData(userId) {
    const response = await fetch(`/api/users/${userId}`);
    return response.json();
}

// ========== НЕИСПОЛЬЗУЕМЫЕ ПЕРЕМЕННЫЕ ==========

// Объявлена, но не используется
let unusedCounter = 0;

// Константа, которая нигде не используется
const MAX_RETRIES = 3;

// Неиспользуемый объект
const config = {
    apiUrl: 'https://api.example.com',
    timeout: 5000,
    retry: true
};

// Неиспользуемая переменная для темы
let theme = 'light';