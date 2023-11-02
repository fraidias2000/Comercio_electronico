using System.Web.Http;
using Braintree;
using Microsoft.Azure.Mobile.Server.Config;
using ServerComercioElectronico.DataObjects;

namespace ServerComercioElectronico.Controllers
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
        public string Post(CustomerUser cliente)
        {
            string identificadorUsuario = "";
            var gateway = new BraintreeGateway
            {
                Environment = Braintree.Environment.SANDBOX,
                MerchantId = "t6xvwsnv3vjj8kkb",
                PublicKey = "gcnpgpx42pz8mh2z",
                PrivateKey = "f9d774c6ccb8c5383d1443a1e3bd4f16"
            };
            identificadorUsuario = CrearCustomer(cliente, gateway);
            if (identificadorUsuario.Equals(ERROR))
            {
                return GenerarTokenPorDefecto(gateway);
            }
            else {
                return GenerarToken(identificadorUsuario, gateway);
            }
        }

        private string CrearCustomer(CustomerUser customerUser, BraintreeGateway gateway) {
            var request = new Braintree.CustomerRequest
            {
                FirstName = customerUser.nombre,
                LastName = customerUser.apellidos,
                Email = customerUser.correo
            };
            Result<Customer> result = gateway.Customer.Create(request);

            if (result.IsSuccess())
            {
                return result.Target.Id;
            }
            else {
                return ERROR;
            }
            
        }
        private string GenerarToken(string idUser, BraintreeGateway gateway) {
            var clientToken = gateway.ClientToken.Generate(
                new ClientTokenRequest {
                    CustomerId = idUser
                }
            );
            return clientToken;

        }
        private string GenerarTokenPorDefecto(BraintreeGateway gateway) {
            return gateway.ClientToken.Generate();
             
        }
    }
}
