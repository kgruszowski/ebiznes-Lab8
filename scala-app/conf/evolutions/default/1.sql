-- Users schema

-- !Ups

CREATE TABLE "category" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL
);

CREATE TABLE "product" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL,
    "description" TEXT NOT NULL,
    "photo" VARCHAR NOT NULL,
    "price" FLOAT NOT NULL,
    "category" INT NOT NULL,
    FOREIGN KEY(category) references category(id)
);

CREATE TABLE "inventory" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product" INT NOT NULL,
    "quantity" INT NOT NULL,
    "available" INT NOT NULL,
    FOREIGN KEY(product) references product(id)
);

CREATE TABLE "customer" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "firstname" VARCHAR NOT NULL,
    "surname" VARCHAR NOT NULL,
    "address" VARCHAR NOT NULL
);

CREATE TABLE "shippingMethod" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL,
    "deliveryTime" VARCHAR NOT NULL,
    "price" FLOAT NOT NULL
);

CREATE TABLE "review" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product" INT NOT NULL,
    "rate" INT NOT NULL,
    "comment" TEXT NULL,
    FOREIGN KEY(product) references product(id)
);

CREATE TABLE "wishlist" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product" INT NOT NULL,
    "customer" INT NOT NULL,
    FOREIGN KEY(product) references product(id),
    FOREIGN KEY(customer) references customer(id)
);

CREATE TABLE "discount" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "code" VARCHAR NOT NULL,
    "value" INT NOT NULL
);

CREATE TABLE "cart" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "customer" INT NOT NULL,
    "enabled" INT NOT NULL
);

CREATE TABLE "cart_products" (
    "cart" INT NOT NULL,
    "product" INT NOT NULL,
    "quantity" INT NOT NULL,
    FOREIGN KEY(cart) references cart(id),
    FOREIGN KEY(product) references product(id)
);

CREATE TABLE "order" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "cart" INT NOT NULL,
    "discount" INT NULL,
    "shippingMethod" INT NOT NULL,
    FOREIGN KEY(cart) references cart(id),
    FOREIGN KEY(discount) references discount(id),
    FOREIGN KEY(shippingMethod) references shippingMethod(id)
);

CREATE TABLE "social_auth_info" (
  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  provider varchar(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  access_token TEXT NOT NULL,
  token_type VARCHAR(30),
  expires_in INT,
  refresh_token TEXT
);

CREATE TABLE "user" (
    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    user_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- !Downs

DROP TABLE "category";
DROP TABLE "product";
DROP TABLE "inventory";
DROP TABLE "customer";
DROP TABLE "cart";
DROP TABLE "cart_products";
DROP TABLE "shippingMethod";
DROP TABLE "review";
DROP TABLE "order";
DROP TABLE "discount";
DROP TABLE "wishlist";
DROP TABLE "social_auth_info";