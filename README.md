# java-filmorate
Template repository for Filmorate project.
#### Диаграма проекта:
![This is an image](src/main/resources/DatabaseER.jpeg)

#### Запрос на получение всех пользователей:

SELECT * <br/>
FROM USERS

#### Запрос на получение пользователя по id:

SELECT * <br/>
FROM USERS <br/>
where USER_ID = ?

#### Запрос на получение всех фильмов:

SELECT * <br/>
FROM FILMS

#### Запрос на получение фильма по id:

SELECT * <br/>
FROM FILMS <br/>
where FILM_ID = ?

#### Запрос на получение друзей пользователя:

SELECT U.USER_ID,<br/> 
       U.USER_NAME,<br/>
       U.EMAIL,<br/>
       U.BIRTHDAY, <br/>
       U.LOGIN <br/> 
FROM FRIENDSHIPS as F <br/>
LEFT JOIN USERS U ON F.FRIEND_ID = U.USER_ID <br/>
WHERE F.USER_ID = ?


#### Запрос на получение общих друзей:

SELECT U.USER_ID,<br/>
       U.USER_NAME,<br/>
       U.EMAIL,<br/>
       U.BIRTHDAY,<br/>
       U.LOGIN <br/>
FROM FRIENDSHIPS AS F INNER JOIN FRIENDSHIPS F2 ON F.FRIEND_ID = F2.FRIEND_ID <br/>
LEFT JOIN USERS U ON F.FRIEND_ID = U.USER_ID <br/>
WHERE F.USER_ID = ? AND F2.USER_ID = ?

#### Запрос на получение популярных фильмов:

SELECT F.FILM_ID AS FILM_ID,<br/>
       F.DESCRIPTION AS DESCRIPTION,<br/>
       F.DURATION AS DURATION,<br/>
       F.FILM_NAME AS FILM_NAME,<br/> 
       F.MPA AS MPA,<br/> 
       F.RELEASE_DATE AS RELEASE_DATE<br/>
FROM FILMS F <br/>
LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID <br/> 
GROUP BY F.FILM_ID<br/>
ORDER BY COUNT(DISTINCT L.USER_ID) DESC LIMIT ?<br/>