using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace servidorProyectoComercioElecronico.DataObjects
{
    public class TokenNonce
    {
        public string identificadorCompra { get; set; }
        public double precio { get; set; }
    }
}