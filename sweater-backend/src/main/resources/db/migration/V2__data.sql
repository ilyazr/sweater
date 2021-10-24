INSERT INTO chat_message_status (status) VALUES ('SENT');
INSERT INTO chat_message_status (status) VALUES ('DELIVERED');


INSERT INTO users (id, country, email, first_name, last_name, password, phone_number, username, enabled, date_of_birth)
 VALUES (0, 'Russia', 'hello@gmail.com', 'ilya', 'zakharov', 'qwerty', '89041602229', 'dev123', true, '1999-02-09 19:40:00-00'::timestamp),
        (1, 'Germany', 'kekoleg@mail.ru', 'oleg', 'petrovich', 'qwerty', '89042946311', 'Newuser12345', true, '2001-01-11 15:24:00-00'::timestamp),
        (2, 'Australia', 'newbox@yahoo.com', 'Jack', 'Johnson', 'jjfsdnfbjsd', '89021941252', 'jack1eb0y', true, '1977-03-16 08:53:00-00'::timestamp),
        (3, 'USA', 'mynewbox@yahoo.com', 'alex', 'ivanov', 'qazwsxedc', '89921641517', 'TheGhost', true, '1973-01-28 12:12:00-00'::timestamp),
        (4, 'Canada', 'qwer@mail.com', 'john', 'jackson', 'qwerty', '89911635717', 'smile', true, '1978-02-21 12:12:00-00'::timestamp),
        (5, 'Russia', 'globe@gmail.com', 'petr', 'sobolev', 'qwerty', '89921436113', 'addmeasafriend', true, '1978-02-21 12:12:00-00'::timestamp);

SELECT setval('users_id_seq', max(id)) FROM users;

INSERT INTO wall_messages (created_at, text, updated_at, author_id)
VALUES ('2013-11-03 00:00:00-07'::timestamptz, 'dev123 first msg', '2013-11-03 00:00:00-07'::timestamptz, 0),
       ('2013-11-03 01:00:00-07'::timestamptz, 'dev123 second msg', '2013-11-03 01:00:00-07'::timestamptz, 0),
       ('2013-11-03 02:00:00-07'::timestamptz, 'dev123 third msg', '2013-11-03 02:00:00-07'::timestamptz, 0),
       ('2013-11-03 03:00:00-07'::timestamptz, 'TheGhost first msg', '2013-11-03 03:00:00-07'::timestamptz, 3),
       ('2013-11-03 04:00:00-07'::timestamptz, 'TheGhost second msg', '2013-11-03 04:00:00-07'::timestamptz, 3),
       ('2013-11-03 04:00:00-07'::timestamptz, 'Newuser12345 first msg', '2013-11-03 04:00:00-07'::timestamptz, 1);

INSERT INTO likes (created_at, owner_id, post_id) VALUES (now(), 3, 2);


INSERT INTO friendship_statuses (status) VALUES ('requested'), ('accepted');

INSERT INTO friendship (user_id, friend_id, status)
VALUES (0, 1, 'accepted'),
       (1, 0, 'accepted'),
       (0, 3, 'accepted'),
       (3, 0, 'accepted'),
       (2, 0, 'requested'),
       (0, 4, 'requested');

INSERT INTO chat_room (capacity, owner_id)
VALUES (2, 0), (2, 0);

INSERT INTO chat_room_users (chat_room_id, user_id)
VALUES (1, 0), (1, 1),
       (2, 0), (2, 3);

INSERT INTO chat_message
    (chat_room_id, created_at, recipient_id, sender_id, status, text)
    VALUES (1, '2021-10-11 12:00:00-07'::timestamptz, 1, 0, 'DELIVERED', 'Hi, Oleg!'),
           (1, '2021-10-11 12:05:00-07'::timestamptz, 0, 1, 'DELIVERED', 'Hello'),
           (1, '2021-10-11 12:06:00-07'::timestamptz, 0, 1, 'SENT', 'How you been?');


INSERT INTO user_roles (user_id, role)
 VALUES (0, 'ADMIN'), (1, 'USER'), (2, 'USER'), (3, 'USER'), (4, 'USER'), (5, 'USER');


INSERT INTO users_avatars (avatar_uri, user_id)
 VALUES ('http://localhost:9090/api/fs/download/dev123/dev123_avatar.jpg', 0),
        ('http://localhost:9090/api/fs/download/Newuser12345/Newuser12345_avatar.jpg', 1),
        ('http://localhost:9090/api/fs/download/jack1eb0y/jack1eb0y_avatar.jpg', 2),
        ('http://localhost:9090/api/fs/download/smile/smile_avatar.jpg', 4),
        ('http://localhost:9090/api/fs/download/addmeasafriend/addmeasafriend_avatar.jpg', 5);
