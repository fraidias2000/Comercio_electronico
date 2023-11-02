using System.Web.Http;
using Braintree;
using Microsoft.Azure.Mobile.Server.Config;
using servidorProyectoComercioElecronico.DataObjects;

namespace servidorProyectoComercioElecronico.Controllers
{
    [MobileAppController]
    public class GetTokenController : ApiController
    {
        private string ERROR = "error";
        private string ERROR_CREAR_USUARIO = "Error al crear usuario";
        // GET api/GetToken
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/GetToken
        public string Post(Usuario usuario)
        {
            string identificadorUsuario = "";
            var gateway = new BraintreeGateway
            {
                Environment = Braintree.Environment.SANDBOX,
                MerchantId = "msmfht5pgkfgvd4s",
                PublicKey = "fgytx2dmkptd8nh8",
                PrivateKey = "a5273c34591d0fc0f52aeb956cc0f18d"
            };


            identificadorUsuario = CrearCustomer(usuario, gateway);
            if (identificadorUsuario.Equals(ERROR))
            {
                return GenerarTokenPorDefecto(gateway);
            }
            else
            {
                return GenerarToken(identificadorUsuario, gateway);
            }
        }

        private string CrearCustomer(Usuario customerUser, BraintreeGateway gateway)
        {
            var request = new Braintree.CustomerRequest
            {
                FirstName = customerUser.nombre,
                LastName = customerUser.apellidos,
                Email = customerUser.email
            };
            Result<Customer> result = gateway.Customer.Create(request);

            if (result.IsSuccess())
            {
                return result.Target.Id;
            }
            else
            {
                return ERROR;
            }

        }
        private string GenerarToken(string idUser, BraintreeGateway gateway)
        {
            var clientToken = gateway.ClientToken.Generate(
                new ClientTokenRequest
                {
                    CustomerId = idUser
                }
            );
            return clientToken;

        }
        private string GenerarTokenPorDefecto(BraintreeGateway gateway)
        {
            return gateway.ClientToken.Generate();

        }
    }
}
