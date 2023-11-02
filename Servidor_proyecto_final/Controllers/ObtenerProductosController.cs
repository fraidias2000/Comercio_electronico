using System.Collections.Generic;
using System.Linq;
using System.Web.Http;
using Microsoft.Azure.Mobile.Server.Config;
using servidorProyectoComercioElecronico.DataObjects;
using servidorProyectoComercioElecronico.Models;

namespace servidorProyectoComercioElecronico.Controllers
{
    [MobileAppController]
    public class ObtenerProductosController : ApiController
    {
        // GET api/ObtenerProductos
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/ObtenerProductos
        public ListaProductos Post(Mensaje mensaje)
        {

            ListaProductos listaProductos = new ListaProductos();
            //Producto productoAux;
            MobileServiceContext context = new MobileServiceContext();
            List<Producto> listaBD = context.Database.SqlQuery<Producto>("SELECT * FROM productos;").ToList();

            listaProductos.misProductos = listaBD;
            return listaProductos;
        }
    }
    
}
