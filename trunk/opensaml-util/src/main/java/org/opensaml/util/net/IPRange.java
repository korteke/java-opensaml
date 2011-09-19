/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.util.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.BitSet;

import org.opensaml.util.StringSupport;

/** Represents a range of IP addresses. */
public class IPRange {

    /** Number of bits within the address.  32 bits for IPv4 address, 128 bits for IPv6 addresses. */
    private int addressLength;

    /** The IP network address for the range. */
    private BitSet network;

    /** The netmask for the range. */
    private BitSet mask;

    /**
     * Constructor.
     * 
     * @param networkAddress the network address for the range
     * @param maskSize the number of bits in the netmask
     */
    public IPRange(InetAddress networkAddress, int maskSize) {
        this(networkAddress.getAddress(), maskSize);
    }

    /**
     * Constructor.
     * 
     * @param networkAddress the network address for the range
     * @param maskSize the number of bits in the netmask
     */
    public IPRange(byte[] networkAddress, int maskSize) {
        addressLength = networkAddress.length * 8;
        if (addressLength != 32 && addressLength != 128) {
            throw new IllegalArgumentException("Network address was neither an IPv4 or IPv6 address");
        }

        network = toBitSet(networkAddress);
        mask = new BitSet(addressLength);
        mask.set(addressLength - maskSize, addressLength, true);
    }

    /**
     * Validate an IPv4 address for use as the base of a CIDR block.
     * 
     * Throws IllegalArgumentException if validation fails.
     * 
     * @param address the address to validate
     */
    private static void validateV4Address(final String address) {
        String[] components = address.split("\\.");
        if (components.length != 4) {
            throw new IllegalArgumentException("IPv4 address should have four components");
        }
        for (String component : components) {
            int value = Integer.parseInt(component, 10);
            if (value < 0 || (value > 255)) {
                throw new IllegalArgumentException("IPv4 component range error: " + component);
            }
        }
    }
    
    /**
     * Validate an IPv6 address for use as the base of a CIDR block.
     * 
     * Throws IllegalArgumentException if validation fails.
     * 
     * @param address the address to validate
     */
    private static void validateV6Address(final String address) {
        String[] components = address.split(":");
        if (components.length != 8) {
            throw new IllegalArgumentException("IPv6 address should have eight components");
        }
        for (String component : components) {
            int value = Integer.parseInt(component, 16);
            if (value < 0 || (value > 0xFFFF)) {
                throw new IllegalArgumentException("IPv6 component range error: " + component);
            }
        }
    }
    
    /**
     * Validate an IP address for use as the base of a CIDR block.
     * 
     * Throws IllegalArgumentException if validation fails.
     * 
     * @param address the address to validate
     */
    private static void validateIPAddress(final String address) {
        // any colons mean a V6 address, otherwise V4
        if (address.indexOf(':') >= 0) {
            validateV6Address(address);
        } else {
            validateV4Address(address);
        }
    }
    
    /**
     * Parses a CIDR block definition in to an IP range.
     * 
     * @param cidrBlock the CIDR block definition
     * 
     * @return the resultant IP range
     */
    public static IPRange parseCIDRBlock(String cidrBlock) {
        String block = StringSupport.trimOrNull(cidrBlock);
        if (block == null) {
            throw new IllegalArgumentException("CIDR block definition may not be null");
        }

        String[] blockParts = block.split("/");
        try {
            validateIPAddress(blockParts[0]);
            InetAddress networkAddress = InetAddress.getByName(blockParts[0]);
            int maskSize = Integer.parseInt(blockParts[1]);
            return new IPRange(networkAddress, maskSize);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Invalid IP address");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid netmask size");
        }
    }

    /**
     * Determines whether the given address is contained in the IP range.
     * 
     * @param address the address to check
     * 
     * @return true if the address is in the range, false it not
     */
    public boolean contains(InetAddress address) {
        return contains(address.getAddress());
    }

    /**
     * Determines whether the given address is contained in the IP range.
     * 
     * @param address the address to check
     * 
     * @return true if the address is in the range, false it not
     */
    public boolean contains(byte[] address) {
        if (address.length * 8 != addressLength) {
            return false;
        }

        BitSet addrNetwork = toBitSet(address);
        addrNetwork.and(mask);

        return addrNetwork.equals(network);
    }

    /**
     * Converts a byte array to a BitSet.
     * 
     * The supplied byte array is assumed to have the most significant bit in element 0.
     * 
     * @param bytes the byte array with most significant bit in element 0.
     * 
     * @return the BitSet
     */
    protected BitSet toBitSet(byte[] bytes) {
        BitSet bits = new BitSet(bytes.length * 8);

        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                bits.set(i);
            }
        }

        return bits;
    }
}