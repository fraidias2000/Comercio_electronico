using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Web.Http;
using Microsoft.Azure.Mobile.Server.Config;
using servidorProyectoComercioElecronico.DataObjects;
using servidorProyectoComercioElecronico.Models;

namespace servidorProyectoComercioElecronico.Controllers
{
    [MobileAppController]
    public class InsertarUsuarioController : ApiController
    {
        // GET api/InsertarUsuario
        public string Get()
        {
           
                return "Hello from custom controller!";
        }

        //POST api/InsertarUsuario
        public Usuario Post(Usuario usuarioInsertado) {
            MobileServiceContext context = new MobileServiceContext();
             var resultado = context.Database.ExecuteSqlCommand("INSERT INTO usuarios(nombre, apellidos, email, contrasenia) " +
                 "VALUES (@nombre, @apellidos, @email, @contrasenia)",
                  new SqlParameter("@nombre", usuarioInsertado.nombre),
                  new SqlParameter("@apellidos", usuarioInsertado.apellidos),
                  new SqlParameter("@email", usuarioInsertado.email),
                  new SqlParameter("@contrasenia", usuarioInsertado.contrasenia));

             List<Usuario> ListasUsuarios = context.Database.SqlQuery<Usuario>("SELECT * FROM usuarios WHERE nombre = @nombre AND contrasenia = @contrasenia",
                                                                             new SqlParameter("@nombre", usuarioInsertado.nombre),
                                                                             new SqlParameter("@contrasenia", usuarioInsertado.contrasenia)).ToList();

             if ((ListasUsuarios != null) || (ListasUsuarios.Count > 0))
             {
                 return ListasUsuarios[0];
             }
             else
             {
                 return null;
             }
           
        }
    }
}
