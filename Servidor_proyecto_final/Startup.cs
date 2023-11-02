using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(servidorProyectoComercioElecronico.Startup))]

namespace servidorProyectoComercioElecronico
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureMobileApp(app);
        }
    }
}