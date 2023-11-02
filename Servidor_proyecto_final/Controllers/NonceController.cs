using System.Web.Http;
using Braintree;
using Microsoft.Azure.Mobile.Server.Config;
using servidorProyectoComercioElecronico.DataObjects;

namespace servidorProyectoComercioElecronico.Controllers
{
    [MobileAppController]
    public class NonceController : ApiController
    {
        private double valor = 0;
        // GET api/Nonce
        public string Get()
        {
            return "Hello from custom controller!";
        }
        // POST api/Nonce
        public string Post(TokenNonce nonceCompra)
        {
            int valor = (int) nonceCompra.precio;
            if (nonceCompra == null)
            {
                return "nonce nulo";
            }
            else
            {        
                if (valor == null)
                {
                    valor = 0;
                }
                var gateway = new BraintreeGateway
                {
                    Environment = Braintree.Environment.SANDBOX,
                    MerchantId = "msmfht5pgkfgvd4s",
                    PublicKey = "fgytx2dmkptd8nh8",
                    PrivateKey = "a5273c34591d0fc0f52aeb956cc0f18d"
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
