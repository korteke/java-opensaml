
package org.opensaml.util.net;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IPRangeTest {

    private void testInvalid(final String address) {
        try {
            IPRange.parseCIDRBlock(address);
            Assert.fail("address should have been invalid: " + address);
        } catch (IllegalArgumentException e) {
            // expected behaviour
            return;
        }
    }
    
    @Test
    public void validV4Addresses() {
        IPRange.parseCIDRBlock("1.2.3.4/32");
        IPRange.parseCIDRBlock("0.0.0.0/8");
        IPRange.parseCIDRBlock("0.0.0.0/0");
    }
    
    @Test
    public void validV6Addresses() {
        IPRange.parseCIDRBlock("0:0:0:0:0:0:0:0/128");
        IPRange.parseCIDRBlock("0:0:0:0:0:0:0:0/0");
        IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/128");
        IPRange.parseCIDRBlock("1234:5678::BBBB:CCCC/128");
        IPRange.parseCIDRBlock("2001:630:200::/48");
        IPRange.parseCIDRBlock("::0BAD:7/128");
    }
    
    @Test
    public void invalidJunkAddresses() {
        testInvalid("/32");
        testInvalid("f/32");
    }
    
    @Test
    public void invalidV4Addresses() {
        testInvalid("1/32");
        testInvalid("1.2/32");
        testInvalid("1.2.3/32");
        testInvalid("1.2.3.4/33");
        testInvalid("1.2.3.4/-3");
        testInvalid("1.2.3.4/wrong");
    }
    
    @Test
    public void invalidV6Addresses() {
        testInvalid("0:0/128");
        testInvalid("1:2:3:4:5:6:7/128");
        testInvalid("::0BAD::7/128");
        testInvalid("1:2:3:4:5:6:7:8/-5");
        testInvalid("1:2:3:4:5:6:7:8/129");
        testInvalid("1:2:3:4:5:6:7:8/wrong");
    }

    @Test
    public void contains() {
        // IPRange given a network address
        IPRange networkRange = IPRange.parseCIDRBlock("192.168.117.192/28");
        
        // IPRange given a host address
        IPRange hostRange = IPRange.parseCIDRBlock("192.168.117.199/28");
        
        // test for contain
        byte[] bytes = new byte[]{(byte)192, (byte)168, 117, (byte)191};
        Assert.assertFalse(networkRange.contains(bytes));
        Assert.assertFalse(hostRange.contains(bytes));
        
        for (int host = 0; host < 16; host++) {
            bytes[3] = (byte)(192+host);
            Assert.assertTrue(networkRange.contains(bytes));
            Assert.assertTrue(hostRange.contains(bytes));
        }
        
        bytes[3] = (byte)(192+16);
        Assert.assertFalse(networkRange.contains(bytes));
        Assert.assertFalse(hostRange.contains(bytes));
    }
    
    @Test
    public void getNetworkAddress() {
        IPRange v6a = IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/128");
        byte[] expected6a = {
                (byte)0x12,
                (byte)0x34,
                (byte)0x56,
                (byte)0x78,
                (byte)0x90,
                (byte)0xab,
                (byte)0xcd,
                (byte)0xef,
                (byte)0xff,
                (byte)0xff,
                (byte)0xaa,
                (byte)0xaa,
                (byte)0xbb,
                (byte)0xbb,
                (byte)0xcc,
                (byte)0xcc,
        };
        Assert.assertEquals(v6a.getNetworkAddress().getAddress(), expected6a);
        
        IPRange v6b = IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/104");
        byte[] expected6b = {
                (byte)0x12,
                (byte)0x34,
                (byte)0x56,
                (byte)0x78,
                (byte)0x90,
                (byte)0xab,
                (byte)0xcd,
                (byte)0xef,
                (byte)0xff,
                (byte)0xff,
                (byte)0xaa,
                (byte)0xaa,
                (byte)0xbb,
                (byte)0x00,
                (byte)0x00,
                (byte)0x00,
        };
        Assert.assertEquals(v6b.getNetworkAddress().getAddress(), expected6b);

        IPRange v4a = IPRange.parseCIDRBlock("192.168.117.17/32");
        byte[] expected4a = {
                (byte)192, (byte)168, (byte)117, (byte)17
        };
        Assert.assertEquals(v4a.getNetworkAddress().getAddress(), expected4a);

        IPRange v4b = IPRange.parseCIDRBlock("192.168.117.17/16");
        byte[] expected4b = {
                (byte)192, (byte)168, (byte)0, (byte)0
        };
        Assert.assertEquals(v4b.getNetworkAddress().getAddress(), expected4b);
    }
    
    @Test
    public void getHostAddress() {
        IPRange v6a = IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/128");
        Assert.assertNull(v6a.getHostAddress());

        IPRange v6b = IPRange.parseCIDRBlock("1234:5678:90ab:cdef::/64");
        Assert.assertNull(v6b.getHostAddress());
        
        IPRange v6c = IPRange.parseCIDRBlock("1234:5678:90ab:cdef:FfFf:AaAa:BBBB:CCCC/64");
        Assert.assertNotNull(v6c.getHostAddress());
        Assert.assertEquals(v6c.getHostAddress().getAddress(), v6a.getNetworkAddress().getAddress());
        
        IPRange v4a = IPRange.parseCIDRBlock("192.168.117.17/32");
        Assert.assertNull(v4a.getHostAddress());

        IPRange v4b = IPRange.parseCIDRBlock("192.168.0.0/16");
        Assert.assertNull(v4b.getHostAddress());
        
        IPRange v4c = IPRange.parseCIDRBlock("192.168.117.17/16");
        Assert.assertNotNull(v4c.getHostAddress());
        Assert.assertEquals(v4c.getHostAddress().getAddress(), v4a.getNetworkAddress().getAddress());
    }
    
}
