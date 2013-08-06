using System;
using Microsoft.Kinect.Toolkit.Controls;

namespace HWUPortal
{
    public class ServiceInfo
    {
        public String serviceName;
        public String serviceExe;
        public String serviceURL;
        public KinectCircleButton button;
        public ServiceType serviceType;
        public Boolean requiresKinect;
        public int servicePortNumber;

        public ServiceInfo()
        {
            this.serviceName = string.Empty;
            this.serviceExe = string.Empty;
            this.serviceURL = string.Empty;
            this.serviceType = ServiceType.EXE;
            this.requiresKinect = false;

        }
        public override String ToString()
        {
            return "ServiceName: " + serviceName + "\n" +
                "ServiceExe: " + serviceExe + "\n" +
                "Service URL " + serviceURL + "\n" +
                "Service Type: " + serviceType + "\n" +
                "Service Port Number" + servicePortNumber + "\n";
            //"Button: " + button.Name;
        }
    }

    public enum ServiceType
    {
        WEB, EXE, JAR
    }
}
