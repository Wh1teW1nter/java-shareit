
    CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    CONSTRAINT uq_user_email UNIQUE (email));

    CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description varchar(2000) NOT NULL,
    requester_id BIGINT NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id));

    CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(200) NOT NULL,
    description varchar(2000) NOT NULL,
    is_available boolean NOT NULL,
    user_id BIGINT NOT NULL,
    request_id BIGINT,
    CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id),
    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id));

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status varchar(10) NOT NULL,
     CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id),
     CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id));

    CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text varchar(2000) NOT NULL,
    author_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id),
    CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id));