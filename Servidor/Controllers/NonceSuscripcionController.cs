using System.Web.Http;
using Braintree;
using Microsoft.Azure.Mobile.Server.Config;
using ServerComercioElectronico.DataObjects;

namespace ServerComercioElectronico.Controllers
{
    [MobileAppController]
    public class NonceSuscripcionController : ApiController
    {
        // GET api/NonceSuscripcion
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/NonceSuscripcion
        public string Post(TokenNonceSuscripcion nonceSuscripcion)
        {
            var gateway = new BraintreeGateway
            {
                Environment = Braintree.Environment.SANDBOX,
                MerchantId = "t6xvwsnv3vjj8kkb",
                PublicKey = "gcnpgpx42pz8mh2z",
                PrivateKey = "f9d774c6ccb8c5383d1443a1e3bd4f16"
            };       

            var request = new SubscriptionRequest
            {
                PaymentMethodToken = nonceSuscripcion.identificadorCompra,
                PlanId = nonceSuscripcion.tipoSuscripcion,
                AddOns = new AddOnsRequest
                {
                    Add = new AddAddOnRequest[]
                    {
                        new AddAddOnRequest
                    {
                        InheritedFromId = "mensual",
                        Amount = 5
                    },
                    new AddAddOnRequest
                    {
                        InheritedFromId = "anual",
                        Amount = 50
                    }
                    }
                }
            };
            Result <Subscription> result = gateway.Subscription.Create(request);
            if (result.IsSuccess())
            {
                Subscription suscripcion = result.Target;
                SubscriptionStatus status = suscripcion.Status;              
                return "ok";
            }
            else
            {
                return "Error en la transaccion";
            }
        }
    }
}
