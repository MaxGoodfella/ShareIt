### О проекте 

*ShareIt* - микросервисное приложение для аренды вещей

Приложение позволяет пользователям выставлять вещи, которыми они готовы поделиться, брать вещи в аренду на определённое время, просматривать доступные для аренды вещи.

*Cтек технологий*: Java, Spring Boot, Spring Data JPA, Lombok, REST API, Docker, Postman, Postgres, JPQL, JUnit, Mockito, Integration-testing.

Приложение разделено на 2 микросервиса: 
- gateway - валидация запросов;
- server - бизнес-логика.

Для микросервиса ``` server ``` своя база данных.

Микросервисы и база данных поднимаются через docker-compose в 3 отдельных контейнерах.

### Функциональность 

- Добавление, обновление и получение пользователей;
- Добавление, обновление, получение предметов и поиск;
- Управление заявками на аренду вещей;
- Обработка запросов на аренду вещей;
- Комментирование успешно завершённой аренды.

### Запуск приложения

Приложение поднимается через docker-compose, одновременно поднимая 2 инстанса PostgreSQL, а также 2 сервиса (бизнес-логика и статистика).

Порядок запуска приложения:
1. *Клонирование репозитория*
```
git clone https://github.com/MaxGoodfella/ShareIt.git
cd ShareIt
```

2. *Запуск контейнеров используя Makefile*
```
make restart
```
Это действие поднимет 3 контейнера, перезапишет Maven. Для перезапуска в дальнейшем можно смело использовать напрямую без отдельного ``` docker-compose down ```.

3. *Запуск коллекции запросов*

Коллекция postman-запросов доступна в папке postman. 
