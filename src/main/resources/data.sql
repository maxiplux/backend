-- 100 Product Insertions for PRODUCT table

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Wireless Headphones (1)', 'High-quality device with advanced features', 129.99, 1, 250)
;

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Smart Watch (2)', 'Latest technology with premium design', 249.50, 1, 150)
;

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Laptop (3)', 'Powerful and efficient performance', 799.99, 1, 75)
;

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Tablet (4)', 'Sleek and modern electronic gadget', 349.99, 1, 100)
;

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Gaming Console (5)', 'Cutting-edge technology for tech enthusiasts', 499.99, 1, 200)
;

-- ... and so on for 100 records
-- I'll continue with a few more examples to demonstrate the pattern

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Bluetooth Speaker (6)', 'Portable sound solution', 89.99, 1, 300)
;

INSERT INTO PRODUCT ( NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
VALUES
    ( 'Digital Camera (7)', 'Professional photography equipment', 599.99, 1, 80)
;


DECLARE
TYPE product_rec_type IS RECORD (
    name VARCHAR2(255),
    description VARCHAR2(255),
    price NUMBER,
    in_stock NUMBER,
    stock NUMBER
  );
  TYPE product_tab_type IS TABLE OF product_rec_type INDEX BY PLS_INTEGER;
  product_tab product_tab_type;
BEGIN
  -- Populate the product_tab array. Replace this with your data generation logic.
FOR i IN 1..200 LOOP
    product_tab(i).name := 'Product ' || i;
    product_tab(i).description := 'Generic description for product ' || i;
    product_tab(i).price := ROUND(DBMS_RANDOM.VALUE(10, 1000), 2);
    product_tab(i).in_stock := 1;
    product_tab(i).stock := FLOOR(DBMS_RANDOM.VALUE(50, 500));
END LOOP;


  FORALL i IN 1 .. product_tab.COUNT
    INSERT INTO PRODUCT (NAME, DESCRIPTION, PRICE, IN_STOCK, STOCK)
    VALUES (product_tab(i).name, product_tab(i).description, product_tab(i).price, product_tab(i).in_stock, product_tab(i).stock);

COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('Error inserting data: ' || SQLERRM);
ROLLBACK;
END;
/

