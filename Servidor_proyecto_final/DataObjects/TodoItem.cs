using Microsoft.Azure.Mobile.Server;

namespace servidorProyectoComercioElecronico.DataObjects
{
    public class TodoItem : EntityData
    {
        public string Text { get; set; }

        public bool Complete { get; set; }
    }
}