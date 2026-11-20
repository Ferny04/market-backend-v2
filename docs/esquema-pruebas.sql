-- Ejecutar una sola vez en una base PostgreSQL de pruebas vacía.
-- Esquema reproducido a partir de la estructura del proyecto de clase.
CREATE TABLE categorias (
    id_categoria serial PRIMARY KEY,
    descripcion varchar(45) NOT NULL,
    estado boolean NOT NULL
);

CREATE TABLE clientes (
    id varchar(20) PRIMARY KEY,
    nombre varchar(40),
    apellidos varchar(100),
    celular numeric,
    direccion varchar(80),
    correo_electronico varchar(70)
);

CREATE TABLE productos (
    id_producto serial PRIMARY KEY,
    nombre varchar(45),
    id_categoria integer NOT NULL REFERENCES categorias(id_categoria),
    codigo_barras varchar(150),
    precio_venta numeric(16, 2),
    cantidad_stock integer NOT NULL,
    estado boolean
);

CREATE TABLE compras (
    id_compra serial PRIMARY KEY,
    id_cliente varchar(20) NOT NULL REFERENCES clientes(id),
    fecha timestamp,
    medio_pago char(1),
    comentario varchar(300),
    estado char(1)
);

CREATE TABLE compras_productos (
    id_compra integer NOT NULL REFERENCES compras(id_compra),
    id_producto integer NOT NULL REFERENCES productos(id_producto),
    cantidad integer,
    total numeric(16, 2),
    estado boolean,
    PRIMARY KEY (id_compra, id_producto)
);
