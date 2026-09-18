package com.merida.tecnm.market_backend_v2.persistence.entity;

import jakart.pesistance.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    private String nombre;

    @Column (name = "id_categoria")
    private Integer id_categoria;

    @Column (name = "codigo barras")
    private String codigoBarras;

    @column (name= "precio venta")
    private Double precioVenta;

    @Column (name = "cantidad stock")
    private Integer cantidadStock;
}
