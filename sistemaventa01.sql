-- Base de datos: `sistemaventa`
IF DB_ID('sistemaventa') IS NULL
    CREATE DATABASE sistemaventa;
GO

USE sistemaventa;
GO

-- Iniciar una transacción para mayor control
BEGIN TRANSACTION;

-- Estructura de tabla para la tabla `clientes`
CREATE TABLE clientes (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  dni VARCHAR(8) NOT NULL,
  nombre VARCHAR(180) NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  direccion VARCHAR(255) NOT NULL,
  CONSTRAINT CK_dni CHECK (LEN(dni) = 8),
  CONSTRAINT CK_telefono CHECK (LEN(telefono) <= 15)
);

-- Volcado de datos para la tabla `clientes`
INSERT INTO clientes (dni, nombre, telefono, direccion) VALUES
('61474046', 'Kevin Estrada', '918904971', 'Casma - Perú');

-- Estructura de tabla para la tabla `config`
CREATE TABLE config (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ruc BIGINT NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  telefono BIGINT NOT NULL,
  direccion TEXT NOT NULL,
  mensaje VARCHAR(255) NOT NULL,
  CONSTRAINT CK_ruc CHECK (ruc BETWEEN 10000000000 AND 99999999999),
  CONSTRAINT CK_telefono_config CHECK (telefono BETWEEN 100000000 AND 999999999)
);

-- Volcado de datos para la tabla `config`
INSERT INTO config (ruc, nombre, telefono, direccion, mensaje) VALUES
(71347267, 'Ruby Cruz', 925491524, 'Chimbote - Perú', 'Base de datos');

-- Estructura de tabla para la tabla `detalle`
CREATE TABLE detalle (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  id_pro INT NOT NULL,
  cantidad INT NOT NULL,
  precio DECIMAL(10, 2) NOT NULL,
  id_venta INT NOT NULL,
  CONSTRAINT CK_precio CHECK (precio >= 0),
  CONSTRAINT CK_cantidad CHECK (cantidad >= 0)
);

-- Volcado de datos para la tabla `detalle`
INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES
(1, 2, 3000.00, 8),
(1, 2, 3000.00, 9),
(1, 2, 3000.00, 10),
(1, 2, 3000.00, 11),
(1, 2, 3000.00, 12),
(1, 2, 3000.00, 13),
(1, 2, 3000.00, 14),
(1, 2, 3000.00, 15);

-- Estructura de tabla para la tabla `productos`
CREATE TABLE productos (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  codigo VARCHAR(20) NOT NULL,
  nombre TEXT NOT NULL,
  proveedor INT NOT NULL,
  stock INT NOT NULL,
  precio DECIMAL(10, 2) NOT NULL,
  CONSTRAINT CK_precio_productos CHECK (precio >= 0),
  CONSTRAINT CK_stock CHECK (stock >= 0)
);

-- Volcado de datos para la tabla `productos`
INSERT INTO productos (codigo, nombre, proveedor, stock, precio) VALUES
('1', 'Laptop lenovo', 1, 7, 3000.00),
('2', 'Iphone 13', 1, 10, 4500.00);

-- Estructura de tabla para la tabla `proveedor`
CREATE TABLE proveedor (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  ruc VARCHAR(15) NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  direccion VARCHAR(255) NOT NULL,
  CONSTRAINT CK_ruc_proveedor CHECK (LEN(ruc) <= 15),
  CONSTRAINT CK_telefono_proveedor CHECK (LEN(telefono) <= 15)
);

-- Volcado de datos para la tabla `proveedor`
INSERT INTO proveedor (ruc, nombre, telefono, direccion) VALUES
('998787', 'Open Services', '798978879', 'Chimbote - Perú'),
('124738', 'Hábitat Eléctrico', '1839457389', 'Chimbote - Perú');

-- Estructura de tabla para la tabla `usuarios`
CREATE TABLE usuarios (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  nombre VARCHAR(200) NOT NULL,
  correo VARCHAR(200) NOT NULL,
  pass VARCHAR(50) NOT NULL,
  rol VARCHAR(20) NOT NULL,
  CONSTRAINT CK_rol CHECK (rol IN ('Administrador', 'Usuario'))
);

-- Volcado de datos para la tabla `usuarios`
INSERT INTO usuarios (nombre, correo, pass, rol) VALUES
('Kevin Estrada', 'kevinestrada2103@gmail.com', 'admin', 'Administrador');

-- Estructura de tabla para la tabla `ventas`
CREATE TABLE ventas (
  id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
  cliente INT NOT NULL,
  vendedor VARCHAR(60) NOT NULL,
  total DECIMAL(10, 2) NOT NULL,
  fecha DATE NOT NULL,
  CONSTRAINT CK_total CHECK (total >= 0)
);

-- Volcado de datos para la tabla `ventas`
INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18'),
(1, 'Kevin Estrada', 6000.00, '2024-07-18');

-- Índices para tablas volcadas (además de los PRIMARY KEY ya definidos)
CREATE INDEX idx_detalle_id_venta ON detalle(id_venta);
CREATE INDEX idx_detalle_id_pro ON detalle(id_pro);
CREATE INDEX idx_productos_proveedor ON productos(proveedor);
CREATE INDEX idx_ventas_cliente ON ventas(cliente);

-- Restricciones para tablas volcadas
ALTER TABLE detalle
  ADD CONSTRAINT FK_detalle_productos FOREIGN KEY (id_pro) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE;
  
ALTER TABLE detalle
  ADD CONSTRAINT FK_detalle_ventas FOREIGN KEY (id_venta) REFERENCES ventas(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE productos
  ADD CONSTRAINT FK_productos_proveedor FOREIGN KEY (proveedor) REFERENCES proveedor(id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE ventas
  ADD CONSTRAINT FK_ventas_clientes FOREIGN KEY (cliente) REFERENCES clientes(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Finalizar la transacción
COMMIT TRANSACTION;
GO
