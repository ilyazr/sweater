CREATE TABLE chat_message (
    id SERIAL PRIMARY KEY NOT NULL,
    chat_room_id integer,
    created_at timestamptz,
    recipient_id integer NOT NULL,
    sender_fullname character varying(255),
    sender_id integer NOT NULL,
    sender_username character varying(255),
    status character varying(255),
    text character varying(255)
);



CREATE TABLE chat_message_status (
    status text PRIMARY KEY NOT NULL
);



CREATE TABLE chat_room (
    id SERIAL PRIMARY KEY NOT NULL,
    capacity integer,
    name character varying(255),
    owner_id integer
);



CREATE TABLE chat_room_users (
    chat_room_id integer NOT NULL,
    user_id integer NOT NULL
);



CREATE TABLE confirmation_tokens (
    id SERIAL PRIMARY KEY NOT NULL,
    token character varying(255) NOT NULL,
    created_at timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL,
    confirmed_at timestamp with time zone,
    user_id integer NOT NULL
);



CREATE TABLE friendship (
    id SERIAL PRIMARY KEY NOT NULL,
    user_id integer,
    friend_id integer,
    status text
);



CREATE TABLE friendship_statuses (
    status text PRIMARY KEY NOT NULL
);



CREATE TABLE likes (
    id SERIAL PRIMARY KEY NOT NULL,
    created_at timestamptz,
    owner_id integer NOT NULL,
    post_id integer NOT NULL
);



CREATE TABLE online_users (
    id SERIAL PRIMARY KEY NOT NULL,
    session_id character varying(255),
    user_id integer,
    username character varying(255)
);



CREATE TABLE user_roles (
    user_id integer NOT NULL,
    role character varying(255) DEFAULT 'USER'::character varying NOT NULL
);



CREATE TABLE users (
    id SERIAL PRIMARY KEY NOT NULL,
    country character varying(255),
    email character varying(255) NOT NULL UNIQUE,
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255) NOT NULL,
    phone_number character varying(255),
    username character varying(255) NOT NULL UNIQUE,
    enabled boolean,
    date_of_birth timestamp
);



CREATE TABLE users_avatars (
    avatar_uri character varying(255),
    user_id integer NOT NULL
);



CREATE TABLE wall_messages (
    id SERIAL PRIMARY KEY NOT NULL,
    created_at timestamptz,
    img_uri character varying(255),
    text character varying(255) NOT NULL,
    updated_at timestamptz,
    author_id integer NOT NULL
);



CREATE TABLE wall_msg_comments (
    id SERIAL PRIMARY KEY NOT NULL,
    comment character varying(255) NOT NULL,
    created_at timestamptz,
    author_id integer NOT NULL,
    wall_msg_id integer NOT NULL
);


--
----------------------------------------BELOW ARE THE CONSTRAINTS----------------------------------------
--

ALTER TABLE online_users
    ADD CONSTRAINT fk_online_users_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;


ALTER TABLE online_users
    ADD CONSTRAINT fk_online_users_user_username FOREIGN KEY (username) REFERENCES users (username) ON UPDATE CASCADE;


ALTER TABLE ONLY chat_room_users
    ADD CONSTRAINT chat_room_users_pkey PRIMARY KEY (chat_room_id, user_id);


ALTER TABLE ONLY confirmation_tokens
    ADD CONSTRAINT confirmation_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;


ALTER TABLE ONLY likes
    ADD CONSTRAINT likes_post_id_fkey FOREIGN KEY (post_id) REFERENCES wall_messages(id);


ALTER TABLE ONLY chat_room
    ADD CONSTRAINT chat_room_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES users(id);


ALTER TABLE ONLY wall_messages
    ADD CONSTRAINT wall_messages_author_id_fkey FOREIGN KEY (author_id) REFERENCES users(id);


ALTER TABLE ONLY chat_room_users
    ADD CONSTRAINT fk_chat_room_id FOREIGN KEY (chat_room_id) REFERENCES chat_room(id) ON DELETE CASCADE;


ALTER TABLE ONLY chat_message
    ADD CONSTRAINT fk_msg_status FOREIGN KEY (status) REFERENCES chat_message_status(status) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE ONLY chat_message
    ADD CONSTRAINT chat_room_id_fk FOREIGN KEY (chat_room_id) REFERENCES chat_room(id) ON DELETE CASCADE;


ALTER TABLE ONLY chat_room_users
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;


ALTER TABLE ONLY user_roles
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;


ALTER TABLE ONLY users_avatars
    ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id);


ALTER TABLE ONLY likes
    ADD CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users(id);


ALTER TABLE ONLY wall_msg_comments
    ADD CONSTRAINT fk_wall_msg_id FOREIGN KEY (wall_msg_id) REFERENCES wall_messages(id);


ALTER TABLE ONLY wall_msg_comments
    ADD CONSTRAINT fk_author_id FOREIGN KEY (author_id) REFERENCES users(id);


ALTER TABLE ONLY friendship
    ADD CONSTRAINT friendship_friend_id_fkey FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE;


ALTER TABLE ONLY friendship
    ADD CONSTRAINT friendship_status_fkey FOREIGN KEY (status) REFERENCES friendship_statuses(status) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE ONLY friendship
    ADD CONSTRAINT friendship_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

