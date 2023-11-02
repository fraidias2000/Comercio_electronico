using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace servidorProyectoComercioElecronico.DataObjects
{
    public class Usuario
    {
        public int id { get; set; }
        public string nombre { get; set; }
        public string apellidos { get; set; }
        public string email { get; set; }
        public string contrasenia { get; set; }
    }
}