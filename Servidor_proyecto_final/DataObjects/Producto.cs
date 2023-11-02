using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace servidorProyectoComercioElecronico.DataObjects
{
    public class Producto
    {
        public int id_producto { get; set; }
        public string nombre { get; set; }
        public double precio { get; set; }

        public string descripcion { get; set; }

        public int cantidadProducto { get; set; }
    }
}