using System.Data.SqlClient;
using System.Web.Http;
using Microsoft.Azure.Mobile.Server.Config;
using servidorProyectoComercioElecronico.DataObjects;
using servidorProyectoComercioElecronico.Models;

namespace servidorProyectoComercioElecronico.Controllers
{
    [MobileAppController]
    public class ActualizarProductosController : ApiController
    {
        // GET api/ActualizarProductos
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/ActualizarProductos
        public string Post(Producto producto)
        {

            MobileServiceContext context = new MobileServiceContext();
            var resultado = context.Database.ExecuteSqlCommand("UPDATE productos " +
                                                                "set cantidadProducto = @cantidadProducto " +
                                                                "where id_producto = @id_producto) ",
                 new SqlParameter("@cantidadProducto", producto.cantidadProducto),
                 new SqlParameter("@id_producto", producto.id_producto));

            if (resultado == null)
            {
                return "error";

            }
            else {
                return "acierto";
            }
        }
    }
}
