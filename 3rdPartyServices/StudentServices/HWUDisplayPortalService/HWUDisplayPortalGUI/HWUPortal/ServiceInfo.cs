using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
namespace HWUPortal
{
    public class ServiceInfo
    {
        public String serviceName;
        public String serviceExe;
        public String serviceURL;
        public Button button;
        public ServiceType serviceType;

        public ServiceInfo()
        {
            this.serviceName = string.Empty;
            this.serviceExe = string.Empty;
            this.serviceURL = string.Empty;
            this.serviceType = ServiceType.EXE;
        }
        public override String ToString()
        {
            return "ServiceName: " + serviceName + "\n" +
                "ServiceExe: " + serviceExe + "\n" +
                "Service URL " + serviceURL + "\n" +
                "Service Type: " + serviceType + "\n";
                //"Button: " + button.Name;
        }
    }

    public enum ServiceType{
        WEB, EXE, JAR
    }
}
