/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:58 LKT)
 */
package com.db4o.omplus.ws;


/**
 *  ExtensionMapper class
 */
public class ExtensionMapper {
    public static java.lang.Object getTypeObject(
        java.lang.String namespaceURI, java.lang.String typeName,
        javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
        if ("http://db4o.com/User".equals(namespaceURI) &&
                "UserInfo".equals(typeName)) {
            return com.db4o.omplus.ws.UserInfo.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "ArrayOfFeaturePermission".equals(typeName)) {
            return com.db4o.omplus.ws.ArrayOfFeaturePermission.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "LoginNotice".equals(typeName)) {
            return com.db4o.omplus.ws.LoginNotice.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "ArrayOfString".equals(typeName)) {
            return com.db4o.omplus.ws.ArrayOfString.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "ArrayOfString1".equals(typeName)) {
            return com.db4o.omplus.ws.ArrayOfString1.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "FeaturePermission".equals(typeName)) {
            return com.db4o.omplus.ws.FeaturePermission.Factory.parse(reader);
        }

        if ("http://db4o.com/User".equals(namespaceURI) &&
                "SeatAuthorization".equals(typeName)) {
            return com.db4o.omplus.ws.SeatAuthorization.Factory.parse(reader);
        }

        throw new org.apache.axis2.databinding.ADBException("Unsupported type " +
            namespaceURI + " " + typeName);
    }
}
