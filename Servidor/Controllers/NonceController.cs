using System.Web.Http;
using Braintree;
using Microsoft.Azure.Mobile.Server.Config;
using ServerComercioElectronico.DataObjects;

namespace ServerComercioElectronico.Controllers
{
    [MobileAppController]
    public class NonceController : ApiController
    {
        private int precioProducto = 3;
        private int valor = 0;
        // GET api/Nonce
        public string Get()
        {
            return "Hello from custom controller!";
        }

        // POST api/Nonce
        public string Post(TokenNonce  nonceCompra)
        {
            
            if (nonceCompra == null)
            {
                return "nonce nulo";
            }
            else {
                valor = nonceCompra.cantidadProducto * precioProducto;
                if (valor == null) {
                    valor = 0;
                }
                var gateway = new BraintreeGateway
                {
                    Environment = Braintree.Environment.SANDBOX,
                    MerchantId = "t6xvwsnv3vjj8kkb",
                    PublicKey = "gcnpgpx42pz8mh2z",
                    PrivateKey = "f9d774c6ccb8c5383d1443a1e3bd4f16"
                };

                var nuevaTransaccion = new TransactionRequest
                {
                    Amount = valor,
                    PaymentMethodNonce = nonceCompra.identificadorCompra,  //VISA
                    Options = new TransactionOptionsRequest
                    {
                        SubmitForSettlement = true
                    }
                };

                Result<Transaction> result = gateway.Transaction.Sale(nuevaTransaccion);
                if (result.IsSuccess())
                {
                    Transaction transaction = result.Target;
                    TransactionStatus status = transaction.Status;                   
                    return "ok";
                }
                else
                {
                    return "Error en la transaccion";
                }
               
            }
            
        }
    }
}
