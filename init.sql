
CREATE TABLE products (
    id serial PRIMARY KEY,
    name VARCHAR (255) NOT NULL,
    description TEXT,
    price DECIMAL (10, 2) NOT NULL
);

INSERT INTO products (name, description, price)
VALUES
    ('Product A', 'Description for Product A', 19.99),
    ('Product B', 'Description for Product B', 29.99),
    ('Product C', 'Description for Product C', 39.99);
CREATE ROLE "ro" NOINHERIT;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO "ro";

