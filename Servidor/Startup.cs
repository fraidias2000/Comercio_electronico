using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(ServerComercioElectronico.Startup))]

namespace ServerComercioElectronico
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}