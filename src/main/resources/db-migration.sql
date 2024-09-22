CREATE TABLE authors (
                         id SERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL
);

CREATE TABLE books (
                       id SERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       author_id INT REFERENCES authors(id) ON DELETE CASCADE
);

-- Таблица для хранения связей ManyToMany между книгами
CREATE TABLE book_relationships (
                                    book_id INT REFERENCES books(id) ON DELETE CASCADE,
                                    related_book_id INT REFERENCES books(id) ON DELETE CASCADE,
                                    PRIMARY KEY (book_id, related_book_id)
);