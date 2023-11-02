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
    public class LoginUsuarioController : ApiController
    {
        // GET api/LoginUsuario
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/LoginUsuario
        public Usuario Post(Usuario usuario)
        {

            MobileServiceContext context = new MobileServiceContext();
            List<Usuario> ListasUsuarios = context.Database.SqlQuery<Usuario>("SELECT * FROM usuarios WHERE nombre = @nombre AND contrasenia = @contrasenia",
                                                                            new SqlParameter("@nombre", usuario.nombre),
                                                                            new SqlParameter("@contrasenia", usuario.contrasenia)).ToList();

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
