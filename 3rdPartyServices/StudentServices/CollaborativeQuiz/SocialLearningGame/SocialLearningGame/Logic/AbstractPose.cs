/* 
 * Copyright (coffee) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

using System;
using Microsoft.Kinect;

namespace SocialLearningGame.Logic
{
    public abstract class AbstractPose
    {
        public int ID { get; protected set; }
        public String Name { get; protected set; }
        public Uri ImageUri { get; protected set; }

        protected Joint head;
        
        protected Joint handLeft;
        protected Joint handRight;
        protected Joint wristLeft;
        protected Joint wristRight;
        protected Joint elbowLeft;
        protected Joint elbowRight;

        protected Joint hipLeft;
        protected Joint hipRight;
        protected Joint kneeLeft;
        protected Joint kneeRight;
        protected Joint ankleLeft;
        protected Joint ankleRight;

        protected AbstractPose(int id, String name, Uri imageUri)
        {
            this.ID = id;
            this.Name = name;
            this.ImageUri = imageUri;
        }

        public void SetCurrentJoints(Joint head,
            Joint handLeft, Joint handRight, Joint wristLeft, Joint wristRight, Joint elbowLeft, Joint elbowRight,
            Joint hipLeft, Joint hipRight, Joint kneeLeft, Joint kneeRight, Joint ankleLeft, Joint ankleRight)
        {
            this.head = head;
            this.handLeft = handLeft;
            this.handRight = handRight;
            this.wristLeft = wristLeft;
            this.wristRight = wristRight;
            this.elbowLeft = elbowLeft;
            this.elbowRight = elbowRight;

            this.hipLeft = hipLeft;
            this.hipRight = hipRight;
            this.kneeLeft = kneeLeft;
            this.kneeRight = kneeRight;
            this.ankleLeft = ankleLeft;
            this.ankleRight = ankleRight;
        }

        public virtual bool IsHeadCorrect() { return true; }

        public virtual bool IsHandLeftCorrect() { return true; }
        public virtual bool IsHandRightCorrect() { return true; }
        public virtual bool IsWristLeftCorrect() { return true; }
        public virtual bool IsWristRightCorrect() { return true; }
        public virtual bool IsElbowLeftCorrect() { return true; }
        public virtual bool IsElbowRightCorrect() { return true; }

        public virtual bool IsHipLeftCorrect() { return true; }
        public virtual bool IsHipRightCorrect() { return true; }
        public virtual bool IsKneeLeftCorrect() { return true; }
        public virtual bool IsKneeRightCorrect() { return true; }
        public virtual bool IsAnkleLeftCorrect() { return true; }
        public virtual bool IsAnkleRightCorrect() { return true; }


        public bool IsPoseValid()
        {
            return IsHeadCorrect()
                && IsHandLeftCorrect()
                && IsHandRightCorrect()
                && IsWristLeftCorrect()
                && IsWristRightCorrect()
                && IsElbowLeftCorrect()
                && IsElbowRightCorrect()

                && IsHipLeftCorrect()
                && IsHipRightCorrect()
                && IsKneeLeftCorrect()
                && IsKneeRightCorrect()
                && IsAnkleLeftCorrect()
                && IsAnkleRightCorrect()

                ;
        }


    }
}
