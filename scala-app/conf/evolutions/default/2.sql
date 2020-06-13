-- Users schema

-- !Ups

INSERT INTO "category" (name)
VALUES ("pianos"), ("guitars"), ("violins"), ("trumpets");

INSERT INTO "shippingMethod" (name, deliveryTime, price)
VALUES
    ("InPost", "2-3 days", 8.99),
    ("DPD", "1-2 days", 15.99),
    ("UPS", "1-2 days", 14.99);

INSERT INTO "discount" (code, value)
VALUES ("promo20", 15), ("ebiznes", 20);

INSERT INTO "product" (name, description, photo, price, category)
VALUES
    ("Yamaha P-125", "Cheap and easy to play", "https://static2.redcart.pl/templates/images/thumb/17512/1500/1500/pl/0/templates/images/products/17512/745528245be0a1fcb433baae079c04db.jpg", 1599.99, 1),
    ("Yamaha P-155", "The best piano in that price", "https://www.runthemusic.com/wp-content/uploads/2017/09/Yamaha-P155-1.jpg", 2299.99, 1),
    ("Fender Telecaster", "The legend is here", "https://www.fan.com.pl/userdata/public/gfx/75248/GIT.-FENDER-TELE-72-DELUXE-WLN-11996.jpg", 999.99, 2),
    ("Ibanez AE 315", "La cucaraca", "https://static.sonovente.com/img/library/zoom/46/optim/46350_1.jpg", 999.99, 2),
    ("Fender Stratocaster", "Money for nothing", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRKheEAezJzWQXtKMuA4yJx4XbY--3gFNCSKv451wRcnQji3frY&usqp=CAU", 1299.99, 2),
    ("Bach Bb", "Learn and enjoy", "https://www.woodbrass.com/images/SQUARE400/woodbrass/BACH+TR501+VERNIE.JPG", 119.99, 3),
    ("Arnold trumpeter", "Tuturutu", "https://images.samash.com/sa/BAT/BATP100XX-P.fpx?cvt=jpg", 29.99, 3);

INSERT INTO "review" (rate, comment, product)
VALUES (5, "nice piano", 1);

INSERT INTO "inventory" (product, quantity, available)
VALUES
    (1, 100, 100),
    (2, 75, 75),
    (3, 0, 0),
    (4, 15, 15),
    (5, 5, 5),
    (6, 150, 150),
    (7, 999, 999);

INSERT INTO "customer" (firstname, surname, address)
VALUES
    ("Joe", "Doe", "Seasame st. 18"),
    ("Ann", "Black", "Long st. 233");

INSERT INTO "cart" (customer, enabled)
VALUES (1, 1);

INSERT INTO "cart_products" (cart, product, quantity)
VALUES
    (1,1,1),
    (1,4,1);

INSERT INTO "order" (cart, shippingMethod, discount)
VALUES (1, 2, null);

INSERT INTO "wishlist" (customer, product)
VALUES (1, 3);

-- !Downs

DELETE FROM "category"
DELETE FROM "shippingMethod"
DELETE FROM "discount"
DELETE FROM "product"
DELETE FROM "inventory"
DELETE FROM "reviews"
DELETE FROM "customer"
DELETE FROM "cart_products"
DELETE FROM "cart"