using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Net;

namespace HWUPortal
{

    public class UserSession
    {
        private String userIdentity;

        //item1: name of service
        //item2: remote location
        //item3: local drive location (set after downloading the file)
        private List<ServiceInfo> services;
        private IPAddress userIPAddress;
        

        public UserSession()
        {
            this.userIdentity = string.Empty;
            services = new List<ServiceInfo>();
            
        }
        public UserSession(IPAddress ipAddr)
        {
            this.userIdentity = string.Empty;
            services = new List<ServiceInfo>();
            this.userIPAddress = ipAddr;

        }
        public void setUserIdentity(String identity)
        {
            this.userIdentity = identity;
        }

        public String getUserIdentity()
        {
            return this.userIdentity;
        }


        public void addService(String name, String service, String locationInDrive)
        {

            ServiceInfo sInfo = new ServiceInfo();
            sInfo.serviceName = name;
            sInfo.serviceURL = service;
            sInfo.serviceExe = locationInDrive;
            this.services.Add(sInfo);
        }

        public void addService(ServiceInfo sInfo)
        {
            this.services.Add(sInfo);
        }
        public void setLocalPathToExe(String serviceName, String path)
        {
            foreach (ServiceInfo s in services)
            {
                if (s.serviceName.ToLower().Equals(serviceName.ToLower()))
                {
                    s.serviceExe = path;
                }
            }
        }
        public void setButton(String serviceName, Button button)
        {
            foreach (ServiceInfo s in services){
                if (s.serviceName.ToLower().Equals(serviceName.ToLower())){
                    s.button = button;
                }
            }
        }

        public List<ServiceInfo> getServices()
        {
            return this.services;
        }

        public ServiceInfo getService(String serviceName)
        {
            foreach (ServiceInfo sInfo in services)
            {
                if (sInfo.serviceName.ToLower().Equals(serviceName.ToLower()))
                {
                    return sInfo;
                }
            }

            return null;
        }

        internal IPAddress getIPAddress()
        {
            return this.userIPAddress;
        }

        public void setIPAddress(IPAddress userIPAddr)
        {
            this.userIPAddress = userIPAddr;
        }
    }
}
